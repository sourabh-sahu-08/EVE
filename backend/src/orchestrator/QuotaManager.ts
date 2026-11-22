import { AIProvider } from '../providers/AIProvider';
import { JsonPersistenceRepository } from '../repository/JsonPersistenceRepository';
import { ProviderQuota } from '../types/provider';

export class QuotaManager {
  private repository: JsonPersistenceRepository;
  private cooldowns: Record<string, { cooldownUntil: number; reason: string }> = {};

  constructor(repository?: JsonPersistenceRepository) {
    this.repository = repository || new JsonPersistenceRepository();
    const persisted = this.repository.loadQuotas();
    const now = Date.now();
    for (const [provider, data] of Object.entries(persisted)) {
      if (data && data.cooldownUntil && data.cooldownUntil > now) {
        this.cooldowns[provider] = data;
      }
    }
  }

  isAvailable(provider: AIProvider): boolean {
    const quota = provider.getQuota();
    if (quota.isExhausted) return false;

    const storedCooldown = this.cooldowns[provider.name];
    if (storedCooldown && storedCooldown.cooldownUntil > Date.now()) {
      return false;
    }

    return true;
  }

  getCooldownRemainingSec(provider: string): number {
    const entry = this.cooldowns[provider];
    if (!entry) return 0;
    const remaining = entry.cooldownUntil - Date.now();
    return remaining > 0 ? Math.ceil(remaining / 1000) : 0;
  }

  setCooldown(provider: AIProvider, cooldownMs: number, reason: string): void {
    const cooldownUntil = Date.now() + cooldownMs;
    this.cooldowns[provider.name] = { cooldownUntil, reason };
    provider.setCooldown(cooldownMs, reason);
    this.repository.saveQuotas(this.cooldowns);
  }

  clearCooldown(provider: AIProvider): void {
    delete this.cooldowns[provider.name];
    provider.clearCooldown();
    this.repository.saveQuotas(this.cooldowns);
  }

  getStatus(providers: AIProvider[]): Record<string, ProviderQuota & { cooldownRemainingSec: number }> {
    const result: Record<string, any> = {};
    for (const p of providers) {
      const q = p.getQuota();
      const cooldownSec = this.getCooldownRemainingSec(p.name);
      result[p.name] = {
        ...q,
        cooldownRemainingSec: cooldownSec,
        isExhausted: q.isExhausted || cooldownSec > 0
      };
    }
    return result;
  }
}
