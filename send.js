// send.js
const amqp = require('amqplib');

async function sendMsg({ queue = 'casaPago', message = { casaID: 'casa001' } } = {}) {
  try {
    const connection = await amqp.connect('amqp://localhost');
    const channel = await connection.createChannel();

    await channel.assertQueue(queue, { durable: false });
    channel.sendToQueue(queue, Buffer.from(JSON.stringify(message)));

    console.log(`[x] Mensaje enviado a [${queue}]: ${JSON.stringify(message)}`);

    setTimeout(() => connection.close(), 500);
  } catch (error) {
    console.error('Error al enviar el mensaje:', error.message);
  }
}

// Si este archivo es ejecutado directamente con argumentos CLI (por consola)
if (require.main === module) {
  const [queue, msg] = process.argv.slice(2);
  let parsedMsg = {};

  try {
    parsedMsg = msg ? JSON.parse(msg) : undefined;
  } catch (err) {
    console.warn('Mensaje no es un JSON válido, usando como string plano');
    parsedMsg = msg;
  }

  sendMsg({
    queue: queue || undefined,
    message: parsedMsg || undefined,
  });
}

// Para uso externo, puedes importar esta función:
// const { sendMsg } = require('./send');
// sendMsg({ queue: 'nombreDeLaCola', message: { ... } });
module.exports = { sendMsg };

