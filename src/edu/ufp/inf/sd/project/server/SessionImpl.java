package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI
{

    private int id;
    private FactoryImpl factoryImpl;
    //ArrayList<JobGroupImpl> jobs = new ArrayList<>();




    public SessionImpl(FactoryImpl factoryImpl, String uname) throws RemoteException {
        super();
        this.factoryImpl = factoryImpl;
    }

    //getjobgroups

    @Override
    public void logout(String uname) throws RemoteException
    {
        if (this.factoryImpl.getSessions().containsKey(uname))
        {
            this.factoryImpl.getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }


    public JobGroupImpl createJobGroup(String uname, String path) throws RemoteException
    {
        for (User u : factoryImpl.getDb().getUsers())
        {
            if (u.getUname().compareTo(uname) == 0)
            {
                    JobGroupImpl jobGroup = new JobGroupImpl();
                    jobGroup.setJoburl(path);
                    jobGroup.setUser(uname);
                    this.factoryImpl.addJobGroup(jobGroup);
                    this.factoryImpl.getDb().addJobGroup(jobGroup);
                    //u.addCredits(100);
                return jobGroup;
            }
        }
        return null;
    }

    @Override
    public void removeJobGroup(String uname, int jobId) throws RemoteException
    {
        for (User u : factoryImpl.getDb().getUsers())
        {
            if (u.getUname().compareTo(uname) == 0)
            {
                if(this.factoryImpl.getJobGroups().contains(jobId)){
                    JobGroupImpl jobGroup = this.factoryImpl.getJobGroups().get(jobId);
                    if(jobGroup.getUser().equals(uname)){
                        this.factoryImpl.getJobGroups().remove(jobGroup);
                        this.factoryImpl.getDb().removeJobGroup(jobGroup);
                    }
                }
            }
        }
    }

    @Override
    public void listJobGoups(String uname) throws RemoteException {
        for(Entry  : this.factoryImpl.getJobGroups().entrySet()){
            if(job.getUser().equals(uname))
            System.out.println(job);
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
