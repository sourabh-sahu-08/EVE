import { AIProvider } from '../providers/AIProvider';
import { AIRequest, AIResponse, FallbackStep, ProviderError } from '../types/provider';
import { QuotaManager } from './QuotaManager';
import { UsageTrackingService } from './UsageTrackingService';
import { ZeroCostGuard } from './ZeroCostGuard';

export class AllProvidersFailedError extends Error {
  constructor(
    message: string,
    public readonly history: FallbackStep[],
    public readonly lastError?: Error
  ) {
    super(message);
    this.name = 'AllProvidersFailedError';
  }
}

export class FallbackManager {
  constructor(
    private quotaManager: QuotaManager,
    private usageTracker: UsageTrackingService
  ) {}

  async executeWithFallback(
    providers: AIProvider[],
    request: AIRequest
  ): Promise<AIResponse> {
    const fallbackHistory: FallbackStep[] = [];
    let fallbackCount = 0;
    let lastError: Error | undefined;

    for (let i = 0; i < providers.length; i++) {
      const provider = providers[i];
      const startTime = Date.now();

      // 1. Check if provider credentials/endpoint are configured
      if (!provider.isConfigured()) {
        const step: FallbackStep = {
          provider: provider.name,
          model: request.model || 'default',
          reason: `Credentials for ${provider.name.toUpperCase()} are not configured in .env`,
          category: 'AUTH_ERROR',
          durationMs: 0
        };
        fallbackHistory.push(step);
        fallbackCount++;
        continue;
      }

      // 2. Check if provider is currently in rate-limit cooldown
      if (!this.quotaManager.isAvailable(provider)) {
        const remainingSec = this.quotaManager.getCooldownRemainingSec(provider.name);
        const step: FallbackStep = {
          provider: provider.name,
          model: request.model || 'default',
          reason: `${provider.name} is in cooldown for ${remainingSec}s due to recent rate limit (429)`,
          category: 'RATE_LIMIT',
          durationMs: 0
        };
        fallbackHistory.push(step);
        fallbackCount++;
        continue;
      }

      // 3. Zero-cost validation
      try {
        ZeroCostGuard.validateRequest(provider.name, request);
      } catch (guardErr: any) {
        const step: FallbackStep = {
          provider: provider.name,
          model: request.model || 'default',
          reason: guardErr.message,
          category: 'ZERO_COST_VIOLATION',
          durationMs: 0
        };
        fallbackHistory.push(step);
        fallbackCount++;
        continue;
      }

      // 4. Attempt generation
      try {
        console.log(`[AIOrchestrator] Attempting generation with provider: ${provider.name.toUpperCase()}...`);
        const response = await provider.generateResponse(request);

        const normalized: AIResponse = {
          ...response,
          fallbackCount: fallbackHistory.length,
          fallbackHistory
        };

        this.usageTracker.recordSuccess(normalized);
        return normalized;
      } catch (err: unknown) {
        const durationMs = Date.now() - startTime;
        const classified = err instanceof ProviderError
          ? err
          : new ProviderError(provider.name, (err as Error).message, 'UNKNOWN', undefined, true);

        lastError = classified;

        // Apply cooldown if specified by headers or 429
        if (classified.cooldownMs) {
          this.quotaManager.setCooldown(provider, classified.cooldownMs, classified.message);
        }

        this.usageTracker.recordFailure(
          provider.name,
          request.model || 'default',
          classified.message,
          classified.category,
          durationMs
        );

        const step: FallbackStep = {
          provider: provider.name,
          model: request.model || 'default',
          reason: classified.message,
          category: classified.category,
          durationMs
        };
        fallbackHistory.push(step);
        fallbackCount++;

        console.warn(`[FallbackManager] Provider ${provider.name.toUpperCase()} failed: ${classified.message}. Bounded fallback continuing...`);
      }
    }

    // All available providers failed
    const errorSummary = fallbackHistory.map((s) => `${s.provider} (${s.category}: ${s.reason})`).join(' -> ');
    throw new AllProvidersFailedError(
      `All AI providers failed or are unavailable. Fallback trail: [${errorSummary}]`,
      fallbackHistory,
      lastError
    );
  }
}
