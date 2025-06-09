// receive.Gasto.js
const { getChannel } = require('./lib/rabbit');
const { ItemCompra } = require('./models');
const { connectDB, onConnectionEvents, isConnected } = require('./mongo');

const queue = 'get.ItemCompra';

async function main() {
  await connectDB();
  onConnectionEvents();

  const { channel } = await getChannel();
  await channel.assertQueue(queue, { durable: false });
  console.log(`[💸] Servicio ItemCompra escuchando en ${queue}`);

  channel.consume(queue, async msg => {
    const { ids } = JSON.parse(msg.content.toString());

    if (!isConnected()) {
      const errorPayload = { error: 'db_unreachable', message: 'MongoDB no disponible' };
      channel.sendToQueue(
        msg.properties.replyTo,
        Buffer.from(JSON.stringify(errorPayload)),
        { correlationId: msg.properties.correlationId }
      );
      return;
    }

    try {
      const docs = await ItemCompra.find({ casaID: { $in: ids } }).lean();
      channel.sendToQueue(
        msg.properties.replyTo,
        Buffer.from(JSON.stringify(docs)),
        { correlationId: msg.properties.correlationId }
      );
    } catch (err) {
      console.error('[❌ MongoDB] Error en la query:', err.message);
      const errorPayload = { error: 'db_query', message: err.message };
      channel.sendToQueue(
        msg.properties.replyTo,
        Buffer.from(JSON.stringify(errorPayload)),
        { correlationId: msg.properties.correlationId }
      );
    }
  }, { noAck: true });
}

main().catch(err => {
  console.error('[💥 Fatal] No se pudo iniciar el servicio:', err.message);
  process.exit(1);
});



