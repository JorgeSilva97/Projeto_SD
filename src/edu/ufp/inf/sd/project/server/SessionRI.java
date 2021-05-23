package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.WorkerImpl;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.lang.reflect.Array;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SessionRI extends Remote {
    public void logout(String uname) throws RemoteException;

    public boolean createJobGroup(String path, String strategy) throws RemoteException;

    public void removeJobGroup(String uname, int jobId) throws RemoteException;

    public ArrayList<JobGroupRI> listJobGroups() throws RemoteException;

    public User getUser() throws RemoteException;

    public void associateWorkers(int workerID, String uname, int jobID) throws RemoteException;

    public JobShopFactoryRI getJobShopFactoryImpl() throws RemoteException;
    public void changeJobGroupState(int jobID, int state) throws RemoteException;
    }
