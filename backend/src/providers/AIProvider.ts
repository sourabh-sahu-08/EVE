import { AIRequest, AIResponse, ProviderHealth, ProviderQuota } from '../types/provider';

/**
 * Common contract that all AI provider adapters must implement.
 */
export interface AIProvider {
  /** Canonical provider name (groq, gemini, openrouter, ollama) */
  readonly name: string;

  /** Priority rank: 1 (Groq) -> 2 (Gemini) -> 3 (OpenRouter) -> 4 (Ollama) */
  readonly priority: number;

  /** Checks whether the provider credentials and config are present */
  isConfigured(): boolean;

  /** Executes an AI completion request and returns a normalized response */
  generateResponse(request: AIRequest): Promise<AIResponse>;

  /** Performs a health-check probe against the provider endpoint */
  checkHealth(): Promise<ProviderHealth>;

  /** Returns the list of verified zero-cost model IDs for this provider */
  getModelAllowlist(): string[];

  /** Returns current rate limit / quota status */
  getQuota(): ProviderQuota;

  /** Updates quota and cooldown status */
  setCooldown(cooldownMs: number, reason?: string): void;

  /** Clears any active cooldown */
  clearCooldown(): void;
}
