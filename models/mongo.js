// mongo.js
const mongoose = require('mongoose');

async function connectDB({ maxRetries = 5, uri = process.env.MONGODB_URI || 'mongodb://localhost:27017/miapp' } = {}) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      await mongoose.connect(uri);
      console.log('[✅ MongoDB] Conexión establecida');
      return mongoose.connection;
    } catch (err) {
      console.error(`[❌ MongoDB] Intento ${i + 1}/${maxRetries} fallido:`, err.message);
      if (i < maxRetries - 1) {
        await new Promise(r => setTimeout(r, 3000));
      } else {
        throw new Error('No se pudo conectar a MongoDB después de varios intentos');
      }
    }
  }
}

function onConnectionEvents() {
  mongoose.connection.on('disconnected', () => {
    console.error('[⚠️ MongoDB] Conexión perdida — intentando reconectar...');
  });
  mongoose.connection.on('error', err => {
    console.error('[❌ MongoDB] Error en tiempo de ejecución:', err.message);
  });
}

// Función para verificar si está conectado
function isConnected() {
  return mongoose.connection.readyState === 1;
}

module.exports = { connectDB, onConnectionEvents, isConnected };
