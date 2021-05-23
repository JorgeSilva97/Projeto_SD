package edu.ufp.inf.sd.project.server;

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
    private String user;
    private String jobUrl;
    private String strategy;
    private int state; // 1 - Ativo, 2 - Pausado, 0 - Finalizado
    private int credits;
    private int bestResult;
    //array de workers disponiveis
    private ArrayList<WorkerRI> workerRI = new ArrayList<>();
    HashMap<Integer, Integer> results = new HashMap<>();

    public JobGroupImpl(String user, String joburl, String strategy, int credits) {
        this.user = user;
        this.jobUrl = joburl;
        this.strategy = strategy;
        this.credits = credits;
        this.state = 1;
        this.bestResult = 0;
    }

    public void changeState(int state){
        this.state = state;
    }

    public void saveResults(Integer workId, Integer result) throws RemoteException {
        this.results.put(workId, result);
        if(this.bestResult < result)
            this.bestResult = result;

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "[JobGroup] Result {0} from Worker -> {1} successfully saved!",
                new Object[]{result,workId});
        notifyAllworkers(result,workId);
    }

    public void addWorkers(WorkerRI wRI) throws RemoteException {
        this.workerRI.add(wRI);
        wRI.workTSS(this);
    }

    public void notifyAllworkers(int result, int workiD) throws RemoteException {
        for(WorkerRI workerRI : this.workerRI){
            workerRI.update(result, workiD);
        }
    }

    public String getUser() throws RemoteException {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
