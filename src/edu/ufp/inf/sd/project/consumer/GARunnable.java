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

public class GARunnable implements Runnable {

    public Worker worker;
    public String path;
    public Channel channel;

    public GARunnable(String path, Channel channel, Worker worker) {
        this.worker = worker;
        this.channel = channel;
        this.path = path;
    }

    @Override
    public void run() {
        //============ Call GA ============
        System.out.println("startwork()");
        String queue = this.worker.getUname();
        String resultsQueue = this.worker.getUname() + "_results";
        CrossoverStrategies strategy = CrossoverStrategies.ONE;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "GA is running for {0}, check queue {1}",
                new Object[]{this.path,resultsQueue});
        GeneticAlgorithmJSSP ga = new GeneticAlgorithmJSSP(this.path, queue, strategy);
        ga.run();
    }
}
