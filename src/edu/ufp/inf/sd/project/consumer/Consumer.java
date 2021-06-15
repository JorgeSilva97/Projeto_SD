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
    public final static String QUEUE_NAME = "consumer_queue";
    ArrayList<Worker> workers = new ArrayList<>();

    public static void main(String[] argv) {

        /* Create a connection to the server (abstracts the socket connection,
           protocol version negotiation and authentication, etc.) */
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        //factory.setPassword("guest4rabbitmq");

        /* try-with-resources\. will close resources automatically in reverse order... avoids finally */
        try (//Create a channel, which is where most of the API resides
             Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
        ) {
            /* We must declare a queue to send to; this is idempotent, i.e.,
            it will only be created if it doesn't exist already;
            then we can publish a message to the queue; The message content is a
            byte array (can encode whatever we need). */


            menu(channel);

            // Stop the GA
            //sendMessage(channel, "stop");

        } catch (IOException | TimeoutException | InterruptedException e) {
            Logger.getLogger(Consumer.class.getName()).log(Level.INFO, e.toString());
        }
    }

    public static void menu(Channel channel) throws IOException, InterruptedException {
        boolean session = true;

        //Criação de uma sessão para o consumer aceder ao producer e criar uma routingKey
        Scanner myObj = new Scanner(System.in);
        System.out.println("Name");
        String name = myObj.nextLine();
        System.out.println("Password:");
        String pass = myObj.nextLine();

        //Declaração de uma nova queue, com o seu nome. Onde vai receber mensagens do producer
        channel.queueDeclare(name, false, false, false, null);
        channel.queueDeclare(name + "_results", false, false, false, null);

        //Bind do exchange do producer com o routingkey = name
        channel.queueBind(name, "producer", name);

        while (session) {

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
                //CREATE JOB
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
                    //Recebe os jobs ativos para inserir workers
                    getJobs(channel, name, pass);
                    Thread.sleep(2000);
                    System.out.println("What Job do you want to join?");
                    int jobId = myObj.nextInt();
                    //envia pedido para o producer enviar id's disponiveis para criação
                    getiD(channel, name, jobId, workers);

                    break;
                //DELETE TASK
                case "5":
                    System.out.print("Which Job do you want to delete? ");
                    getmyJobs(channel, name, pass);
                    jobId = myObj.nextInt();

                    /**
                     *  Não implementado por falta de tempo, função identica ao create JobGroup
                     **/

                    System.out.println("Job removed");
                    break;
                //PAUSE TASK
                case "6":
                    System.out.println("Which task do you want to pause? ");
                    getmyJobs(channel, name, pass);
                    jobId = myObj.nextInt();
                    pauseJob(channel, name, pass, jobId);

                    break;
                //LOGOUT
                case "7":
                    session = false;
                    break;
            }
            getProducerReply(channel, name);
            Thread.sleep(2000);
        }
    }

    /**
     * Iniciar um jobGroup
     *
     * @param channel
     * @param name    nome do criador do Job
     * @param pass
     * @param jobiD   iD do Job que se pretende dar start
     * @throws IOException
     */
    public static void startJob(Channel channel, String name, String pass, int jobiD) throws IOException {
        //mensagem que vai ser enviada para o producer a informar o producer a começar um determinado job
        String message = "startjob;" + name + ";" + pass + ";" + jobiD;

        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
    }

    /**
     * Pausar um jobGroup
     *
     * @param channel
     * @param name    nome do criador do Job
     * @param pass
     * @param jobiD   iD do Job que se pretende dar start
     * @throws IOException
     */
    public static void pauseJob(Channel channel, String name, String pass, int jobiD) throws IOException {
        //mensagem que vai ser enviada para o producer a informar o producer a começar um determinado job
        String message = "pausejob;" + name + ";" + pass + ";" + jobiD;

        channel.basicPublish("", jobiD + "_results", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
    }

    /**
     * Pedido ao Producer para enviar os jobs ativos existentes
     *
     * @param channel
     * @param name
     * @param pass
     * @throws IOException
     */
    public static void getJobs(Channel channel, String name, String pass) throws IOException {

        //mensagem enviada para o producer com a palavra "jobs" no inicio para identificar o pedido
        String message = "jobs;" + name + ";" + pass + ";";
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
    }

    /**
     * Pedir ao producer para enviar os Jobs criados pelo consumer
     *
     * @param channel
     * @param name
     * @param pass
     * @throws IOException
     */
    public static void getmyJobs(Channel channel, String name, String pass) throws IOException {
        String message = "myjobs;" + name + ";" + pass + ";";
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

    }

    /**
     * Recebe a resposta do producer quando o routingKey colocado pelo Producer é igual ao seu nome
     *
     * @param channel
     * @param name
     * @throws IOException
     */
    private static void getProducerReply(Channel channel, String name) throws IOException {

        boolean run = true;
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x]- Received from Server\n'" + message + "'");
            String[] parameters = message.split("'");

            System.out.println("parameters.lenght = " + parameters.length);
            if (parameters.length > 1) {
                String[] ids = parameters[1].split(";");
                if (ids[0].equals("ids")) {
                    createWorkers(channel, Integer.parseInt(ids[1]), ids, name);

                }
            }

            while (!run) {
                try {
                    long sleepMillis = 2000;
                    Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName() + ": sleep " + sleepMillis);
                    Thread.currentThread().sleep(sleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(name, true, deliverCallback, consumerTag -> {
        });
    }

    /**
     * Envia a criação de um novo JobGroup para o Producer guarda na base de dados
     *
     * @param channel
     * @param name
     * @param pass
     * @throws IOException
     */
    public static void sendJobGroup(Channel channel, String name, String pass) throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Path to file");
        String path = myObj.nextLine();

        System.out.println("Credits: ");
        String credits = myObj.nextLine();

        //mensagem enviada para o producer com os dados de criação do Job
        String message = path + ";" + credits + ";" + name + ";" + pass + ";";

        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent new JobGroup'" + message + "'");

    }

    /**
     * Criar workers com os id's recebidos do Producer
     *
     * @param channel
     * @param jobId
     * @param workers
     * @throws IOException
     */
    private static void createWorkers(Channel channel, int jobId, String[] workers, String uname) throws IOException {
        //create workers com threads

        ThreadPool threadPool = new ThreadPool(workers.length - 2);
        ArrayList<WorkerRunnable> w = new ArrayList<>();
        for (int i = Integer.parseInt(workers[2]); i <= Integer.parseInt(workers[workers.length - 1]); i++) {
            WorkerRunnable wR = new WorkerRunnable(jobId, channel);
            wR.worker.setWorkerID(i);
            wR.worker.setUname(uname);
            w.add(wR);
        }
        System.out.println("Workers criados!\n");

        //Inicialização das threads dos workers.
        for (int i = 0; i < workers.length - 2; i++) {
            threadPool.execute(w.get(i));
        }
    }

    /**
     * Enviar pedido ao Producer de id's disponiveis para criação de workers
     *
     * @param channel
     * @param name
     * @param jobId
     * @param workers
     * @throws IOException
     * @throws InterruptedException
     */
    private static void getiD(Channel channel, String name, int jobId, int workers) throws IOException, InterruptedException {

        String message = "getiD;" + name + ";" + jobId + ";" + workers;

        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));

    }
}
