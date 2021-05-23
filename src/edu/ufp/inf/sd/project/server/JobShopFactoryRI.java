package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopFactoryRI extends Remote {
    int runTS(String jsspInstance) throws RemoteException;
    public boolean register(String uname, String pword) throws RemoteException;
    public SessionRI login(String uname, String pword) throws RemoteException;
    public DBMockup getDb() throws RemoteException;
}
