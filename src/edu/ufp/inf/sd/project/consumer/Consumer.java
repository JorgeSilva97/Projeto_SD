package edu.ufp.inf.sd.project.consumer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.project.consumer.WorkerRunnable;
import edu.ufp.inf.sd.project.util.threading.ThreadPool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
    ArrayList<Worker> workers = new ArrayList<>();

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

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);


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

    public static  void menu(Channel channel) throws IOException, InterruptedException {
        boolean session = true;
        Scanner myObj = new Scanner(System.in);
        System.out.println("Name");
        String name = myObj.nextLine();

        System.out.println("Password:");
        String pass = myObj.nextLine();
        channel.queueDeclare(name, false, false, true, null);
        channel.queueBind(name, "producer", name);

        while(session) {

            System.out.println("1 - Create JobGroup");
            System.out.println("2 - Start JobGroup");
            System.out.println("3 - List JobGroup");
            System.out.println("4 - Join JobGroup");
            System.out.println("5 - Delete JobGroup");
            System.out.println("6 - Pause JobGroup");
            System.out.println("7 - Logout");

            myObj = new Scanner(System.in);
            String opt = myObj.nextLine();

            switch (opt) {
                //CREATE TASK
                case "1":
                    sendJobGroup(channel, name, pass);
                    System.out.println("out of sendJob");
                    break;
                //Start JobGroup
            case "2":
                getmyJobs(channel, name, pass);
                System.out.println("What Job do you want to start?");
                opt = myObj.nextLine();

                startJob(channel, name, pass, Integer.parseInt(opt));

                break;
                //LIST JobGroups
                case "3":
                    getJobs(channel, name, pass);
                    break;
                //JOIN JobGroup
            case "4":
                System.out.println("How many workers do you want to make available?");
                int workers = myObj.nextInt();
                // Criar workers
                getJobs(channel, name, pass);
                Thread.sleep(2000);
                System.out.println("What Job do you want to join?");
                int jobId = myObj.nextInt();
                getiD(channel, name, jobId, workers);
                //createWorkers(workers, jobId);

                break;
            //DELETE TASK
            /*case "5":
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
                break;*/
            //LOGOUT
            case "7":
                session = false;
                break;
            }
        }
    }

    public static void startJob(Channel channel, String name, String pass, int jobiD) throws IOException {

        String message ="startjob;" + name + ";" + pass + ";" + jobiD;

        channel.basicPublish("", QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

        //System.out.println("getJobs");
    }

    public static void getJobs(Channel channel, String name, String pass) throws IOException {

        String message ="jobs;" + name + ";" + pass + ";";

        channel.basicPublish("", QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

        getProducerReply(channel, name);
        //System.out.println("getJobs");
    }

    public static void getmyJobs(Channel channel, String name, String pass) throws IOException {

        String message ="myjobs;" + name + ";" + pass + ";";

        channel.basicPublish("", QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

        getProducerReply(channel, name);
        //System.out.println("getJobs");
    }

    private static void getProducerReply(Channel channel, String name) throws IOException {

        boolean run = true;
        DeliverCallback deliverCallback=(consumerTag, delivery) -> {
            String message=new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received from Server\n'" + message + "'");

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
        channel.basicConsume(name, true, deliverCallback, consumerTag -> {
        });


        /*final String[] reply = {null};
        do{
            DefaultConsumer client = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    reply[0] = new String(body, "UTF-8");
                    System.out.println(" [x] Received from Server'" + reply[0] + "'");
                }
            };
            channel.basicConsume(name,true, client);
            //System.out.println(reply[0]);
        }while(reply[0] == null);*/
        //System.out.println("here");
    }

    public static void sendJobGroup(Channel  channel, String name, String pass) throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Path to file");
        String path = myObj.nextLine();

        System.out.println("Credits: ");
        String credits = myObj.nextLine();

        String message = path + ";" + credits + ";" + name + ";" + pass + ";";

        channel.basicPublish("",QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent new JobGroup'" + message + "'");

        getProducerReply(channel, name);
    }

    //Criar workers com thread.poll
    private static void createWorkers(Channel channel, String name, int id, int jobId, String[] workers) throws IOException {
        //create workers com threads


        ThreadPool threadPool = new ThreadPool(workers.length);
        ArrayList<WorkerRunnable> w = new ArrayList<>();
        for(int i = Integer.parseInt(workers[0]); i <= Integer.parseInt(workers[workers.length - 1]); i++){
            WorkerRunnable wR = new WorkerRunnable(jobId, channel);
            wR.worker.setWorkerID(i);
            w.add(wR);
        }
        for(int i = 0; i < workers.length; i++) {
            threadPool.execute(w.get(i));
        }
        System.out.println("Workers criados!\n");

        //sentWorkerToProducer(wR.worker, channel, name);
    }

    private static void sentWorkerToProducer(Worker wr, Channel channel, String name) throws IOException {
        String message = "worker;" + name + ";" + wr.getWorkerID() + ";" + wr.getJobiD();

        channel.basicPublish("",QUEUE_NAME, null, message.getBytes("UTF-8"));

        getProducerReply(channel, name);
    }

    private static void getiD(Channel channel, String name, int jobId, int workers) throws IOException, InterruptedException {

        String message ="getiD;" + name + ";" + jobId + ";" + workers;

        channel.basicPublish("",QUEUE_NAME, null, message.getBytes("UTF-8"));

        boolean run = true;

        DefaultConsumer client = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException{
                String message=new String(body, "UTF-8");
                String[] parameters = message.split("'");
                String[] ids = parameters[1].split(";");

                System.out.println(" [x] Received from Server\n'" + message + "'");
                createWorkers(channel, name, Integer.parseInt(parameters[1]), jobId, ids);

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
        channel.basicConsume(name, true, client);
    }
}
