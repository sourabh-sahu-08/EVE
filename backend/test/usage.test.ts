import { test, describe } from 'node:test';
import assert from 'node:assert';
import { UsageTrackingService } from '../src/orchestrator/UsageTrackingService';
import { AIResponse } from '../src/types/provider';

describe('UsageTrackingService Tests', () => {
  test('accurately accumulates request counts, tokens, and latencies', () => {
    const service = new UsageTrackingService();

    const sampleResponse: AIResponse = {
      text: 'Hello, this is EVE.',
      provider: 'groq',
      model: 'llama-3.3-70b-versatile',
      tokens: { promptTokens: 12, completionTokens: 18, totalTokens: 30, isEstimated: false },
      latencyMs: 340,
      fallbackCount: 0,
      fallbackHistory: [],
      timestamp: new Date().toISOString()
    };

    service.recordSuccess(sampleResponse);

    const summary = service.getSummary();
    assert.strictEqual(summary.zeroCostGuaranteed, true);
    assert.strictEqual(summary.totalCostUsd, 0.0);
    assert.ok(summary.totals.totalRequests >= 1);
    assert.ok(summary.totals.totalSuccesses >= 1);

    const providerStats = service.getProviderStats();
    assert.ok(providerStats.groq);
    assert.ok(providerStats.groq.requests >= 1);
    assert.ok(providerStats.groq.totalTokens >= 30);
  });

  test('records errors and rate-limit hits properly', () => {
    const service = new UsageTrackingService();
    service.recordFailure('groq', 'llama-3.3-70b-versatile', 'HTTP 429 Too Many Requests', 'RATE_LIMIT', 150);

    const summary = service.getSummary();
    assert.ok(summary.totals.totalErrors >= 1);

    const stats = service.getProviderStats();
    assert.ok(stats.groq.rateLimitHits >= 1);
  });
});
