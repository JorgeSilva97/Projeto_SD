package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobShopClientRI extends Remote {
    public void getState(String error) throws RemoteException;
    public void stopWorker(int workerId, int jobGroupId) throws RemoteException;
    public void addCredits(int credits, int workerId) throws RemoteException;
}
