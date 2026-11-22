import { BaseProvider } from './BaseProvider';
import { AIRequest, AIResponse, ProviderHealth, TokenUsage } from '../types/provider';
import { env } from '../config/env';
import { getDefaultModel, validateZeroCostModel } from '../config/modelAllowlist';

export class GeminiProvider extends BaseProvider {
  readonly name = 'gemini';
  readonly priority = 2;

  isConfigured(): boolean {
    return Boolean(env.GEMINI_API_KEY && env.GEMINI_API_KEY.length > 5);
  }

  async generateResponse(request: AIRequest): Promise<AIResponse> {
    const startTime = Date.now();
    const apiKey = env.GEMINI_API_KEY;

    if (!apiKey) {
      throw this.classifyError(new Error('Gemini API key not configured'), 401);
    }

    const model = request.model || getDefaultModel('gemini');
    if (env.ZERO_COST_MODE) {
      validateZeroCostModel('gemini', model);
    }

    // Check cooldown
    if (this.cooldownUntil > Date.now()) {
      const waitSec = Math.ceil((this.cooldownUntil - Date.now()) / 1000);
      throw this.classifyError(new Error(`Gemini is in cooldown for ${waitSec}s due to recent rate limiting`), 429);
    }

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 20000); // 20s timeout

    // Transform messages to Gemini API format
    const contents: Array<{ role: string; parts: Array<{ text: string }> }> = [];

    for (const msg of request.messages) {
      const role = msg.role === 'assistant' ? 'model' : 'user';
      contents.push({
        role,
        parts: [{ text: msg.content }]
      });
    }

    const body: any = {
      contents,
      generationConfig: {
        maxOutputTokens: request.maxTokens || 1024,
        temperature: request.temperature ?? 0.7
      }
    };

    if (request.systemPrompt) {
      body.systemInstruction = {
        parts: [{ text: request.systemPrompt }]
      };
    }

    const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;

    try {
      const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
        signal: controller.signal
      });

      clearTimeout(timeout);
      this.parseRateLimitHeaders(res.headers);

      if (!res.ok) {
        const errorBody = await res.text().catch(() => '');
        throw this.classifyError(new Error(errorBody || `HTTP ${res.status}`), res.status);
      }

      const data = (await res.json()) as any;
      const text = data.candidates?.[0]?.content?.parts?.[0]?.text?.trim() || '';
      const latencyMs = Date.now() - startTime;

      const usage = data.usageMetadata;
      const tokens: TokenUsage = usage
        ? {
            promptTokens: usage.promptTokenCount || 0,
            completionTokens: usage.candidatesTokenCount || 0,
            totalTokens: usage.totalTokenCount || 0,
            isEstimated: false
          }
        : this.estimateTokens(JSON.stringify(contents), text);

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
    const activeModel = getDefaultModel('gemini');

    if (!this.isConfigured()) {
      return {
        name: this.name,
        priority: this.priority,
        status: 'unconfigured',
        latencyMs: 0,
        message: 'GEMINI_API_KEY is not configured in .env',
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

      const url = `https://generativelanguage.googleapis.com/v1beta/models?key=${env.GEMINI_API_KEY}`;
      const res = await fetch(url, { signal: controller.signal });

      clearTimeout(timeout);
      const latencyMs = Date.now() - startTime;

      if (res.ok) {
        return {
          name: this.name,
          priority: this.priority,
          status: 'healthy',
          latencyMs,
          message: 'Google Gemini endpoint operational',
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
