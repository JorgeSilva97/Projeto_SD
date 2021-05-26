package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.WorkerRI;

import java.rmi.RemoteException;

public class JobGroupRunnable implements Runnable{

    WorkerRI workerRI;
    JobGroupImpl jobGroup;

    public JobGroupRunnable(WorkerRI workerRI, JobGroupImpl jobGroup) {
        this.workerRI = workerRI;
        this.jobGroup = jobGroup;
    }

    @Override
    public void run() {
        try {
            workerRI.workTS(this.jobGroup);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
