package edu.ufp.inf.sd.project.producer;

/**
 * Consumer will keep running to listen for messages from queue and print them out.
 * 
 * DefaultConsumer is a class implementing the Consumer interface, used to buffer 
 * the messages pushed to us by the server.
 * 
 * Compile with RabbitMQ java client on the classpath:
 *  javac -cp amqp-client-4.0.2.jar RPCServer.java RPCClient.java
 * 
 * Run with need rabbitmq-client.jar and its dependencies on the classpath.
 *  java -cp .:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar Recv
 *  java -cp .:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar Producer
 * 
 * OR
 * export CP=.:amqp-client-4.0.2.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar
 * java -cp $CP Producer
 * java -cp %CP% Producer
 * 
 * The client will print the message it gets from the publisher via RabbitMQ.
 * The client will keep running, waiting for messages (Use Ctrl-C to stop it).
 * Try running the publisher from another terminal.
 *
 * Check RabbitMQ Broker runtime info (credentials: guest/guest4rabbitmq):
 *  http://localhost:15672/
 * 
 * 
 * @author rui
 */


import com.rabbitmq.client.*;
import edu.ufp.inf.sd.project.consumer.Consumer;
import edu.ufp.inf.sd.project.producer.DBMockup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Producer {

    //private final static String QUEUE_NAME = "helloqueue";

    public static void main(String[] argv) throws Exception {
        try {
            /* Open a connection and a channel, and declare the queue from which to consume.
            Declare the queue here, as well, because we might start the client before the publisher. */
            DBMockup db = new DBMockup();
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            //Use same username/passwd as the for accessing Management UI @ http://localhost:15672/
            //Default credentials are: guest/guest (change accordingly)
            factory.setUsername("guest");
            factory.setPassword("guest");
            //factory.setPassword("guest4rabbitmq");
            Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();
            Channel channelCoord=connection.createChannel();

            String resultsQueue = edu.ufp.inf.sd.project.consumer.Consumer.QUEUE_NAME + "_results";
            //String coordenatorQueue = Producer.QUEUE_JOB + "_results";

            channel.queueDeclare(resultsQueue, true, false, false, null);
            channel.exchangeDeclare(resultsQueue, BuiltinExchangeType.DIRECT);
            //channel.queueDeclare(Producer.QUEUE_NAME, false, false, false, null);
            //channelCoord.queueDeclare(Producer.QUEUE_JOB, false, false, false, null);
            //channelCoord.exchangeDeclare(coordenatorQueue, BuiltinExchangeType.DIRECT);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

             //The server pushes messages asynchronously, hence we provide a
            //DefaultConsumer callback that will buffer the messages until we're ready to use them.
            DefaultConsumer client = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException, UnsupportedEncodingException {
                    String message=new String(body, "UTF-8");
                    System.out.println(" [x] Connection received' [" + message + "]'");
                    String reply= "received";
                    /*if(db.getUser(message) != null){
                        reply = "Found";
                    }else
                    reply= "Not Found";*/

                    channel.basicPublish(Consumer.QUEUE_NAME + "_results", "123", null, reply.getBytes("UTF-8"));
                }
            };
            channel.basicConsume(Consumer.QUEUE_NAME, true, client);

            /*DefaultConsumer clientCoord = new DefaultConsumer(channelCoord){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message= new String(body, "UTF-8");
                    System.out.println(" [x] Received from Coord'" + message + "'");
                    String m = "Received!";
                    //channelCoord.basicPublish("", coordenatorQueue, null, m.getBytes("UTF-8"));
                }
            };
            channelCoord.basicConsume(Producer.QUEUE_JOB, true, clientCoord);*/


            /*DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(resultsQueue, true, deliverCallback, consumerTag -> { });*/

        } catch (Exception e){
            //Logger.getLogger(Recv.class.getName()).log(Level.INFO, e.toString());
            e.printStackTrace();
        }
    }
}
