import { test, describe } from 'node:test';
import assert from 'node:assert';
import { AIProvider } from '../src/providers/AIProvider';
import { FallbackManager, AllProvidersFailedError } from '../src/orchestrator/FallbackManager';
import { QuotaManager } from '../src/orchestrator/QuotaManager';
import { UsageTrackingService } from '../src/orchestrator/UsageTrackingService';
import { AIRequest, AIResponse, ProviderError, ProviderHealth, ProviderQuota } from '../src/types/provider';

class MockProvider implements AIProvider {
  constructor(
    public readonly name: string,
    public readonly priority: number,
    public shouldFail: boolean = false,
    public errorCode: number = 429,
    public errorCategory: any = 'RATE_LIMIT',
    public cooldownMs: number = 5000,
    public configured: boolean = true
  ) {}

  isConfigured(): boolean {
    return this.configured;
  }

  async generateResponse(request: AIRequest): Promise<AIResponse> {
    if (this.shouldFail) {
      throw new ProviderError(
        this.name,
        `Mock failure for ${this.name}`,
        this.errorCategory,
        this.errorCode,
        true,
        this.cooldownMs
      );
    }

    return {
      text: `Response from ${this.name}`,
      provider: this.name,
      model: 'test-model',
      tokens: { promptTokens: 10, completionTokens: 15, totalTokens: 25, isEstimated: false },
      latencyMs: 120,
      fallbackCount: 0,
      fallbackHistory: [],
      timestamp: new Date().toISOString()
    };
  }

  async checkHealth(): Promise<ProviderHealth> {
    return {
      name: this.name,
      priority: this.priority,
      status: this.shouldFail ? 'degraded' : 'healthy',
      latencyMs: 50,
      message: 'OK',
      activeModel: 'test-model',
      timestamp: new Date().toISOString()
    };
  }

  getModelAllowlist(): string[] {
    return ['test-model'];
  }

  getQuota(): ProviderQuota {
    return {
      provider: this.name,
      isExhausted: false
    };
  }

  setCooldown(): void {}
  clearCooldown(): void {}
}

const mockRepo: any = {
  loadQuotas: () => ({}),
  saveQuotas: () => {},
  loadUsage: () => ({
    records: [],
    totals: { totalRequests: 0, totalSuccesses: 0, totalErrors: 0, totalTokens: 0, estimatedCostUsd: 0 },
    providers: {},
    lastUpdated: new Date().toISOString()
  }),
  saveUsage: () => {}
};

describe('Bounded Fallback & Orchestrator Logic', () => {
  test('returns response from Priority 1 when healthy', async () => {
    const quotaMgr = new QuotaManager(mockRepo);
    const usageTracker = new UsageTrackingService(mockRepo);
    const fallbackMgr = new FallbackManager(quotaMgr, usageTracker);

    const p1 = new MockProvider('groq', 1, false);
    const p2 = new MockProvider('gemini', 2, false);

    const res = await fallbackMgr.executeWithFallback([p1, p2], {
      messages: [{ role: 'user', content: 'hello' }]
    });

    assert.strictEqual(res.provider, 'groq');
    assert.strictEqual(res.fallbackCount, 0);
  });

  test('falls back to Priority 2 when Priority 1 encounters 429 rate limit', async () => {
    const quotaMgr = new QuotaManager(mockRepo);
    const usageTracker = new UsageTrackingService(mockRepo);
    const fallbackMgr = new FallbackManager(quotaMgr, usageTracker);

    const p1 = new MockProvider('groq', 1, true, 429, 'RATE_LIMIT', 60000);
    const p2 = new MockProvider('gemini', 2, false);

    const res = await fallbackMgr.executeWithFallback([p1, p2], {
      messages: [{ role: 'user', content: 'hello' }]
    });

    assert.strictEqual(res.provider, 'gemini');
    assert.strictEqual(res.fallbackCount, 1);
    assert.strictEqual(res.fallbackHistory[0].provider, 'groq');
    assert.strictEqual(res.fallbackHistory[0].category, 'RATE_LIMIT');
  });

  test('falls back down chain: Groq -> Gemini -> OpenRouter -> Ollama', async () => {
    const quotaMgr = new QuotaManager(mockRepo);
    const usageTracker = new UsageTrackingService(mockRepo);
    const fallbackMgr = new FallbackManager(quotaMgr, usageTracker);

    const p1 = new MockProvider('groq', 1, true, 429, 'RATE_LIMIT');
    const p2 = new MockProvider('gemini', 2, true, 503, 'SERVER_ERROR');
    const p3 = new MockProvider('openrouter', 3, true, 500, 'SERVER_ERROR');
    const p4 = new MockProvider('ollama', 4, false);

    const res = await fallbackMgr.executeWithFallback([p1, p2, p3, p4], {
      messages: [{ role: 'user', content: 'hello' }]
    });

    assert.strictEqual(res.provider, 'ollama');
    assert.strictEqual(res.fallbackCount, 3);
    assert.strictEqual(res.fallbackHistory.length, 3);
  });

  test('throws AllProvidersFailedError with complete history when all providers fail', async () => {
    const quotaMgr = new QuotaManager(mockRepo);
    const usageTracker = new UsageTrackingService(mockRepo);
    const fallbackMgr = new FallbackManager(quotaMgr, usageTracker);

    const p1 = new MockProvider('groq', 1, true, 429);
    const p2 = new MockProvider('gemini', 2, true, 500);

    await assert.rejects(
      async () => {
        await fallbackMgr.executeWithFallback([p1, p2], {
          messages: [{ role: 'user', content: 'hello' }]
        });
      },
      (err: any) => {
        assert(err instanceof AllProvidersFailedError);
        assert.strictEqual(err.history.length, 2);
        return true;
      }
    );
  });
});
