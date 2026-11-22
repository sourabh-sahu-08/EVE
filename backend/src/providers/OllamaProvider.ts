import { BaseProvider } from './BaseProvider';
import { AIRequest, AIResponse, ProviderHealth, TokenUsage } from '../types/provider';
import { env } from '../config/env';
import { getDefaultModel } from '../config/modelAllowlist';

export class OllamaProvider extends BaseProvider {
  readonly name = 'ollama';
  readonly priority = 4;

  private availableModels: string[] = [];
  private lastModelCheck: number = 0;

  isConfigured(): boolean {
    return Boolean(env.OLLAMA_BASE_URL && env.OLLAMA_BASE_URL.length > 5);
  }

  async getInstalledModels(): Promise<string[]> {
    const now = Date.now();
    // Cache for 60 seconds
    if (this.availableModels.length > 0 && now - this.lastModelCheck < 60000) {
      return this.availableModels;
    }

    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 4000);

      const res = await fetch(`${env.OLLAMA_BASE_URL}/api/tags`, {
        signal: controller.signal
      });

      clearTimeout(timeout);
      if (res.ok) {
        const data = (await res.json()) as any;
        this.availableModels = (data.models || []).map((m: any) => m.name || m.model);
        this.lastModelCheck = now;
        return this.availableModels;
      }
    } catch {
      // Fallback
    }

    return this.availableModels;
  }

  async generateResponse(request: AIRequest): Promise<AIResponse> {
    const startTime = Date.now();
    const installed = await this.getInstalledModels();

    // Select installed model or fallback
    let model = request.model;
    if (!model || (installed.length > 0 && !installed.includes(model))) {
      // Prefer qwen2.5:7b, then llama3.1:latest, then first installed, then default
      if (installed.includes('qwen2.5:7b')) {
        model = 'qwen2.5:7b';
      } else if (installed.includes('llama3.1:latest')) {
        model = 'llama3.1:latest';
      } else if (installed.length > 0) {
        model = installed[0];
      } else {
        model = getDefaultModel('ollama');
      }
    }

    const messages = [];
    if (request.systemPrompt) {
      messages.push({ role: 'system', content: request.systemPrompt });
    }
    messages.push(...request.messages);

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 60000); // 60s timeout for local models

    try {
      const res = await fetch(`${env.OLLAMA_BASE_URL}/api/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          model,
          messages,
          stream: false,
          options: {
            temperature: request.temperature ?? 0.7,
            num_predict: request.maxTokens || 1024
          }
        }),
        signal: controller.signal
      });

      clearTimeout(timeout);

      if (!res.ok) {
        const errorBody = await res.text().catch(() => '');
        throw this.classifyError(new Error(errorBody || `HTTP ${res.status}`), res.status);
      }

      const data = (await res.json()) as any;
      const text = data.message?.content?.trim() || '';
      const latencyMs = Date.now() - startTime;

      const tokens: TokenUsage = {
        promptTokens: data.prompt_eval_count || Math.max(1, Math.ceil(JSON.stringify(messages).length / 4)),
        completionTokens: data.eval_count || Math.max(1, Math.ceil(text.length / 4)),
        totalTokens: (data.prompt_eval_count || 0) + (data.eval_count || 0) || Math.ceil((JSON.stringify(messages).length + text.length) / 4),
        isEstimated: !Boolean(data.prompt_eval_count && data.eval_count)
      };

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
    const activeModel = this.availableModels[0] || getDefaultModel('ollama');

    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 4000);

      const res = await fetch(`${env.OLLAMA_BASE_URL}/api/tags`, {
        signal: controller.signal
      });

      clearTimeout(timeout);
      const latencyMs = Date.now() - startTime;

      if (res.ok) {
        const data = (await res.json()) as any;
        const models = (data.models || []).map((m: any) => m.name || m.model);
        this.availableModels = models;

        return {
          name: this.name,
          priority: this.priority,
          status: 'healthy',
          latencyMs,
          message: `Ollama operational with ${models.length} installed model(s): [${models.join(', ')}]`,
          activeModel: models[0] || activeModel,
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
        message: `Local Ollama unreachable at ${env.OLLAMA_BASE_URL} (${err instanceof Error ? err.message : String(err)})`,
        activeModel,
        timestamp: new Date().toISOString()
      };
    }
  }
}
