package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.SessionRI;

public class WorkerRunnable implements Runnable{

    public WorkerRI workerRI;
    private SessionRI sessionRI;
    private JobShopClientRI owner;

    public WorkerRunnable(int workID, String uname, SessionRI sessionRI, JobShopClientRI owner) {
        this.workerRI = new WorkerImpl(workID, uname, owner);
        this.sessionRI = sessionRI;
        this.owner = owner;
    }

    @Override
    public void run(){

    }
}
