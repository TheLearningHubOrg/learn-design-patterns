import { ConnectionPool, HttpRequestBuilder } from './patterns/creational.mjs';
import { FileItem, DirectoryItem, LegacyXmlPaymentService, ModernPaymentAdapter } from './patterns/structural.mjs';
import { EventEmitter, DiscountCalculator, DiscountStrategies, MiddlewareChain } from './patterns/behavioral.mjs';
import { PATTERNS_STAGES, getCompletedCount, getProgressPercentage } from './roadmap.mjs';

console.log('======================================================');
console.log('  The Learning Hub - Gang of Four (GoF) Design Patterns');
console.log('======================================================');
console.log('Paradigms: SOLID Principles, Creational, Structural & Behavioral');
console.log('Craftsmanship: Clean Architecture & Decoupled Composition\n');

// 1. Creational Patterns
console.log('--- 1. Creational Patterns: Singleton & Fluent Builder ---');
const pool1 = new ConnectionPool(20);
const pool2 = new ConnectionPool(50);
console.log(`  Singleton Identity: (pool1 === pool2) -> ${pool1 === pool2}`);

const request = new HttpRequestBuilder('https://api.learninghub.org/v1/courses')
  .setMethod('POST')
  .setHeader('Authorization', 'Bearer token_xyz')
  .setBody({ title: 'Design Patterns' })
  .build();
console.log(`  Built Request Object: [${request.method} ${request.url}] (timeout: ${request.timeout}ms)`);

// 2. Structural Patterns
console.log('\n--- 2. Structural Patterns: Composite & Adapter ---');
const rootDir = new DirectoryItem('workspace');
const srcDir = new DirectoryItem('src');
rootDir.add(new FileItem('README.md', 1200));
srcDir.add(new FileItem('index.js', 450));
srcDir.add(new FileItem('app.css', 300));
rootDir.add(srcDir);
console.log(`  Composite Tree Total Size: ${rootDir.getSize()} bytes (recursive uniform aggregation)`);

const legacyXml = new LegacyXmlPaymentService();
const adapter = new ModernPaymentAdapter(legacyXml);
const paymentRes = adapter.pay(250, 'USD');
console.log(`  Adapter Translation: ${paymentRes.success ? 'SUCCESS' : 'FAILED'}`);

// 3. Behavioral Patterns
console.log('\n--- 3. Behavioral Patterns: Strategy & Middleware Chain ---');
const calc = new DiscountCalculator(DiscountStrategies.regular);
console.log(`  Regular Price ($100):  $${calc.calculate(100)}`);
calc.setStrategy(DiscountStrategies.vip);
console.log(`  VIP Strategy ($100):   $${calc.calculate(100)}`);

const chain = new MiddlewareChain();
chain.use((ctx, next) => { ctx.steps.push('Auth Middleware'); next(); });
chain.use((ctx, next) => { ctx.steps.push('Rate Limiter'); next(); });
chain.use((ctx, next) => { ctx.steps.push('Controller Handler'); next(); });

const pipeline = chain.execute({ steps: [] });
console.log(`  Middleware Pipeline Execution: [${pipeline.steps.join(' -> ')}]\n`);

// 4. 7-Stage Pedagogical Roadmap
console.log('--- 4. 7-Stage Design Patterns Masterclass Roadmap ---');
for (const stage of PATTERNS_STAGES) {
  const icon = stage.completed ? '[OK]' : '[..]';
  console.log(` ${icon} Stage ${stage.stageNumber}: ${stage.title}`);
  console.log(`      Focus: ${stage.domainFocus}`);
  console.log(`      Topics: ${stage.keyTopics.join(', ')}`);
}

const completed = getCompletedCount();
const percent = getProgressPercentage();
console.log('------------------------------------------------------');
console.log(`Progress: ${completed}/7 Stages Complete (${percent}%)`);
console.log('======================================================\n');
