package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.client.WorkerImpl;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SessionRI extends Remote {
    public void logout(String uname) throws RemoteException;

    public boolean createJobGroup(String path, String strategy) throws RemoteException;

    public void removeJobGroup(String uname, int jobId) throws RemoteException;

    public ArrayList<JobGroupRI> listmyJobGroups() throws RemoteException;
    public ArrayList<JobGroupRI> listJobGroups() throws RemoteException;

    public User getUser() throws RemoteException;

    public void associateClient(JobShopClientRI clientRI) throws RemoteException;
    public void associateWorkers(WorkerRI workerRI, int jobID) throws IOException;
    public int getWorkersSize() throws RemoteException;
    public JobShopFactoryRI getJobShopFactoryImpl() throws RemoteException;
    public void changeJobGroupState(int jobID, int state) throws IOException;
    }
