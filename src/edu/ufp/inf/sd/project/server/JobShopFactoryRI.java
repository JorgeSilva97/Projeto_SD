package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopFactoryRI extends Remote {
     boolean register(String uname, String pword) throws RemoteException;
     SessionRI login(String uname, String pword) throws RemoteException;
     DBMockup getDb() throws RemoteException;
}
