import { test, describe } from 'node:test';
import assert from 'node:assert';
import { isModelAllowlisted, validateZeroCostModel } from '../src/config/modelAllowlist';
import { ZeroCostGuard } from '../src/orchestrator/ZeroCostGuard';

describe('ZeroCostGuard & Allowlist Security Tests', () => {
  test('verified zero-cost models are allowlisted', () => {
    assert.strictEqual(isModelAllowlisted('groq', 'qwen/qwen3.8-27b'), true);
    assert.strictEqual(isModelAllowlisted('groq', 'openai/gpt-oss-20b'), true);
    assert.strictEqual(isModelAllowlisted('gemini', 'gemini-2.5-flash'), true);
    assert.strictEqual(isModelAllowlisted('openrouter', 'nvidia/nemotron-3.5-lightning:free'), true);
    assert.strictEqual(isModelAllowlisted('ollama', 'qwen2.5:7b'), true);
  });

  test('paid and unverified models are strictly rejected', () => {
    assert.strictEqual(isModelAllowlisted('groq', 'claude-3-opus'), false);
    assert.strictEqual(isModelAllowlisted('gemini', 'gpt-4o'), false);
    assert.strictEqual(isModelAllowlisted('openrouter', 'anthropic/claude-3.5-sonnet'), false);
  });

  test('OpenRouter models without :free suffix are rejected', () => {
    assert.throws(
      () => {
        validateZeroCostModel('openrouter', 'meta-llama/llama-3.3-70b-instruct');
      },
      /mandatory ":free" suffix/
    );
  });

  test('ZeroCostGuard rejects unlisted models during request validation', () => {
    assert.throws(
      () => {
        ZeroCostGuard.validateRequest('groq', {
          messages: [{ role: 'user', content: 'test' }],
          model: 'paid-expensive-model-v1'
        });
      },
      /Blocked unverified model/
    );
  });

  test('ZeroCostGuard allows valid allowlisted models', () => {
    assert.doesNotThrow(() => {
      ZeroCostGuard.validateRequest('groq', {
        messages: [{ role: 'user', content: 'test' }],
        model: 'qwen/qwen3.8-27b'
      });
    });
  });
});
