package edu.ufp.inf.sd.project.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
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
public class Consumer {

    /*+ name of the queue */
    public final static String QUEUE_NAME="jssp_ga";
    public final static String QUEUE_JOB="jssp_job";

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
             Channel channel=connection.createChannel();
        ) {
            /* We must declare a queue to send to; this is idempotent, i.e.,
            it will only be created if it doesn't exist already;
            then we can publish a message to the queue; The message content is a
            byte array (can encode whatever we need). */

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);


            //channel.queueDeclare(QUEUE_NAME + "_results", true,false, false, null);
            //channel.queueBind(QUEUE_NAME + "_results", QUEUE_NAME, "");

            String consExch = "consExch";
            //channel.exchangeDeclare(consExch, BuiltinExchangeType.DIRECT);

            //declarar outra queue para comunicação entre coordenador e servidor
            //channelCoord.queueDeclare(QUEUE_JOB, false, false, false, null);
            //channelCoord.exchangeDeclare(QUEUE_JOB, BuiltinExchangeType.DIRECT);

            //channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //sendJobGroup(channel);
            menu(channel);

            // Change strategy to CrossoverStrategies.TWO
            //sendMessage(channel, connection.getId());
            Thread.currentThread().sleep(2000);

            /*DefaultConsumer client = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message= new String(body, "UTF-8");
                    System.out.println(" [x] Received from Server['" + message + "']");

                    //menu(channel, connection);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, client);*/

            // Change strategy to CrossoverStrategies.THREE
            //sendMessage(channel, String.valueOf(CrossoverStrategies.THREE.strategy));
            //Thread.currentThread().sleep(2000);


            //sendJobGroup(channelCoord);

            // Stop the GA
            //sendMessage(channel, "stop");

        } catch (IOException | TimeoutException | InterruptedException e) {
            Logger.getLogger(Consumer.class.getName()).log(Level.INFO, e.toString());
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

    public static void menu(Channel channel) throws IOException {

        System.out.println("1 - Create JobGroup");
        System.out.println("2 - Start JobGroup");
        System.out.println("3 - List JobGroup");
        System.out.println("4 - Join JobGroup");
        System.out.println("5 - Delete JobGroup");
        System.out.println("6 - Pause JobGroup");
        System.out.println("7 - Logout");

        Scanner myObj = new Scanner(System.in);
        String opt = myObj.nextLine();

        switch (opt) {
            //CREATE TASK
            case "1":
                sendJobGroup(channel);

                break;
            //Start JobGroup
            /*case "2":
                printJobs();
                System.out.println("What Job do you want to start?");
                opt2 = myObj.nextLine();
                this.sessionRI.changeJobGroupState(Integer.parseInt(opt2), 1);
                break;*/
            //LIST JobGroups
            case "3":
                getJobs(channel);

                break;
            //JOIN JobGroup
            /*case "4":
                System.out.println("How many workers do you want to make available?");
                int workers = myObj.nextInt();
                // Criar workers
                for (JobGroupRI me : this.sessionRI.listJobGroups()) {
                    System.out.println("Key: " + me.getJobId() + " & Value: " + me);
                }
                System.out.println("What Job do you want to join?");
                int jobId = myObj.nextInt();

                createWorkers(workers, jobId);

                break;
            //DELETE TASK
            case "5":
                System.out.print("Which Job do you want to delete? ");
                if (!this.sessionRI.listJobGroups().isEmpty()) {
                    System.out.println(this.sessionRI.listJobGroups());
                }
                jobId = myObj.nextInt();
                this.sessionRI.removeJobGroup(this.sessionRI.getUser().getUname(), jobId);
                System.out.println("Job removed");
                break;
            //PAUSE TASK
            case "6":
                System.out.println("Which task do you want to pause? ");
                if (!this.sessionRI.listJobGroups().isEmpty()) {
                    System.out.println(this.sessionRI.listJobGroups());
                }
                jobId = myObj.nextInt();
                this.sessionRI.changeJobGroupState(jobId, 2);
                break;
            //LOGOUT
            case "7":
                this.sessionRI.logout(this.sessionRI.getUser().getUname());
                break;*/
        }
    }


    public static void getJobs(Channel channel) throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Name");
        String name = myObj.nextLine();

        System.out.println("Password:");
        String pass = myObj.nextLine();

        String message =";jobs;" + name + ";" + pass + ";";

        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Nome enviado para o servidor '" + message + "', aguarde resposta!");

        getProducerReply(channel, name);

    }

    private static void getProducerReply(Channel channel, String name) throws IOException {
        channel.queueDeclare(name, false, false, false, null);
        channel.exchangeDeclare("producer", BuiltinExchangeType.DIRECT);
        channel.queueBind(name, "producer", name);

        final String[] reply = {null};
        do{
            DefaultConsumer client = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    reply[0] = new String(body, "UTF-8");
                    System.out.println(" [x] Received from Server'" + reply[0] + "'");
                }
            };
            channel.basicConsume(name,true, client);
        }while(reply[0] == null);
    }

    public static void sendJobGroup(Channel  channel) throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Path to file");
        String path = myObj.nextLine();

        System.out.println("Credits: ");
        String credits = myObj.nextLine();
        Integer cred = Integer.parseInt(credits);

        System.out.println("Name");
        String name = myObj.nextLine();

        System.out.println("Password:");
        String pass = myObj.nextLine();

        String message = path + ";" + credits + ";" + name + ";" + pass + ";";


        channel.basicPublish("",QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent new JobGroup'" + message + "'");

        getProducerReply(channel, name);
    }
}
