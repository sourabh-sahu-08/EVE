import { JsonPersistenceRepository, PersistedUsageData, ProviderStats, UsageRecord } from '../repository/JsonPersistenceRepository';
import { AIResponse, TokenUsage } from '../types/provider';

export class UsageTrackingService {
  private repository: JsonPersistenceRepository;
  private data: PersistedUsageData;

  constructor(repository?: JsonPersistenceRepository) {
    this.repository = repository || new JsonPersistenceRepository();
    this.data = this.repository.loadUsage();
  }

  recordSuccess(response: AIResponse): void {
    const record: UsageRecord = {
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
      timestamp: response.timestamp,
      provider: response.provider,
      model: response.model,
      promptTokens: response.tokens.promptTokens,
      completionTokens: response.tokens.completionTokens,
      totalTokens: response.tokens.totalTokens,
      isEstimated: response.tokens.isEstimated,
      latencyMs: response.latencyMs,
      status: 'success',
      fallbackCount: response.fallbackCount
    };

    this.data.records.unshift(record);
    if (this.data.records.length > 500) {
      this.data.records = this.data.records.slice(0, 500); // keep last 500 records
    }

    this.data.totals.totalRequests++;
    this.data.totals.totalSuccesses++;
    this.data.totals.totalTokens += response.tokens.totalTokens;

    this.ensureProviderStats(response.provider);
    const stats = this.data.providers[response.provider];
    stats.requests++;
    stats.successes++;
    stats.promptTokens += response.tokens.promptTokens;
    stats.completionTokens += response.tokens.completionTokens;
    stats.totalTokens += response.tokens.totalTokens;
    stats.avgLatencyMs = Math.round(
      (stats.avgLatencyMs * (stats.successes - 1) + response.latencyMs) / stats.successes
    );

    this.data.lastUpdated = new Date().toISOString();
    this.repository.saveUsage(this.data);
  }

  recordFailure(provider: string, model: string, error: string, category: string, latencyMs: number): void {
    const record: UsageRecord = {
      id: `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
      timestamp: new Date().toISOString(),
      provider,
      model,
      promptTokens: 0,
      completionTokens: 0,
      totalTokens: 0,
      isEstimated: true,
      latencyMs,
      status: 'error',
      error: `[${category}] ${error}`,
      fallbackCount: 0
    };

    this.data.records.unshift(record);
    if (this.data.records.length > 500) {
      this.data.records = this.data.records.slice(0, 500);
    }

    this.data.totals.totalRequests++;
    this.data.totals.totalErrors++;

    this.ensureProviderStats(provider);
    const stats = this.data.providers[provider];
    stats.requests++;
    stats.errors++;
    if (category === 'RATE_LIMIT') {
      stats.rateLimitHits++;
    }

    this.data.lastUpdated = new Date().toISOString();
    this.repository.saveUsage(this.data);
  }

  getSummary(): any {
    return {
      status: 'operational',
      zeroCostGuaranteed: true,
      totalCostUsd: 0.0,
      totals: this.data.totals,
      recentRecordsCount: this.data.records.length,
      lastUpdated: this.data.lastUpdated
    };
  }

  getProviderStats(): Record<string, ProviderStats> {
    return this.data.providers;
  }

  getRecentRecords(limit: number = 20): UsageRecord[] {
    return this.data.records.slice(0, limit);
  }

  private ensureProviderStats(provider: string): void {
    if (!this.data.providers[provider]) {
      this.data.providers[provider] = {
        requests: 0,
        successes: 0,
        errors: 0,
        promptTokens: 0,
        completionTokens: 0,
        totalTokens: 0,
        avgLatencyMs: 0,
        rateLimitHits: 0
      };
    }
  }
}
