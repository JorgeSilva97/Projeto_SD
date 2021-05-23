package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.WorkerImpl;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI, Serializable {

    //private DBMockup db;
    private User user;
    private JobShopFactoryRI jobShopFactoryRI;
    //ArrayList<JobGroupImpl> jobs = new ArrayList<>();


    public SessionImpl(User uname, JobShopFactoryRI jobShopFactoryRI) throws RemoteException {
        super();
        this.user = uname;
        this.jobShopFactoryRI = jobShopFactoryRI;
    }

    //getjobgroups

    @Override
    public void logout(String uname) throws RemoteException {
        if (this.jobShopFactoryRI.getDb().getSessions().containsKey(uname)) {
            this.jobShopFactoryRI.getDb().getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }


    public boolean createJobGroup(String path, String strategy) throws RemoteException {
            if (this.user.getCredits() >= 100) {
                user.setCredits(user.getCredits() - 100);
                JobGroupImpl jobGroup = new JobGroupImpl(this.user.getUname(), path, strategy, 100);
                jobGroup.setJobId(jobShopFactoryRI.getDb().getJobgroups().size() + 1);
                this.jobShopFactoryRI.getDb().addJobGroup(jobGroup);
                //u.addCredits(100);
                return true;
            }
        return false;
    }

    public void associateWorkers(int workerID, String uname, int jobID) throws RemoteException{
            this.jobShopFactoryRI.getDb().addWorkers(jobID, new WorkerImpl(workerID, uname));
        }

    @Override
    public void removeJobGroup(String uname, int jobId) throws RemoteException {
        {
                if (jobShopFactoryRI.getDb().getJobgroups().containsKey(jobId)) {
                    JobGroupRI jobGroup = jobShopFactoryRI.getDb().getJobgroups().get(jobId);
                    if (jobGroup.getUser().equals(uname)) {
                        jobShopFactoryRI.getDb().removeJobGroup(jobId);
                    }
                }
        }
    }

    public ArrayList<JobGroupRI> listJobGroups() throws RemoteException{
        ArrayList<JobGroupRI> jobGroups = new ArrayList<>();
        for(int i = 0; i < jobShopFactoryRI.getDb().getJobgroups().size(); i++){
            System.out.println("listjobgroups");
            jobGroups.add(jobShopFactoryRI.getDb().getJobgroups().get(i + 1));
            System.out.println(jobGroups);
        }
        return jobGroups;
    }

    public void changeJobGroupState(int jobID, int state) throws RemoteException{
        this.jobShopFactoryRI.getDb().getJobGroup(jobID).changeState(state);
    }

    public User getUser() {
        return user;
    }

    public JobShopFactoryRI getJobShopFactoryImpl() throws RemoteException {
        return jobShopFactoryRI;
    }

    public void setJobShopFactoryImpl(JobShopFactoryImpl jobShopFactoryImpl) {
        this.jobShopFactoryRI = jobShopFactoryImpl;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
