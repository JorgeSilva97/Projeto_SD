package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.server.JobGroupRI;
import edu.ufp.inf.sd.project.server.JobShopFactoryRI;
import edu.ufp.inf.sd.project.server.SessionRI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerRI extends Remote, Serializable {
    public void workTS(String path) throws RemoteException;
    public void workTSS(JobGroupRI jobGroup) throws RemoteException;
    public void update(int value, int workerID) throws RemoteException;
    public void getState(String error) throws RemoteException;
    public void getPath(String path) throws IOException;
    public void associateJobGroup(JobGroupRI jobGroup) throws RemoteException;
    public void stopWorkers() throws RemoteException;
    public void addCredits(int credits) throws RemoteException;
    public int getWorkerID() throws RemoteException;
    public void startWork() throws RemoteException;
}
