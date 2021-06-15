package edu.ufp.inf.sd.project.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.project.client.WorkerRI;
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
        channel.queueDeclare(this.jobId + "_results", false, false, false, null);
    }


    /**
     * Alterar o estado do JobGroup
     * @param state estado para qual vai ser alterado
     * @param channel
     * @throws IOException
     */
    public void changeState(int state, Channel channel) throws IOException {
        this.state = state;
        String reply = "";
        // State 1 = começar trabalho
        if(state == 1){
            this.readFile(channel, this.jobUrl);
            this.saveResults();
        }
        else if(state == 0){
           reply = "stop";
            channel.basicPublish("JobGroup_" + this.jobId, "",null, reply.getBytes("UTF-8"));
            bestResult();
        }

    }

    public void readFile(Channel channel, String file){
        String input = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String n = reader.readLine();
            input += n + "\n";
            String[] param = n.split(" ");
            for(int i = 0; i < Integer.parseInt(param[0].trim()); i++) {
                input += reader.readLine() + "\n";
            }
            reader.close();
            String reply = "url;" + input;

            channel.basicPublish("JobGroup_" + this.jobId, "",null, reply.getBytes("UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void bestResult() throws IOException {
        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
            if(this.bestResult < entry.getValue()){
                this.bestResult = entry.getValue();
            }
        }

        for(Worker worker : this.worker){
            if(this.bestResult == this.results.get(worker.getWorkerID())){
                String reply = "credits;" + 10;
                channel.basicPublish("JobGroup_" + this.jobId, String.valueOf(worker.getWorkerID()),null, reply.getBytes("UTF-8"));
                this.credits -= 10;
            }else{

                String reply = "credits;" + 1;
                channel.basicPublish("JobGroup_" + this.jobId, String.valueOf(worker.getWorkerID()),null, reply.getBytes("UTF-8"));
                this.credits -= 1;
            }

           // workerRI.getState("O melhor resultado para o Job: " + this.getJobId() + "foi = " + this.bestResult);
        }
    }

    /**
     * Recebe resultados dos workers
     * @throws RemoteException
     */
    public synchronized void saveResults() throws IOException {
        System.out.println("Save results jobgroup\n\n");
        //Função para receber os dados dos workers e guardar
        boolean run = true;
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x]- Received from Server\n'" + message + "'");
            String[] parameters = message.split(";");

            switch (parameters[0]){
                case "result":
                    int value = Integer.parseInt(parameters[2].trim());
                    this.results.put(Integer.parseInt(parameters[1]),value);
                    System.out.println("Result -> " + parameters[2] + " sended by: " + parameters[1] + "\n");
                    break;

                case "pausejob":
                    System.out.println("STOP");
                    this.changeState(0, channel);
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
        };
        channel.basicConsume(this.jobId + "_results", true, deliverCallback, consumerTag -> {
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
