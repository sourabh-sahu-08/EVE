import { Router, Request, Response } from 'express';
import { AIOrchestrator } from '../orchestrator/AIOrchestrator';

export function createUsageRoutes(orchestrator: AIOrchestrator): Router {
  const router = Router();

  /**
   * GET /api/usage/summary
   * Returns aggregate usage metrics, zero-cost verification, and totals.
   */
  router.get('/summary', (req: Request, res: Response) => {
    try {
      const summary = orchestrator.getUsageSummary();
      return res.status(200).json({
        success: true,
        data: summary
      });
    } catch (err: any) {
      return res.status(500).json({
        success: false,
        error: err.message
      });
    }
  });

  /**
   * GET /api/usage/providers
   * Returns granular metrics broken down by individual provider.
   */
  router.get('/providers', (req: Request, res: Response) => {
    try {
      const providerStats = orchestrator.getProviderUsageStats();
      return res.status(200).json({
        success: true,
        data: providerStats
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
