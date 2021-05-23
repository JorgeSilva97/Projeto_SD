package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.WorkerRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobGroupRI extends Remote
{
    public void changeState(int state);
    public void saveResults(Integer workId, Integer result) throws RemoteException;
    public void notifyAllworkers(int result, int workiD) throws RemoteException;
    public int getJobId() throws RemoteException;
    public void addWorkers(WorkerRI wRI) throws RemoteException;
    public String getUser() throws RemoteException;
}
