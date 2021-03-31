package edu.ufp.inf.sd.project.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ufp.inf.sd.project.util.geneticalgorithm.CrossoverStrategies;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RabbitMQ speaks multiple protocols. This tutorial uses AMQP 0-9-1, which is
 * an open, general-purpose protocol for messaging. There are a number of
 * clients for RabbitMQ in many different languages. We'll use the Java client
 * provided by RabbitMQ.
 * <p>
 * Download client library (amqp-client-4.0.2.jar) and its dependencies (SLF4J
 * API and SLF4J Simple) and copy them into lib directory.
 * <p>
 * Jargon terms:
 * RabbitMQ is a message broker, i.e., a server that accepts and forwards messages.
 * Producer is a program that sends messages (Producing means sending).
 * Queue is a post box which lives inside a RabbitMQ broker (large message buffer).
 * Consumer is a program that waits to receive messages (Consuming means receiving).
 * The server, client and broker do not have to reside on the same host
 *
 * @author rui
 */
public class Producer {

    /*+ name of the queue */
    public final static String QUEUE_NAME="jssp_ga";

    public static void main(String[] argv) {
        //Connection connection=null;
        //Channel channel=null;

        /* Create a connection to the server (abstracts the socket connection,
           protocol version negotiation and authentication, etc.) */
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        //factory.setPassword("guest4rabbitmq");

        /* try-with-resources\. will close resources automatically in reverse order... avoids finally */
        try (//Create a channel, which is where most of the API resides
             Connection connection=factory.newConnection();
             Channel channel=connection.createChannel()
        ) {
            /* We must declare a queue to send to; this is idempotent, i.e.,
            it will only be created if it doesn't exist already;
            then we can publish a message to the queue; The message content is a
            byte array (can encode whatever we need). */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            // Change strategy to CrossoverStrategies.TWO
            sendMessage(channel, String.valueOf(CrossoverStrategies.TWO.strategy));
            Thread.currentThread().sleep(2000);

            // Change strategy to CrossoverStrategies.THREE
            sendMessage(channel, String.valueOf(CrossoverStrategies.THREE.strategy));
            Thread.currentThread().sleep(2000);

            // Stop the GA
            sendMessage(channel, "stop");

        } catch (IOException | TimeoutException | InterruptedException e) {
            Logger.getLogger(Producer.class.getName()).log(Level.INFO, e.toString());
        } /* The try-with-resources will close resources automatically in reverse order
            finally {
            try {
                // Lastly, we close the channel and the connection
                if (channel != null) { channel.close(); }
                if (connection != null) { connection.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } */
    }

    public static void sendMessage(Channel channel, String message) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
    }
}
