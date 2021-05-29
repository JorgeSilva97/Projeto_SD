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
import edu.ufp.inf.sd.project.consumer.Worker;
import edu.ufp.inf.sd.project.server.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


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
            String producerExch = "producer";

            Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();
            channel.exchangeDeclare(producerExch, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(Consumer.QUEUE_NAME, true, false, false, null);
            channel.queueDeclare("Workers_results", true, false, false, null);

            String resultsQueue = Consumer.QUEUE_NAME + "_results";

            //String coordenatorQueue = Producer.QUEUE_JOB + "_results";

            //channel.queueDeclare(resultsQueue, true, false, false, null);


            //channel.queueBind(Consumer.QUEUE_NAME + "_results", "consExch", "");
            //channel.queueDeclare(Producer.QUEUE_NAME, false, false, false, null);
            //channelCoord.queueDeclare(Producer.QUEUE_JOB, false, false, false, null);
            //channelCoord.exchangeDeclare(coordenatorQueue, BuiltinExchangeType.DIRECT);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            /*boolean run = true;
            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message=new String(delivery.getBody(), "UTF-8");
                String[] parameters = message.split(";");
                Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": Message received " +message);

                if(parameters[0].equals("jobs")){
                    String reply = db.getJobgroupsString();
                    String test = "hello";
                    channel.queueDeclare(parameters[1], false, false, false, null);
                    channel.basicPublish("producer", parameters[1], null, test.getBytes("UTF-8"));

                }else{
                    createJobGroup(db, channel, parameters);
                }
                System.out.println(db.getJobgroups());
                while (!run){
                    try {
                        long sleepMillis = 2000;
                        Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": sleep " +sleepMillis);
                        Thread.currentThread().sleep(sleepMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": Register Deliver Callback...");
            //Associate callback with channel queue
            channel.basicConsume(Consumer.QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });*/

             //The server pushes messages asynchronously, hence we provide a
            //DefaultConsumer callback that will buffer the messages until we're ready to use them.
            boolean run = true;
            DefaultConsumer client = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
                    String message=new String(body, "UTF-8");
                    String[] parameters = message.split(";");
                    Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": Message received " +message);
                    System.out.println(" [x] Received '" + message + "'");
                    System.out.println(parameters[0]);

                    if(parameters[0].equals("jobs")){
                        String reply = db.getJobgroupsString();

                        //channel.queueDeclare(parameters[1], false, false, false, null);
                        channel.basicPublish("producer", parameters[1], null, reply.getBytes("UTF-8"));
                        System.out.println("message sent to: " + parameters[1]);

                    }else if(parameters[0].equals("getiD")){
                        saveWorker(db, channel, parameters);

                    }else if(parameters[0].equals("worker")){
                        saveWorker(db, channel, parameters);
                    }else if(parameters[0].equals("myjobs")){
                        String reply = db.getmyJobs(parameters[1]);

                        channel.basicPublish("producer", parameters[1], null, reply.getBytes("UTF-8"));
                        System.out.println("message sent to: " + parameters[1]);
                    }else if(parameters[0].equals("startjob")){
                        db.getJobGroup(Integer.parseInt(parameters[3])).changeState(1, channel);
                    }else{
                        createJobGroup(db, channel, parameters);
                    }
                    System.out.println(db.getJobgroups());
                    while (!run){
                        try {
                            long sleepMillis = 2000;
                            Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName()+": sleep " +sleepMillis);
                            Thread.currentThread().sleep(sleepMillis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            channel.basicConsume(Consumer.QUEUE_NAME, true, client);

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

    private static void saveWorker(DBMockup db, Channel channel, String[] parameters) throws IOException {
       String ids = "";
        for(int i = 1; i <= Integer.parseInt(parameters[3]); i++){
           ids += (db.getWorkers().size() + i) + ";";
            Worker worker = new Worker(Integer.parseInt(parameters[2]), channel);
            worker.setWorkerID((db.getWorkers().size() + i));
            db.addWorkers(Integer.parseInt(parameters[2]), worker);
        }

        String reply = "Workers with id's: '" + ids + "' saved on Producer!";
        System.out.println(reply);
        channel.basicPublish("producer", parameters[1], null, reply.getBytes("UTF-8"));
    }

    private static void createJobGroup(DBMockup db, Channel channel, String[] parameters) throws IOException {
        User user = null;
        if(db.getUser(parameters[2]) == null){
            user = new User(parameters[2], parameters[3]);
        }else{
            user = db.getUser(parameters[2]);
        }

        JobGroup jobGroup = new JobGroup(db.getJobgroups().size() + 1,user, parameters[0], "GA",2, Integer.parseInt(parameters[1]), channel);
        db.addJobGroup(jobGroup);

        String reply = "JobGroup with id " + db.getJobgroups().size() +" created!";

        channel.basicPublish("producer", parameters[2], null, reply.getBytes("UTF-8"));
    }
}
