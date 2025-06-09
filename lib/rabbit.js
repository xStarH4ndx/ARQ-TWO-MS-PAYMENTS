// lib/rabbit.js
const amqp = require('amqplib');
const { v4: uuid } = require('uuid');

async function getChannel() {
  const connection = await amqp.connect('amqp://localhost');
  const channel = await connection.createChannel();
  return { connection, channel };
}

async function rpcRequest(entity, payload) {
  const { connection, channel } = await getChannel();

  const rpcQueue = `get.${entity}`;     // p.e. get.CasaPago
  const replyQueue = (await channel.assertQueue('', { exclusive: true })).queue;
  const correlationId = uuid();

  return new Promise((resolve, reject) => {
    channel.consume(replyQueue, msg => {
      if (msg.properties.correlationId === correlationId) {
        const data = JSON.parse(msg.content.toString());
        resolve(data);
        setTimeout(() => connection.close(), 100);   // cierra conexión
      }
    }, { noAck: true });

    channel.sendToQueue(
      rpcQueue,
      Buffer.from(JSON.stringify(payload)),
      { correlationId, replyTo: replyQueue }
    );
  });
}

module.exports = { getChannel, rpcRequest };
