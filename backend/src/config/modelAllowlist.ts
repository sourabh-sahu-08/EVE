/**
 * Manually verified Zero-Cost Model Allowlist.
 * In ZERO_COST_MODE=true, ONLY models listed here are permitted.
 * Any model with pricing, billing upgrade requirements, or unknown terms is strictly rejected.
 */

export interface ModelMetadata {
  id: string;
  provider: 'groq' | 'gemini' | 'openrouter' | 'ollama';
  displayName: string;
  contextWindow: number;
  rateLimitTiers: string;
  isFreeVerified: boolean;
}

export const ZERO_COST_ALLOWLIST: Record<string, ModelMetadata[]> = {
  groq: [
    {
      id: 'qwen/qwen3.8-27b',
      provider: 'groq',
      displayName: 'Qwen 3.8 27B',
      contextWindow: 131072,
      rateLimitTiers: 'Free Tier: 30 RPM, 14400 RPD',
      isFreeVerified: true
    },
    {
      id: 'openai/gpt-oss-20b',
      provider: 'groq',
      displayName: 'GPT-OSS 20B',
      contextWindow: 128000,
      rateLimitTiers: 'Free Tier: 30 RPM, 14400 RPD',
      isFreeVerified: true
    },
    {
      id: 'openai/gpt-oss-120b',
      provider: 'groq',
      displayName: 'GPT-OSS 120B',
      contextWindow: 128000,
      rateLimitTiers: 'Free Tier: 30 RPM, 14400 RPD',
      isFreeVerified: true
    },
    {
      id: 'allam-2-7b',
      provider: 'groq',
      displayName: 'Allam 2 7B',
      contextWindow: 8192,
      rateLimitTiers: 'Free Tier: 30 RPM, 14400 RPD',
      isFreeVerified: true
    }
  ],
  gemini: [
    {
      id: 'gemini-2.5-flash',
      provider: 'gemini',
      displayName: 'Gemini 2.5 Flash',
      contextWindow: 1048576,
      rateLimitTiers: 'Free Tier: 15 RPM, 1500 RPD, 1000000 TPM',
      isFreeVerified: true
    },
    {
      id: 'gemini-flash-latest',
      provider: 'gemini',
      displayName: 'Gemini Flash Latest',
      contextWindow: 1048576,
      rateLimitTiers: 'Free Tier: 15 RPM, 1500 RPD, 1000000 TPM',
      isFreeVerified: true
    },
    {
      id: 'gemini-2.5-pro',
      provider: 'gemini',
      displayName: 'Gemini 2.5 Pro',
      contextWindow: 2097152,
      rateLimitTiers: 'Free Tier: 2 RPM, 50 RPD, 32000 TPM',
      isFreeVerified: true
    },
    {
      id: 'gemini-3.8-flash',
      provider: 'gemini',
      displayName: 'Gemini 3.8 Flash',
      contextWindow: 1048576,
      rateLimitTiers: 'Free Tier Preview',
      isFreeVerified: true
    }
  ],
  openrouter: [
    {
      id: 'nvidia/nemotron-3.5-lightning:free',
      provider: 'openrouter',
      displayName: 'Nemotron 3.5 Lightning (Free)',
      contextWindow: 1000000,
      rateLimitTiers: 'Free Tier: 20 RPM',
      isFreeVerified: true
    },
    {
      id: 'inclusionai/ling-3.2-flash:free',
      provider: 'openrouter',
      displayName: 'Ling 3.2 Flash (Free)',
      contextWindow: 1048576,
      rateLimitTiers: 'Free Tier: 20 RPM',
      isFreeVerified: true
    },
    {
      id: 'liquid/lfm-3-1.7b:free',
      provider: 'openrouter',
      displayName: 'LFM 3 1.7B (Free)',
      contextWindow: 262144,
      rateLimitTiers: 'Free Tier: 20 RPM',
      isFreeVerified: true
    },
    {
      id: 'google/gemma-4-26b-a4b-it:free',
      provider: 'openrouter',
      displayName: 'Gemma 4 26B (Free)',
      contextWindow: 262144,
      rateLimitTiers: 'Free Tier: 20 RPM',
      isFreeVerified: true
    },
    {
      id: 'particle-ai/particle-1:free',
      provider: 'openrouter',
      displayName: 'Particle-1 (Free)',
      contextWindow: 1048576,
      rateLimitTiers: 'Free Tier: 20 RPM',
      isFreeVerified: true
    }
  ],
  ollama: [
    {
      id: 'qwen2.5:7b',
      provider: 'ollama',
      displayName: 'Qwen 2.5 7B (Local)',
      contextWindow: 32768,
      rateLimitTiers: 'Unlimited Local Hardware',
      isFreeVerified: true
    },
    {
      id: 'llama3.1:latest',
      provider: 'ollama',
      displayName: 'Llama 3.1 8B (Local)',
      contextWindow: 131072,
      rateLimitTiers: 'Unlimited Local Hardware',
      isFreeVerified: true
    }
  ]
};

/**
 * Checks if a specific model for a provider is in the verified zero-cost allowlist.
 */
export function isModelAllowlisted(provider: string, modelId: string): boolean {
  const models = ZERO_COST_ALLOWLIST[provider.toLowerCase()];
  if (!models) return false;
  return models.some((m) => m.id === modelId && m.isFreeVerified);
}

/**
 * Enforces zero-cost policy. Throws an error if model is not allowlisted.
 */
export function validateZeroCostModel(provider: string, modelId: string): void {
  // Extra safeguard for OpenRouter: must end in :free
  if (provider.toLowerCase() === 'openrouter' && !modelId.endsWith(':free')) {
    throw new Error(
      `[ZeroCostGuard] REJECTED: Model "${modelId}" on OpenRouter does not contain the mandatory ":free" suffix. Paid models are blocked.`
    );
  }

  if (!isModelAllowlisted(provider, modelId)) {
    throw new Error(
      `[ZeroCostGuard] REJECTED: Model "${modelId}" is not in the verified zero-cost allowlist for provider "${provider}".`
    );
  }
}

/**
 * Retrieves the preferred default zero-cost model for a provider.
 */
export function getDefaultModel(provider: string): string {
  const models = ZERO_COST_ALLOWLIST[provider.toLowerCase()];
  if (!models || models.length === 0) {
    throw new Error(`No zero-cost models configured for provider "${provider}"`);
  }
  return models[0].id;
}
