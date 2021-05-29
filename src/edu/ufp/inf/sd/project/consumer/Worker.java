package edu.ufp.inf.sd.project.consumer;

import com.rabbitmq.client.Channel;

public class Worker {
    private int workerID;
    private int jobiD;
    private String pathToWork;
    private Channel channel;


    public Worker(int jobiD, Channel channel) {
        this.jobiD = jobiD;
        this.channel = channel;
    }

    public void startWork() {
        //chamar função de GA;
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
}
