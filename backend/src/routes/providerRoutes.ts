import { Router, Request, Response } from 'express';
import { AIOrchestrator } from '../orchestrator/AIOrchestrator';

export function createProviderRoutes(orchestrator: AIOrchestrator): Router {
  const router = Router();

  /**
   * GET /api/providers/status
   * Reports current configuration, priority, model allowlists, and cooldown statuses.
   */
  router.get('/status', (req: Request, res: Response) => {
    try {
      const status = orchestrator.getProvidersStatus();
      return res.status(200).json({
        success: true,
        data: status
      });
    } catch (err: any) {
      return res.status(500).json({
        success: false,
        error: err.message
      });
    }
  });

  /**
   * GET /api/providers/health
   * Performs an active health-check ping against all providers.
   */
  router.get('/health', async (req: Request, res: Response) => {
    try {
      const healthList = await orchestrator.checkAllProvidersHealth();
      const anyHealthy = healthList.some((h) => h.status === 'healthy');

      return res.status(anyHealthy ? 200 : 503).json({
        success: anyHealthy,
        data: healthList
      });
    } catch (err: any) {
      return res.status(500).json({
        success: false,
        error: err.message
      });
    }
  });

  return router;
}
