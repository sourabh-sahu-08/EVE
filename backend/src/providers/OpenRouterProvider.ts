import { BaseProvider } from './BaseProvider';
import { AIRequest, AIResponse, ProviderHealth, TokenUsage } from '../types/provider';
import { env } from '../config/env';
import { getDefaultModel, validateZeroCostModel } from '../config/modelAllowlist';

export class OpenRouterProvider extends BaseProvider {
  readonly name = 'openrouter';
  readonly priority = 3;

  isConfigured(): boolean {
    return Boolean(env.OPENROUTER_API_KEY && env.OPENROUTER_API_KEY.length > 5);
  }

  async generateResponse(request: AIRequest): Promise<AIResponse> {
    const startTime = Date.now();
    const apiKey = env.OPENROUTER_API_KEY;

    if (!apiKey) {
      throw this.classifyError(new Error('OpenRouter API key not configured'), 401);
    }

    const model = request.model || getDefaultModel('openrouter');

    // Strict zero-cost enforcement: must end with :free
    if (!model.endsWith(':free')) {
      throw new Error(`[ZeroCostGuard] OpenRouter model "${model}" is rejected: does not have ":free" suffix.`);
    }

    if (env.ZERO_COST_MODE) {
      validateZeroCostModel('openrouter', model);
    }

    // Check cooldown
    if (this.cooldownUntil > Date.now()) {
      const waitSec = Math.ceil((this.cooldownUntil - Date.now()) / 1000);
      throw this.classifyError(new Error(`OpenRouter is in cooldown for ${waitSec}s due to recent rate limiting`), 429);
    }

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 20000); // 20s timeout

    const messages = [];
    if (request.systemPrompt) {
      messages.push({ role: 'system', content: request.systemPrompt });
    }
    messages.push(...request.messages);

    try {
      const res = await fetch('https://openrouter.ai/api/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${apiKey}`,
          'HTTP-Referer': 'http://localhost:3000',
          'X-Title': 'EVE Personal AI Assistant'
        },
        body: JSON.stringify({
          model,
          messages,
          max_tokens: request.maxTokens || 1024,
          temperature: request.temperature ?? 0.7
        }),
        signal: controller.signal
      });

      clearTimeout(timeout);
      this.parseRateLimitHeaders(res.headers);

      if (!res.ok) {
        const errorBody = await res.text().catch(() => '');
        throw this.classifyError(new Error(errorBody || `HTTP ${res.status}`), res.status);
      }

      const data = (await res.json()) as any;
      const text = data.choices?.[0]?.message?.content?.trim() || '';
      const latencyMs = Date.now() - startTime;

      const tokens: TokenUsage = data.usage
        ? {
            promptTokens: data.usage.prompt_tokens || 0,
            completionTokens: data.usage.completion_tokens || 0,
            totalTokens: data.usage.total_tokens || 0,
            isEstimated: false
          }
        : this.estimateTokens(JSON.stringify(messages), text);

      return {
        text,
        provider: this.name,
        model,
        tokens,
        latencyMs,
        fallbackCount: 0,
        fallbackHistory: [],
        timestamp: new Date().toISOString()
      };
    } catch (err: unknown) {
      clearTimeout(timeout);
      throw this.classifyError(err);
    }
  }

  async checkHealth(): Promise<ProviderHealth> {
    const startTime = Date.now();
    const activeModel = getDefaultModel('openrouter');

    if (!this.isConfigured()) {
      return {
        name: this.name,
        priority: this.priority,
        status: 'unconfigured',
        latencyMs: 0,
        message: 'OPENROUTER_API_KEY is not configured in .env',
        activeModel,
        timestamp: new Date().toISOString()
      };
    }

    if (this.cooldownUntil > Date.now()) {
      const waitSec = Math.ceil((this.cooldownUntil - Date.now()) / 1000);
      return {
        name: this.name,
        priority: this.priority,
        status: 'degraded',
        latencyMs: 0,
        message: `Rate limited. In cooldown for ${waitSec}s`,
        activeModel,
        cooldownRemainingSec: waitSec,
        timestamp: new Date().toISOString()
      };
    }

    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 6000);

      const res = await fetch('https://openrouter.ai/api/v1/auth/key', {
        headers: { Authorization: `Bearer ${env.OPENROUTER_API_KEY}` },
        signal: controller.signal
      });

      clearTimeout(timeout);
      const latencyMs = Date.now() - startTime;

      if (res.ok) {
        return {
          name: this.name,
          priority: this.priority,
          status: 'healthy',
          latencyMs,
          message: 'OpenRouter Free Tier endpoint operational',
          activeModel,
          timestamp: new Date().toISOString()
        };
      } else {
        return {
          name: this.name,
          priority: this.priority,
          status: 'unavailable',
          latencyMs,
          message: `HTTP ${res.status}: ${res.statusText}`,
          activeModel,
          timestamp: new Date().toISOString()
        };
      }
    } catch (err: unknown) {
      return {
        name: this.name,
        priority: this.priority,
        status: 'unavailable',
        latencyMs: Date.now() - startTime,
        message: err instanceof Error ? err.message : String(err),
        activeModel,
        timestamp: new Date().toISOString()
      };
    }
  }
}
