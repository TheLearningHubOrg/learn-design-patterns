/**
 * The Learning Hub - Gang of Four (GoF) & Enterprise Design Patterns Roadmap
 */

export const PATTERNS_STAGES = [
  {
    stageNumber: 1,
    title: "Object-Oriented Foundations & The SOLID Principles",
    domainFocus: "Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion",
    completed: true,
    keyTopics: ["Composition Over Inheritance", "Cohesion vs Coupling Metrics", "Inversion of Control (IoC)", "Interface Segregation Guardrails"]
  },
  {
    stageNumber: 2,
    title: "Creational Design Patterns: Instantiation Governance",
    domainFocus: "Singleton (Thread-Safe), Factory Method, Abstract Factory, Fluent Builder, Prototype Deep Cloning",
    completed: true,
    keyTopics: ["Singleton Reference Identity", "Factory Method Dynamic Construction", "Fluent Builder Director", "Prototype Deep Cloner"]
  },
  {
    stageNumber: 3,
    title: "Structural Design Patterns: Composition & Interfaces",
    domainFocus: "Adapter (Interface translation), Decorator, Facade, Proxy (Virtual & Protection), Composite (Tree aggregation)",
    completed: true,
    keyTopics: ["Composite Recursive Tree Aggregation", "Adapter Interface Bridging", "Decorator Dynamic Wrapping", "Flyweight Object Memory Reduction"]
  },
  {
    stageNumber: 4,
    title: "Behavioral Patterns I: Events, Algorithms & State",
    domainFocus: "Observer / Pub-Sub event dispatch, Strategy interchangeable algorithms, Command Undo/Redo, State machines",
    completed: true,
    keyTopics: ["Observer Synchronous Fanout", "Strategy Pluggable Algorithms", "Command Stack Undo/Redo", "State Machine Determinism"]
  },
  {
    stageNumber: 5,
    title: "Behavioral Patterns II: Workflows & Traversals",
    domainFocus: "Chain of Responsibility (Middleware), Mediator, Template Method, Iterator, Visitor (Double Dispatch), Memento",
    completed: true,
    keyTopics: ["Chain of Responsibility Handler Pipeline", "Mediator Loose Coupling", "Visitor Double Dispatch", "Memento State Snapshots"]
  },
  {
    stageNumber: 6,
    title: "Enterprise Architectural Patterns & Microservices",
    domainFocus: "Repository & Unit of Work, CQRS & Event Sourcing, Dependency Injection Container, Transactional Outbox",
    completed: true,
    keyTopics: ["Repository Data Decoupling", "CQRS Read/Write Segregation", "Event Sourcing Immutable State", "Transactional Outbox Pattern"]
  },
  {
    stageNumber: 7,
    title: "Staff Software Engineer Interview Handbook",
    domainFocus: "Pattern Selection Decision Trees, 6 Anti-Patterns & Refactoring, 25 Staff Interview Scenarios",
    completed: true,
    keyTopics: ["God Object Anti-Pattern Refactoring", "Golden Hammer Heuristics", "Pattern Decision Tree Selection", "Staff Interview Scenarios"]
  }
];

export function getCompletedCount() {
  return PATTERNS_STAGES.filter(s => s.completed).length;
}

export function getProgressPercentage() {
  return Math.round((getCompletedCount() / PATTERNS_STAGES.length) * 100);
}
