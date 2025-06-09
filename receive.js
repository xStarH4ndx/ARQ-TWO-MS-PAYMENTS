// receive.js
const amqp = require('amqplib');
const mongoose = require('mongoose');
const { connectDB,CasaPago, Gasto } = require('./models');


async function main() {
  await connectDB();

  const connection = await amqp.connect('amqp://localhost');
  const channel = await connection.createChannel();
  const queue = 'casaPago';

  await channel.assertQueue(queue, { durable: false });

  console.log(`[x] Esperando mensajes en ${queue}...`);

  channel.consume(queue, async msg => {
    const content = JSON.parse(msg.content.toString());
    console.log(`[>] Mensaje recibido:`, content);

    // Suponiendo que envían { casaID: "casa001" }
    const casa = await CasaPago.findOne({ casaID: content.casaID }).lean();

    if (!casa) {
      console.log('[!] Casa no encontrada');
      return;
    }

    const gastos = await Gasto.find({ casaID: casa.casaID }).lean();

    console.log(`[✓] Casa: ${casa.nombre}, #Gastos: ${gastos.length}`);
    gastos.forEach(gasto => {
      console.log(`→ ${gasto.descripcion}: $${gasto.valorTotal}`);
    });

    // Aquí podrías enviar una respuesta por otra cola si fuera necesario
  }, { noAck: true });
}

main().catch(console.error);
