package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobGroupImpl implements JobGroupRI, Serializable
{

    private int jobId;
    private String user;
    private String joburl;
    private String strategy;
    private int state; // 1 - Ativo, 2 - Pausado, 0 - Finalizado
    private int credits;
    private int bestResult;
    //array de workers disponiveis
    ArrayList<Worker> workers = new ArrayList<>();
    HashMap<Integer, Integer> results = new HashMap<>();

    public JobGroupImpl(String user, String joburl, String strategy, int credits) {
        this.user = user;
        this.joburl = joburl;
        this.strategy = strategy;
        this.credits = credits;
        this.state = 1;
        this.bestResult = 0;
    }

    public void changeState(int state){
        this.state = state;
    }

    public void saveResults(Integer workId, Integer result){
        this.results.put(workId, result);
        if(this.bestResult < result)
            this.bestResult = result;

        Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                "[JobGroup] Result {0} from Worker -> {1}\n successfully saved!",
                new Object[]{result,workId});
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getJoburl() {
        return joburl;
    }

    public void setJoburl(String joburl) {
        this.joburl = joburl;
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

    @Override
    public String toString() {
        return "JobGroupImpl{" +
                "jobId=" + jobId +
                ", user='" + user + '\'' +
                ", joburl='" + joburl + '\'' +
                ", strategy='" + strategy + '\'' +
                '}';
    }
}
