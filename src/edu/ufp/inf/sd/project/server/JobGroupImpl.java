package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobGroupImpl implements JobGroupRI, Serializable
{

    private int jobId;
    private JobShopClientRI client;
    private User user;
    private String jobUrl;
    private String strategy;
    private int state; // 1 - Ativo, 2 - Pausado, 0 - Finalizado
    private int credits;
    private int bestResult;
    //array de workers disponiveis
    private ArrayList<WorkerRI> workerRI = new ArrayList<>();
    HashMap<Integer, Integer> results = new HashMap<>();

    public JobGroupImpl(User user, JobShopClientRI client, String joburl, String strategy, int credits) {
        this.user = user;
        this.client = client;
        this.jobUrl = joburl;
        this.strategy = strategy;
        this.credits = credits;
        this.state = 1;
        this.bestResult = 0;
    }

    public void changeState(int state) throws RemoteException {
        this.state = state;
        if(state == 1){
            for(WorkerRI wRI : workerRI){
                wRI.workTSS(this);
            }
        }
    }

    public void saveResults(Integer workId, Integer result) throws RemoteException {
        this.results.put(workId, result);
        if(this.bestResult < result)
            this.bestResult = result;

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "[JobGroup] Result {0} from Worker -> {1} successfully saved!",
                new Object[]{result,workId});
        //nao notificar quando receber, notificar depois de ter a melhor decisºao
    }

    //Verificar se existe saldo suficiente para a inserção de mais workers
    public void addWorkers(WorkerRI wRI) throws RemoteException {
        if(this.credits > this.workerRI.size() + 10) {
            this.workerRI.add(wRI);
            this.client.getState("Foi adicionado um novo Worker ao job->" + this.getJobId());
            wRI.getState("Worker adicionado com sucesso ao job ->" + this.getJobId() +  "!!!");
        }else{
            wRI.getState("Não foi possivel adicionar mais workers por falta de créditos");
            this.client.getState("JobGroup iD: " + this.jobId + " não tem mais espaço para workers!");
        }
        //enviar trabalho para os workers
    }

    public void notifyAllworkers(int result, int workiD) throws RemoteException {
        for(WorkerRI workerRI : this.workerRI){
            workerRI.update(result, workiD);
        }
    }

    public User getUser() throws RemoteException {
        return this.user;
    }

    public void setUser(JobShopClientRI user) {
        this.client = user;
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

    public ArrayList<WorkerRI> getWorkerRI() {
        return workerRI;
    }

    @Override
    public String toString() {
        return "JobGroupImpl{" +
                "jobId=" + jobId +
                ", user='" + user + '\'' +
                ", joburl='" + jobUrl + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
