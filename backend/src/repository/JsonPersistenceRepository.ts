import fs from 'fs';
import path from 'path';
import { env } from '../config/env';

export interface UsageRecord {
  id: string;
  timestamp: string;
  provider: string;
  model: string;
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
  isEstimated: boolean;
  latencyMs: number;
  status: 'success' | 'error';
  error?: string;
  fallbackCount: number;
}

export interface ProviderStats {
  requests: number;
  successes: number;
  errors: number;
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
  avgLatencyMs: number;
  rateLimitHits: number;
}

export interface PersistedUsageData {
  records: UsageRecord[];
  totals: {
    totalRequests: number;
    totalSuccesses: number;
    totalErrors: number;
    totalTokens: number;
    estimatedCostUsd: 0; // Strictly zero
  };
  providers: Record<string, ProviderStats>;
  lastUpdated: string;
}

export class JsonPersistenceRepository {
  private readonly dataDir: string;
  private readonly usageFilePath: string;
  private readonly quotaFilePath: string;

  constructor() {
    this.dataDir = path.resolve(process.cwd(), env.DATA_DIR);
    this.usageFilePath = path.join(this.dataDir, 'usage.json');
    this.quotaFilePath = path.join(this.dataDir, 'quotas.json');
    this.ensureDirectory();
  }

  private ensureDirectory(): void {
    if (!fs.existsSync(this.dataDir)) {
      fs.mkdirSync(this.dataDir, { recursive: true });
    }
  }

  loadUsage(): PersistedUsageData {
    this.ensureDirectory();
    if (fs.existsSync(this.usageFilePath)) {
      try {
        const raw = fs.readFileSync(this.usageFilePath, 'utf-8');
        return JSON.parse(raw);
      } catch (err) {
        console.warn('Failed to parse usage.json, resetting to default:', err);
      }
    }

    return {
      records: [],
      totals: {
        totalRequests: 0,
        totalSuccesses: 0,
        totalErrors: 0,
        totalTokens: 0,
        estimatedCostUsd: 0
      },
      providers: {},
      lastUpdated: new Date().toISOString()
    };
  }

  saveUsage(data: PersistedUsageData): void {
    this.ensureDirectory();
    try {
      const tempPath = `${this.usageFilePath}.tmp`;
      fs.writeFileSync(tempPath, JSON.stringify(data, null, 2), 'utf-8');
      fs.renameSync(tempPath, this.usageFilePath);
    } catch (err) {
      console.error('Failed to save usage data:', err);
    }
  }

  loadQuotas(): Record<string, any> {
    this.ensureDirectory();
    if (fs.existsSync(this.quotaFilePath)) {
      try {
        const raw = fs.readFileSync(this.quotaFilePath, 'utf-8');
        return JSON.parse(raw);
      } catch {
        return {};
      }
    }
    return {};
  }

  saveQuotas(quotas: Record<string, any>): void {
    this.ensureDirectory();
    try {
      const tempPath = `${this.quotaFilePath}.tmp`;
      fs.writeFileSync(tempPath, JSON.stringify(quotas, null, 2), 'utf-8');
      fs.renameSync(tempPath, this.quotaFilePath);
    } catch (err) {
      console.error('Failed to save quota data:', err);
    }
  }
}
