package edu.ufp.inf.sd.project.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
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

        channel.exchangeDeclare("JobGroup_" + this.jobId, BuiltinExchangeType.FANOUT);
    }


    public void changeState(int state, Channel channel) throws IOException {
        this.state = state;
        // State 1 = começar trabalho
        if(state == 1){
            String reply = "url;" + this.jobUrl;
          channel.basicPublish("JobGroup_" + this.jobId, "",null, reply.getBytes("UTF-8"));
        }
        else if(state == 0){
            for(Worker wRI : worker){
                //wRI.stopWorkers();
            }
        }

    }

    // sincronizar método
    public synchronized void saveResults(Integer workId, Integer result) throws RemoteException {
        this.results.put(workId, result);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "[JobGroup] Result {0} from Worker -> {1} successfully saved!",
                new Object[]{result,workId});

        // significa que todos os workers enviaram o resultado
        if(this.results.size() == this.worker.size()){
            //alterar para o valor menor
            for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
                if(this.bestResult < entry.getValue()){
                    this.bestResult = entry.getValue();
                }
            }
            notifyAllworkers();
        }
    }

    //Verificar se existe saldo suficiente para a inserção de mais workers
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

    public void notifyAllworkers() throws RemoteException {
        for(Worker worker : this.worker){
            if(this.bestResult == this.results.get(worker.getWorkerID())){
                //worker.addCredits(10);
                this.credits -= 10;
            }else{
                //worker.addCredits(1);
                this.credits -= 1;
            }
            //worker.getState("O melhor resultado para o Job: " + this.getJobId() + "foi = " + this.bestResult);
        }
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

    @Override
    public String toString() {
        return "JobGroupImpl{" +
                "jobId=" + jobId +
                ", joburl='" + jobUrl + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
