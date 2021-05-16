package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI
{


    private FactoryImpl factoryImpl;
    //ArrayList<JobGroupImpl> jobs = new ArrayList<>();
    JobGroupImpl jobGroup;


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

    @Override
    public ArrayList<JobGroupImpl> createJobGroup(String uname, int length) throws RemoteException
    {
        ArrayList<JobGroupImpl> jobGroups = new ArrayList<>();
        for (User u : factoryImpl.getDb().getUsers())
        {
            if (u.getUname().compareTo(uname) == 0)
            {
                for (int i=0; i<length; i++)
                {
                    JobGroupImpl jobGroup = new JobGroupImpl();
                    //jobs.add(jobGroup);
                    jobGroups.add(jobGroup);
                    //u.addCredits(100);
                }
            }
        }
        return jobGroups;
    }

    @Override
    public ArrayList<JobGroupImpl> removeJobGroup(String uname, int jobId) throws RemoteException
    {
        for (User u : factoryImpl.getDb().getUsers())
        {
            if (u.getUname().compareTo(uname) == 0)
            {

            }

        }
        return null;
    }





}