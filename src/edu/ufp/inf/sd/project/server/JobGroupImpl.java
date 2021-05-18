package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;
import java.util.ArrayList;

public class JobGroupImpl implements JobGroupRI
{

    private int jobId;
    private String user;
    private String joburl;
    private String strategy;
    //array de workers disponiveis
    ArrayList<Worker> workers = new ArrayList<>();

    public JobGroupImpl(String user, String joburl, String strategy) {
        this.user = user;
        this.joburl = joburl;
        this.strategy = strategy;
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
