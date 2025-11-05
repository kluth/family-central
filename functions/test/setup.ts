/**
 * Jest test setup
 * Runs before all tests
 */

// Set test environment variables
process.env.FIRESTORE_EMULATOR_HOST = 'localhost:8080';
process.env.FIREBASE_AUTH_EMULATOR_HOST = 'localhost:9099';
process.env.FIREBASE_STORAGE_EMULATOR_HOST = 'localhost:9199';

// Mock Firebase Admin if not using emulator
jest.setTimeout(30000);

// Global test utilities
global.console = {
  ...console,
  error: jest.fn(), // Suppress error logs in tests
  warn: jest.fn(),  // Suppress warning logs in tests
  log: jest.fn(),   // Suppress info logs in tests
};
