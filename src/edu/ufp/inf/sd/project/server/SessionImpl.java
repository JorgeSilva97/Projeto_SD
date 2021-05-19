package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI {

    private int id;
    private FactoryImpl factoryImpl;
    private String user;
    //ArrayList<JobGroupImpl> jobs = new ArrayList<>();


    public SessionImpl(FactoryImpl factoryImpl, String uname) throws RemoteException {
        super();
        this.user = uname;
        this.factoryImpl = factoryImpl;
    }

    //getjobgroups

    @Override
    public void logout(String uname) throws RemoteException {
        if (this.factoryImpl.getDb().getSessions().containsKey(uname)) {
            this.factoryImpl.getDb().getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }


    public JobGroupImpl createJobGroup(String uname, String path, String strategy) throws RemoteException {
        for (User u : factoryImpl.getDb().getUsers()) {
            if (u.getUname().compareTo(uname) == 0 && u.getCredits() >= 100) {
                u.setCredits(u.getCredits() - 100);
                JobGroupImpl jobGroup = new JobGroupImpl(uname, path, strategy, 100);
                jobGroup.setJobId(this.factoryImpl.getDb().getJobgroups().size() + 1);
                this.factoryImpl.getDb().addJobGroup(jobGroup);
                //u.addCredits(100);
                return jobGroup;
            }
        }
        return null;
    }

    @Override
    public void removeJobGroup(String uname, int jobId) throws RemoteException {
        for (User u : factoryImpl.getDb().getUsers()) {
            if (u.getUname().compareTo(uname) == 0) {
                if (this.factoryImpl.getDb().getJobgroups().containsKey(jobId)) {
                    JobGroupImpl jobGroup = this.factoryImpl.getDb().getJobgroups().get(jobId);
                    if (jobGroup.getUser().equals(uname)) {
                        this.factoryImpl.getDb().removeJobGroup(jobId);
                    }
                }
            }
        }
    }

    public ArrayList<JobGroupImpl> listJobGroups() throws RemoteException{
        ArrayList<JobGroupImpl> jobGroups = new ArrayList<>();
        for(int i = 0; i < this.factoryImpl.getDb().getJobgroups().size(); i++){
            System.out.println("listjobgroups");
            if(this.factoryImpl.getDb().getJobgroups().get(i).getUser().equals(this.user))
                jobGroups.add(this.factoryImpl.getDb().getJobgroups().get(i));
        }
        return jobGroups;
    }
}
