import express, { Request, Response, NextFunction } from 'express';
import cors from 'cors';
import { env, maskKey } from './config/env';
import { AIOrchestrator } from './orchestrator/AIOrchestrator';
import { createChatRoutes } from './routes/chatRoutes';
import { createProviderRoutes } from './routes/providerRoutes';
import { createUsageRoutes } from './routes/usageRoutes';

const app = express();
const orchestrator = new AIOrchestrator();

// Middleware
app.use(cors());
app.use(express.json({ limit: '5mb' }));

// Request logger (sanitized, NEVER logs API keys)
app.use((req: Request, res: Response, next: NextFunction) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    if (req.path.startsWith('/api')) {
      console.log(`[HTTP] ${req.method} ${req.path} -> ${res.statusCode} (${duration}ms)`);
    }
  });
  next();
});

// Optional API Key Protection
app.use((req: Request, res: Response, next: NextFunction) => {
  if (!env.EVE_API_KEY) {
    return next(); // No master key configured, allow personal local access
  }

  // Public health and root endpoints bypass master key
  if (req.path === '/' || req.path === '/health') {
    return next();
  }

  const authHeader = req.headers.authorization;
  const customKey = req.headers['x-eve-api-key'] as string | undefined;

  const providedKey = customKey || (authHeader?.startsWith('Bearer ') ? authHeader.slice(7) : undefined);

  if (!providedKey || providedKey !== env.EVE_API_KEY) {
    return res.status(401).json({
      success: false,
      error: 'Unauthorized. Provide a valid "X-EVE-API-KEY" header or Bearer token.'
    });
  }

  next();
});

// Routes
app.get('/', (req: Request, res: Response) => {
  res.status(200).json({
    app: 'EVE Personal AI Assistant Cloud Infrastructure',
    version: '1.0.0-phase3',
    status: 'online',
    zeroCostMode: env.ZERO_COST_MODE,
    priorityOrder: ['groq', 'gemini', 'openrouter', 'ollama'],
    endpoints: {
      interact: 'POST /api/assistant/interact',
      providersStatus: 'GET /api/providers/status',
      providersHealth: 'GET /api/providers/health',
      usageSummary: 'GET /api/usage/summary',
      usageProviders: 'GET /api/usage/providers'
    }
  });
});

app.get('/health', (req: Request, res: Response) => {
  res.status(200).json({ status: 'ok', timestamp: new Date().toISOString() });
});

app.use('/api/assistant', createChatRoutes(orchestrator));
app.use('/api/providers', createProviderRoutes(orchestrator));
app.use('/api/usage', createUsageRoutes(orchestrator));

// 404 handler
app.use((req: Request, res: Response) => {
  res.status(404).json({ success: false, error: `Endpoint ${req.method} ${req.path} not found` });
});

// Start HTTP Server
const server = app.listen(env.PORT, env.HOST, () => {
  console.log('================================================================');
  console.log('🤖 EVE AI Assistant — Zero-Cost Multi-Provider Backend');
  console.log(`🌐 Listening on http://${env.HOST}:${env.PORT}`);
  console.log(`🔒 Zero-Cost Enforcement : ${env.ZERO_COST_MODE ? 'ACTIVE (Budget ₹0/month)' : 'INACTIVE'}`);
  console.log('----------------------------------------------------------------');
  console.log(`  1. Groq Cloud          : ${env.GROQ_API_KEY ? `Configured [${maskKey(env.GROQ_API_KEY)}]` : 'Unconfigured (Awaiting Key in .env)'}`);
  console.log(`  2. Google Gemini       : ${env.GEMINI_API_KEY ? `Configured [${maskKey(env.GEMINI_API_KEY)}]` : 'Unconfigured (Awaiting Key in .env)'}`);
  console.log(`  3. OpenRouter (:free)  : ${env.OPENROUTER_API_KEY ? `Configured [${maskKey(env.OPENROUTER_API_KEY)}]` : 'Unconfigured (Awaiting Key in .env)'}`);
  console.log(`  4. Local Ollama        : Configured [${env.OLLAMA_BASE_URL}]`);
  console.log('================================================================');
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received. Shutting down gracefully...');
  server.close(() => process.exit(0));
});

process.on('SIGINT', () => {
  console.log('SIGINT received. Shutting down gracefully...');
  server.close(() => process.exit(0));
});

export { app, orchestrator };
