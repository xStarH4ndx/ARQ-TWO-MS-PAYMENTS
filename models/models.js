// models.js
require('dotenv').config(); 
const mongoose = require('mongoose');
const { Schema } = mongoose;

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/miapp';

async function connectDB() {
  if (mongoose.connection.readyState === 0) {
    await mongoose.connect(MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log('[📦 MongoDB] Conectado a:', MONGODB_URI);
  }
}

const ItemCompraPayloadSchema = new Schema({
  productoID: String,
  nombreProducto: String,
  cantidad: Number,
  precioUnitario: Number,
  valorTotal: Number,
  esCompartido: Boolean,
  propietario: String,
});

const GastoCompraSchema = new Schema({
  itemCompraPlayload: [ItemCompraPayloadSchema],
  valorTotalCompartido: Number,
  valorTotalIndividual: Number,
});

const GastoServicioSchema = new Schema({
  fechaRenovacion: Date,
});

const CuotaGastoSchema = new Schema({
  gastoID: String,
  usuarioID: String,
  valorCuota: Number,
  estadoPago: String,
});

const GastoSchema = new Schema({
  casaID: String,
  tipo: String,
  descripcion: String,
  fechaRegistro: Date,
  valorTotal: Number,
  responsableUsuarioID: String,
  gastoCompra: GastoCompraSchema,
  gastoServicio: GastoServicioSchema,
  cuotas: [CuotaGastoSchema],
});

const CasaPagoSchema = new Schema({
  casaID: String,
  nombre: String,
  usuariosID: [String],
});

module.exports = {
  connectDB,
  CasaPago: mongoose.model('CasaPago', CasaPagoSchema),
  Gasto: mongoose.model('Gasto', GastoSchema),
  CuotaGasto: mongoose.model('CuotaGasto', CuotaGastoSchema),
};
