package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.WorkerImpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI, Serializable {

    //private DBMockup db;
    private User user;
    private JobShopFactoryImpl jobShopFactoryImpl;
    //ArrayList<JobGroupImpl> jobs = new ArrayList<>();


    public SessionImpl(User uname, JobShopFactoryImpl jobShopFactoryImpl) throws RemoteException {
        super();
        this.user = uname;
        this.jobShopFactoryImpl = jobShopFactoryImpl;
    }

    //getjobgroups

    @Override
    public void logout(String uname) throws RemoteException {
        if (this.jobShopFactoryImpl.getDb().getSessions().containsKey(uname)) {
            this.jobShopFactoryImpl.getDb().getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }


    public boolean createJobGroup(String path, String strategy) throws RemoteException {
            if (this.user.getCredits() >= 100) {
                user.setCredits(user.getCredits() - 100);
                JobGroupImpl jobGroup = new JobGroupImpl(this.user.getUname(), path, strategy, 100);
                jobGroup.setJobId(jobShopFactoryImpl.getDb().getJobgroups().size() + 1);
                this.jobShopFactoryImpl.getDb().addJobGroup(jobGroup);
                //u.addCredits(100);
                return true;
            }
        return false;
    }

    public void associateWorkers(int workerID, String uname, int jobID) throws RemoteException{
            this.jobShopFactoryImpl.getDb().addWorkers(jobID, new WorkerImpl(workerID, uname));
        }

    @Override
    public void removeJobGroup(String uname, int jobId) throws RemoteException {
        {
                if (jobShopFactoryImpl.getDb().getJobgroups().containsKey(jobId)) {
                    JobGroupRI jobGroup = jobShopFactoryImpl.getDb().getJobgroups().get(jobId);
                    if (jobGroup.getUser().equals(uname)) {
                        jobShopFactoryImpl.getDb().removeJobGroup(jobId);
                    }
                }
        }
    }

    public ArrayList<JobGroupRI> listJobGroups() throws RemoteException{
        ArrayList<JobGroupRI> jobGroups = new ArrayList<>();
        for(int i = 0; i < jobShopFactoryImpl.getDb().getJobgroups().size(); i++){
            System.out.println("listjobgroups");
            jobGroups.add(jobShopFactoryImpl.getDb().getJobgroups().get(i + 1));
            System.out.println(jobGroups);
        }
        return jobGroups;
    }

    public void changeJobGroupState(int jobID, int state) throws RemoteException{
        this.jobShopFactoryImpl.getDb().getJobGroup(jobID).changeState(state);
    }

    public User getUser() {
        return user;
    }

    public JobShopFactoryImpl getJobShopFactoryImpl() throws RemoteException {
        return jobShopFactoryImpl;
    }

    public void setJobShopFactoryImpl(JobShopFactoryImpl jobShopFactoryImpl) {
        this.jobShopFactoryImpl = jobShopFactoryImpl;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
