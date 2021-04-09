package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopSessionRI extends Remote
{
    public void logout(String uname) throws RemoteException;
}
