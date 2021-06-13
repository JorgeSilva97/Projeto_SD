package edu.ufp.inf.sd.project.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.project.consumer.Worker;
import edu.ufp.inf.sd.project.server.User;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobGroup
{

    private int jobId;
    private User user;
    private String jobUrl;
    private String strategy;
    private int state; // 1 - Ativo, 2 - Pausado, 0 - Finalizado
    private int credits;
    private int bestResult;
    private Channel channel;
    //array de workers disponiveis
    private ArrayList<Worker> worker = new ArrayList<>();
    HashMap<Integer, Integer> results = new HashMap<>();

    public JobGroup(int jobId, User user, String jobUrl, String strategy, int state, int credits, Channel channel) throws IOException {
        this.jobId = jobId;
        this.user = user;
        this.jobUrl = jobUrl;
        this.strategy = strategy;
        this.state = state;
        this.credits = credits;
        this.channel = channel;

        channel.exchangeDeclare("JobGroup_" + this.jobId, BuiltinExchangeType.FANOUT);
    }


    /**
     * Alterar o estado do JobGroup
     * @param state estado para qual vai ser alterado
     * @param channel
     * @throws IOException
     */
    public void changeState(int state, Channel channel) throws IOException {
        this.state = state;
        // State 1 = começar trabalho
        if(state == 1){
            String reply = "url;" + this.jobUrl;
            channel.basicPublish("JobGroup_" + this.jobId, "",null, reply.getBytes("UTF-8"));
            //this.saveResults();
        }
        else if(state == 0){
            for(Worker wRI : worker){
                //wRI.stopWorkers();
            }
        }

    }

    /**
     * Recebe resultados dos workers
     * @throws RemoteException
     */
    public synchronized void saveResults() throws IOException {
        //Função para receber os dados dos workers e guardar
        boolean run = true;
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x]- Received from Server\n'" + message + "'");
            String[] parameters = message.split("'");

            this.results.put(Integer.parseInt(parameters[0]),Integer.parseInt(parameters[1]));
            System.out.println("Result -> " + parameters[1] + " sended by: " + parameters[0] + "\n");

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
        channel.basicConsume("jssp_ga_results", true, deliverCallback, consumerTag -> {
        });


    }

    /**
     * Adicionar workers ao array de workers do JobGroup
     * @param worker
     * @throws IOException
     */
    public void addWorkers(Worker worker) throws IOException {
        if(this.credits > this.worker.size() + 10) {
            this.worker.add(worker);
            //worker.getPath(this.getJobUrl());
            //wRI.getState("Worker adicionado com sucesso ao job ->" + this.getJobId() +  "!!!");
        }else{
            //wRI.getState("Não foi possivel adicionar mais workers por falta de créditos");
        }
        //enviar trabalho para os workers
    }


    //objeto do ficheiro
    public File downloadFile(String path) throws FileNotFoundException, RemoteException {
        //ler o ficheiro
        File myObj = new File(path);
        return myObj;
    }

    public User getUser(){
        return this.user;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public ArrayList<Worker> getworker() {
        return worker;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "JobGroupImpl{" +
                "jobId=" + jobId +
                ", joburl='" + jobUrl + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
