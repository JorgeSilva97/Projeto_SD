package edu.ufp.inf.sd.project.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.client.WorkerImpl;
import edu.ufp.inf.sd.project.client.WorkerRI;
import edu.ufp.inf.sd.project.server.SessionRI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerRunnable implements Runnable {

    public Worker worker;

    public WorkerRunnable(int jobiD, Channel channel) {
        this.worker = new Worker(jobiD, channel);
    }

    @Override
    public void run() {
        try {
            Worker worker = this.worker;
            Channel channel = this.worker.getChannel();
            //Declaração de uma nova queue que vai ser usada pelo worker para receber informação do JobGroup associado
            channel.queueDeclare("worker_" + this.worker.getWorkerID(), false, false, false, null);
            //Bind da sua queue para o exchange do JobGroup associado
            channel.queueBind("worker_" + this.worker.getWorkerID(), "JobGroup_" + this.worker.getJobiD(), "");

            boolean run = true;
            DefaultConsumer client = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    String[] parameters = message.split(";");
                    Logger.getAnonymousLogger().log(Level.INFO, Thread.currentThread().getName() + ": Message received " + message);

                    switch (parameters[0]) {
                        case "url":
                            System.out.println("url received!\n");
                            worker.setPathToWork(parameters[1]);
                            worker.startWork();
                            worker.getResults();
                            break;

                        case "stop":
                            System.out.println("worker will stop now!\n");
                            //stopWork;
                            break;
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
                }
            };
            channel.basicConsume("worker_" + this.worker.getWorkerID(), true, client);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
