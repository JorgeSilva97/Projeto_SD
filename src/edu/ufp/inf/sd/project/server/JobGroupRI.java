package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobGroupRI extends Remote
{
    public void changeState(int state) throws RemoteException;
    public void saveResults(Integer workId, Integer result) throws RemoteException;
    public void notifyAllworkers() throws RemoteException;
    public int getJobId() throws RemoteException;
    public void addWorkers(WorkerRI wRI) throws RemoteException;
    public User getUser() throws RemoteException;
}
