/**
 * Structural Design Patterns: Composite, Adapter, Decorator
 */

// 1. Composite Pattern
export class FileItem {
  constructor(name, size) {
    this.name = name;
    this.size = size;
  }
  getSize() {
    return this.size;
  }
}

export class DirectoryItem {
  constructor(name) {
    this.name = name;
    this.children = [];
  }
  add(item) {
    this.children.push(item);
    return this;
  }
  getSize() {
    return this.children.reduce((acc, c) => acc + c.getSize(), 0);
  }
}

// 2. Adapter Pattern
export class LegacyXmlPaymentService {
  processXml(xmlPayload) {
    return `<response status="success" id="tx-999">${xmlPayload}</response>`;
  }
}

export class ModernPaymentAdapter {
  constructor(legacyService) {
    this.legacyService = legacyService;
  }

  pay(amount, currency) {
    const xml = `<payment amount="${amount}" currency="${currency}" />`;
    const xmlRes = this.legacyService.processXml(xml);
    return { success: true, originalResponse: xmlRes };
  }
}
