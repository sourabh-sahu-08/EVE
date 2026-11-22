import dotenv from 'dotenv';
import path from 'path';

// Load environment variables from .env file
dotenv.config({ path: path.resolve(process.cwd(), '.env') });

export interface EnvConfig {
  PORT: number;
  HOST: string;
  NODE_ENV: string;
  ZERO_COST_MODE: boolean;
  GROQ_API_KEY?: string;
  GEMINI_API_KEY?: string;
  OPENROUTER_API_KEY?: string;
  OLLAMA_BASE_URL: string;
  EVE_API_KEY?: string;
  DATA_DIR: string;
}

export const env: EnvConfig = {
  PORT: parseInt(process.env.PORT || '3000', 10),
  HOST: process.env.HOST || '0.0.0.0',
  NODE_ENV: process.env.NODE_ENV || 'development',
  ZERO_COST_MODE: process.env.ZERO_COST_MODE !== 'false', // Default true: strictly enforce zero-cost
  GROQ_API_KEY: process.env.GROQ_API_KEY?.trim() || undefined,
  GEMINI_API_KEY: process.env.GEMINI_API_KEY?.trim() || undefined,
  OPENROUTER_API_KEY: process.env.OPENROUTER_API_KEY?.trim() || undefined,
  OLLAMA_BASE_URL: process.env.OLLAMA_BASE_URL?.trim() || 'http://127.0.0.1:11434',
  EVE_API_KEY: process.env.EVE_API_KEY?.trim() || undefined,
  DATA_DIR: process.env.DATA_DIR || './data'
};

/**
 * Mask an API key so it is never exposed in logs or terminal outputs.
 */
export function maskKey(key?: string): string {
  if (!key) return '(not configured)';
  if (key.length <= 8) return '****';
  return `${key.slice(0, 4)}...${key.slice(-4)}`;
}
