package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FactoryRI extends Remote {
    int runTS(String jsspInstance) throws RemoteException;
    public boolean register(String uname, String pword) throws RemoteException;
    public SessionRI login(String uname, String pword) throws RemoteException;
}
