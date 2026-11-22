import { Router, Request, Response } from 'express';
import { AIOrchestrator } from '../orchestrator/AIOrchestrator';
import { AIRequest, ChatMessage } from '../types/provider';

export function createChatRoutes(orchestrator: AIOrchestrator): Router {
  const router = Router();

  /**
   * POST /api/assistant/interact
   * Core assistant conversation endpoint.
   */
  router.post('/interact', async (req: Request, res: Response) => {
    try {
      const { message, history, model, maxTokens, temperature } = req.body;

      if (!message || typeof message !== 'string' || message.trim().length === 0) {
        return res.status(400).json({
          success: false,
          error: 'Field "message" is required and cannot be empty.'
        });
      }

      const messages: ChatMessage[] = [];
      if (Array.isArray(history)) {
        for (const item of history) {
          if (item && item.role && item.content) {
            messages.push({ role: item.role, content: item.content });
          }
        }
      }

      messages.push({ role: 'user', content: message.trim() });

      const aiRequest: AIRequest = {
        messages,
        model,
        maxTokens: maxTokens ? parseInt(maxTokens, 10) : 1024,
        temperature: temperature !== undefined ? parseFloat(temperature) : 0.7
      };

      const response = await orchestrator.processInteraction(aiRequest);

      return res.status(200).json({
        success: true,
        response
      });
    } catch (err: any) {
      console.error('[ChatRoutes] Interaction failed:', err.message);
      return res.status(503).json({
        success: false,
        error: err.message,
        fallbackHistory: err.history || []
      });
    }
  });

  return router;
}
