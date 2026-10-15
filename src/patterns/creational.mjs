/**
 * Creational Design Patterns: Singleton, Builder, Factory Method
 */

export class ConnectionPool {
  static instance = null;

  constructor(maxConnections = 10) {
    if (ConnectionPool.instance) {
      return ConnectionPool.instance;
    }
    this.maxConnections = maxConnections;
    this.connected = true;
    ConnectionPool.instance = this;
  }
}

export class HttpRequestBuilder {
  constructor(url) {
    this.url = url;
    this.method = 'GET';
    this.headers = {};
    this.body = null;
    this.timeout = 5000;
  }

  setMethod(method) {
    this.method = method.toUpperCase();
    return this;
  }

  setHeader(key, value) {
    this.headers[key] = value;
    return this;
  }

  setBody(body) {
    this.body = body;
    return this;
  }

  setTimeout(ms) {
    this.timeout = ms;
    return this;
  }

  build() {
    return {
      url: this.url,
      method: this.method,
      headers: { ...this.headers },
      body: this.body,
      timeout: this.timeout
    };
  }
}
