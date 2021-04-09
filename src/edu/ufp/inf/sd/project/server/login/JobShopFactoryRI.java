package edu.ufp.inf.sd.project.server.login;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopFactoryRI extends Remote
{
    public boolean register(String uname, String pword) throws RemoteException;
    public JobShopSessionRI login(String uname, String pword) throws RemoteException;
}
