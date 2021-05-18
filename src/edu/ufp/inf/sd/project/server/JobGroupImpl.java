package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;

import java.io.Serializable;
import java.util.ArrayList;

public class JobGroupImpl implements JobGroupRI, Serializable
{

    private int jobId;
    private String user;
    private String joburl;
    private String strategy;
    private int credits;
    //array de workers disponiveis
    ArrayList<Worker> workers = new ArrayList<>();

    public JobGroupImpl(String user, String joburl, String strategy, int credits) {
        this.user = user;
        this.joburl = joburl;
        this.strategy = strategy;
        this.credits = credits;
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
