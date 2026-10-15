/**
 * Behavioral Design Patterns: Observer, Strategy, Chain of Responsibility
 */

// 1. Observer Pattern
export class EventEmitter {
  constructor() {
    this.listeners = [];
  }
  subscribe(fn) {
    this.listeners.push(fn);
    return () => {
      this.listeners = this.listeners.filter(l => l !== fn);
    };
  }
  emit(data) {
    for (const fn of this.listeners) {
      fn(data);
    }
  }
}

// 2. Strategy Pattern
export class DiscountCalculator {
  constructor(strategy) {
    this.strategy = strategy;
  }
  setStrategy(strategy) {
    this.strategy = strategy;
  }
  calculate(price) {
    return this.strategy(price);
  }
}

export const DiscountStrategies = {
  regular: (p) => p,
  seasonal: (p) => p * 0.85,
  vip: (p) => p * 0.70
};

// 3. Chain of Responsibility Pattern
export class MiddlewareChain {
  constructor() {
    this.handlers = [];
  }
  use(handler) {
    this.handlers.push(handler);
    return this;
  }
  execute(context) {
    let index = 0;
    const next = () => {
      if (index < this.handlers.length) {
        const handler = this.handlers[index++];
        handler(context, next);
      }
    };
    next();
    return context;
  }
}
