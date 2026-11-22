import { AIProvider } from '../providers/AIProvider';
import { AIRequest, AIResponse, ProviderHealth } from '../types/provider';
import { FallbackManager } from './FallbackManager';
import { ProviderRouter } from './ProviderRouter';
import { QuotaManager } from './QuotaManager';
import { UsageTrackingService } from './UsageTrackingService';
import { ZeroCostGuard } from './ZeroCostGuard';

export const EVE_SYSTEM_PROMPT = `You are EVE, an ambient, sophisticated personal artificial intelligence voice assistant.
Your responses will be spoken aloud to the user via Text-to-Speech:
1. Be concise, intelligent, warm, and natural.
2. Avoid markdown tables, long lists, emojis, asterisks, or formatting that sounds awkward when spoken aloud.
3. Keep spoken replies typically between 1 to 3 sentences unless the user explicitly asks for detailed explanations.
4. Always prioritize clarity and directness.`;

export class AIOrchestrator {
  private router: ProviderRouter;
  private quotaManager: QuotaManager;
  private usageTracker: UsageTrackingService;
  private fallbackManager: FallbackManager;

  constructor(customRouter?: ProviderRouter) {
    this.router = customRouter || new ProviderRouter();
    this.quotaManager = new QuotaManager();
    this.usageTracker = new UsageTrackingService();
    this.fallbackManager = new FallbackManager(this.quotaManager, this.usageTracker);
  }

  async processInteraction(request: AIRequest): Promise<AIResponse> {
    const fullRequest: AIRequest = {
      ...request,
      systemPrompt: request.systemPrompt || EVE_SYSTEM_PROMPT
    };

    const candidates = this.router.getCandidateProviders();
    return this.fallbackManager.executeWithFallback(candidates, fullRequest);
  }

  async checkAllProvidersHealth(): Promise<ProviderHealth[]> {
    const providers = this.router.getAllProviders();
    const results = await Promise.all(providers.map((p) => p.checkHealth()));
    return results;
  }

  getProvidersStatus(): any {
    const providers = this.router.getAllProviders();
    const quotas = this.quotaManager.getStatus(providers);
    return {
      providers: providers.map((p) => ({
        name: p.name,
        priority: p.priority,
        isConfigured: p.isConfigured(),
        allowlist: p.getModelAllowlist(),
        quota: quotas[p.name]
      })),
      priorityOrder: providers.map((p) => p.name),
      zeroCostGuard: ZeroCostGuard.getStatus()
    };
  }

  getUsageSummary(): any {
    return this.usageTracker.getSummary();
  }

  getProviderUsageStats(): any {
    return this.usageTracker.getProviderStats();
  }

  getRouter(): ProviderRouter {
    return this.router;
  }

  getQuotaManager(): QuotaManager {
    return this.quotaManager;
  }

  getUsageTracker(): UsageTrackingService {
    return this.usageTracker;
  }
}
