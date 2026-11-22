import { AIProvider } from '../providers/AIProvider';
import { GroqProvider } from '../providers/GroqProvider';
import { GeminiProvider } from '../providers/GeminiProvider';
import { OpenRouterProvider } from '../providers/OpenRouterProvider';
import { OllamaProvider } from '../providers/OllamaProvider';

export class ProviderRouter {
  private providers: AIProvider[];

  constructor(customProviders?: AIProvider[]) {
    if (customProviders) {
      this.providers = [...customProviders].sort((a, b) => a.priority - b.priority);
    } else {
      this.providers = [
        new GroqProvider(),
        new GeminiProvider(),
        new OpenRouterProvider(),
        new OllamaProvider()
      ].sort((a, b) => a.priority - b.priority);
    }
  }

  getAllProviders(): AIProvider[] {
    return [...this.providers];
  }

  getProviderByName(name: string): AIProvider | undefined {
    return this.providers.find((p) => p.name.toLowerCase() === name.toLowerCase());
  }

  /**
   * Returns available candidates in strict priority order.
   * If a specific model is requested, filters or prioritizes the matching provider.
   */
  getCandidateProviders(requestedProvider?: string): AIProvider[] {
    if (requestedProvider) {
      const target = this.getProviderByName(requestedProvider);
      if (target) {
        const others = this.providers.filter((p) => p.name !== target.name);
        return [target, ...others];
      }
    }
    return [...this.providers];
  }
}
