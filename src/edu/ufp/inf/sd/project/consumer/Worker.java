package edu.ufp.inf.sd.project.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import edu.ufp.inf.sd.project.util.geneticalgorithm.CrossoverStrategies;
import edu.ufp.inf.sd.project.util.geneticalgorithm.GeneticAlgorithmJSSP;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker {
    private int workerID;
    private int jobiD;
    private String uname;
    private String pathToWork;
    private Channel channel;


    public Worker(int jobiD, Channel channel) {
        this.jobiD = jobiD;
        this.channel = channel;
    }

    public void startWork() {
       //============ Call GA ============
        System.out.println("startwork()");
            String queue = this.uname;
            String resultsQueue = this.uname + "_results";
            CrossoverStrategies strategy = CrossoverStrategies.ONE;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "GA is running for {0}, check queue {1}",
                    new Object[]{this.pathToWork,resultsQueue});
            GeneticAlgorithmJSSP ga = new GeneticAlgorithmJSSP(this.pathToWork, queue, strategy);
            ga.run();
    }

    public void getResults() throws IOException {
        System.out.println("Entrei no getResults\n\n");
        boolean run = true;
        DefaultConsumer client1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                String[] parameters = message.split(";");
                Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName() + ": Message received " + message);

                System.out.println("Resultados: " + message + "\n");

                while (!run) {
                    try {
                        long sleepMillis = 2000;
                        Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName() + ": sleep " + sleepMillis);
                        Thread.currentThread().sleep(sleepMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        channel.basicConsume(this.getUname() + "_results", true, client1);
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

    public int getWorkerID() {
        return workerID;
    }

    public int getJobiD() {
        return jobiD;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setPathToWork(String pathToWork) {
        this.pathToWork = pathToWork;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
