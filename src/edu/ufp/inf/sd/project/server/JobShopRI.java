package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.server.login.JobShopSessionRI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopRI extends Remote {
    int runTS(String jsspInstance) throws RemoteException;
    public boolean register(String uname, String pword) throws RemoteException;
    public JobShopSessionRI login(String uname, String pword) throws RemoteException;
}
