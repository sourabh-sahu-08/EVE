import { AIProvider } from './AIProvider';
import {
  AIRequest,
  AIResponse,
  ErrorCategory,
  ProviderError,
  ProviderHealth,
  ProviderQuota,
  TokenUsage
} from '../types/provider';
import { ZERO_COST_ALLOWLIST } from '../config/modelAllowlist';

export abstract class BaseProvider implements AIProvider {
  abstract readonly name: string;
  abstract readonly priority: number;

  protected cooldownUntil: number = 0;
  protected remainingRequests?: number;
  protected remainingTokens?: number;
  protected resetTimestamp?: number;

  abstract isConfigured(): boolean;
  abstract generateResponse(request: AIRequest): Promise<AIResponse>;
  abstract checkHealth(): Promise<ProviderHealth>;

  getModelAllowlist(): string[] {
    const models = ZERO_COST_ALLOWLIST[this.name.toLowerCase()];
    return models ? models.map((m) => m.id) : [];
  }

  getQuota(): ProviderQuota {
    const now = Date.now();
    const inCooldown = this.cooldownUntil > now;
    return {
      provider: this.name,
      remainingRequests: this.remainingRequests,
      remainingTokens: this.remainingTokens,
      resetTimestamp: this.resetTimestamp,
      cooldownUntil: inCooldown ? this.cooldownUntil : undefined,
      isExhausted: inCooldown || (this.remainingRequests !== undefined && this.remainingRequests <= 0)
    };
  }

  setCooldown(cooldownMs: number, reason?: string): void {
    this.cooldownUntil = Date.now() + cooldownMs;
    console.warn(`[${this.name}] Entering cooldown for ${Math.round(cooldownMs / 1000)}s: ${reason || 'Rate limit'}`);
  }

  clearCooldown(): void {
    this.cooldownUntil = 0;
  }

  protected parseRateLimitHeaders(headers: Headers): void {
    // 1. Retry-After header (seconds or RFC 1123 date)
    const retryAfter = headers.get('retry-after');
    if (retryAfter) {
      const seconds = parseInt(retryAfter, 10);
      if (!isNaN(seconds)) {
        this.setCooldown(seconds * 1000, `Retry-After: ${seconds}s`);
      } else {
        const date = Date.parse(retryAfter);
        if (!isNaN(date) && date > Date.now()) {
          this.setCooldown(date - Date.now(), `Retry-After: ${retryAfter}`);
        }
      }
    }

    // 2. Standard rate limit headers
    const reqRemaining = headers.get('x-ratelimit-remaining-requests');
    if (reqRemaining) {
      this.remainingRequests = parseInt(reqRemaining, 10);
      if (this.remainingRequests === 0 && !this.cooldownUntil) {
        this.setCooldown(60000, 'Zero requests remaining');
      }
    }

    const tokRemaining = headers.get('x-ratelimit-remaining-tokens');
    if (tokRemaining) {
      this.remainingTokens = parseInt(tokRemaining, 10);
    }

    const resetReq = headers.get('x-ratelimit-reset-requests');
    if (resetReq) {
      // Could be e.g. "6m0s" or a timestamp or seconds
      const seconds = parseFloat(resetReq);
      if (!isNaN(seconds)) {
        this.resetTimestamp = Date.now() + seconds * 1000;
      }
    }
  }

  protected classifyError(error: unknown, statusCode?: number): ProviderError {
    if (error instanceof ProviderError) return error;

    const errMsg = error instanceof Error ? error.message : String(error);

    // Timeout detection
    if (errMsg.toLowerCase().includes('timeout') || errMsg.toLowerCase().includes('aborted')) {
      return new ProviderError(this.name, 'Request timed out', 'TIMEOUT', statusCode, true, 5000);
    }

    if (statusCode) {
      switch (statusCode) {
        case 429: {
          const cooldown = 60000; // Default 60s cooldown if Retry-After not found
          this.setCooldown(cooldown, 'HTTP 429 Too Many Requests');
          return new ProviderError(this.name, 'Rate limit exceeded (429)', 'RATE_LIMIT', statusCode, true, cooldown);
        }
        case 401:
        case 403:
          return new ProviderError(this.name, `Authentication failed (${statusCode}). Check API key.`, 'AUTH_ERROR', statusCode, false);
        case 400:
          return new ProviderError(this.name, `Invalid request format (${statusCode}): ${errMsg}`, 'INVALID_REQUEST', statusCode, false);
        case 404:
          return new ProviderError(this.name, `Model or endpoint unavailable (${statusCode})`, 'MODEL_UNAVAILABLE', statusCode, true, 30000);
        case 500:
        case 502:
        case 503:
        case 504:
          return new ProviderError(this.name, `Provider server error (${statusCode})`, 'SERVER_ERROR', statusCode, true, 15000);
        default:
          return new ProviderError(this.name, `HTTP error ${statusCode}: ${errMsg}`, 'UNKNOWN', statusCode, true);
      }
    }

    return new ProviderError(this.name, `Network communication failure: ${errMsg}`, 'NETWORK_ERROR', undefined, true, 5000);
  }

  /**
   * Estimates token count based on typical ~4 characters per token ratio.
   */
  protected estimateTokens(prompt: string, completion: string): TokenUsage {
    const promptTokens = Math.max(1, Math.ceil(prompt.length / 4));
    const completionTokens = Math.max(1, Math.ceil(completion.length / 4));
    return {
      promptTokens,
      completionTokens,
      totalTokens: promptTokens + completionTokens,
      isEstimated: true
    };
  }
}
