import { env } from '../config/env';
import { isModelAllowlisted, validateZeroCostModel, ZERO_COST_ALLOWLIST } from '../config/modelAllowlist';
import { AIRequest, ErrorCategory, ProviderError } from '../types/provider';

export class ZeroCostGuard {
  /**
   * Evaluates an incoming AI request and guarantees that it satisfies zero-cost terms.
   */
  static validateRequest(providerName: string, request: AIRequest): void {
    if (!env.ZERO_COST_MODE) {
      return; // If zero-cost mode is explicitly disabled in .env
    }

    const requestedModel = request.model;
    if (requestedModel) {
      // 1. Extra security guard for OpenRouter
      if (providerName.toLowerCase() === 'openrouter' && !requestedModel.endsWith(':free')) {
        throw new ProviderError(
          providerName,
          `[ZeroCostGuard] Blocked request for paid OpenRouter model "${requestedModel}". Only verified models with the ":free" suffix are permitted.`,
          'ZERO_COST_VIOLATION',
          400,
          false
        );
      }

      // 2. Verified allowlist check
      if (!isModelAllowlisted(providerName, requestedModel)) {
        throw new ProviderError(
          providerName,
          `[ZeroCostGuard] Blocked unverified model "${requestedModel}" on ${providerName}. Budget limit is ₹0/month. Only allowlisted free models are permitted.`,
          'ZERO_COST_VIOLATION',
          400,
          false
        );
      }
    }
  }

  /**
   * Returns metadata verifying that zero-cost enforcement is active.
   */
  static getStatus(): any {
    return {
      zeroCostMode: env.ZERO_COST_MODE,
      budgetLimit: '₹0 / month',
      policy: 'Strictly Free Tier & Local Models Only',
      verifiedProviders: Object.keys(ZERO_COST_ALLOWLIST),
      paidModelFallbackAllowed: false,
      autoPurchaseAllowed: false
    };
  }
}
