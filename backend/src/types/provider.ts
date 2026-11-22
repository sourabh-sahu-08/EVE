export type Role = 'system' | 'user' | 'assistant';

export interface ChatMessage {
  role: Role;
  content: string;
}

export interface AIRequest {
  messages: ChatMessage[];
  model?: string;
  maxTokens?: number;
  temperature?: number;
  systemPrompt?: string;
}

export interface TokenUsage {
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
  isEstimated: boolean;
}

export interface FallbackStep {
  provider: string;
  model: string;
  reason: string;
  category: ErrorCategory;
  durationMs: number;
}

export interface AIResponse {
  text: string;
  provider: string;
  model: string;
  tokens: TokenUsage;
  latencyMs: number;
  fallbackCount: number;
  fallbackHistory: FallbackStep[];
  timestamp: string;
}

export type HealthStatus = 'healthy' | 'degraded' | 'unavailable' | 'unconfigured';

export interface ProviderHealth {
  name: string;
  priority: number;
  status: HealthStatus;
  latencyMs: number;
  message: string;
  activeModel: string;
  rateLimitRemaining?: number;
  cooldownRemainingSec?: number;
  timestamp: string;
}

export interface ProviderQuota {
  provider: string;
  remainingRequests?: number;
  remainingTokens?: number;
  resetTimestamp?: number;
  cooldownUntil?: number;
  isExhausted: boolean;
}

export type ErrorCategory =
  | 'RATE_LIMIT'
  | 'AUTH_ERROR'
  | 'INVALID_REQUEST'
  | 'MODEL_UNAVAILABLE'
  | 'TIMEOUT'
  | 'SERVER_ERROR'
  | 'NETWORK_ERROR'
  | 'ZERO_COST_VIOLATION'
  | 'UNKNOWN';

export class ProviderError extends Error {
  constructor(
    public readonly provider: string,
    message: string,
    public readonly category: ErrorCategory,
    public readonly statusCode?: number,
    public readonly retryable: boolean = true,
    public readonly cooldownMs?: number
  ) {
    super(`[${provider}] ${message}`);
    this.name = 'ProviderError';
  }
}
