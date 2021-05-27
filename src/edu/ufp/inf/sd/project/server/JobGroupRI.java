package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.client.WorkerRI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobGroupRI extends Remote
{
    public void changeState(int state) throws IOException;
    public void saveResults(Integer workId, Integer result) throws RemoteException;
    public void notifyAllworkers() throws RemoteException;
    public int getJobId() throws RemoteException;
    public void addWorkers(WorkerRI wRI) throws IOException;
    public File downloadFile(String path) throws FileNotFoundException, RemoteException;
    public User getUser() throws RemoteException;
}
