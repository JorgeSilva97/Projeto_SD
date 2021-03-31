package edu.ufp.inf.sd.project.server.login;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface JobShopSessionRI extends Remote
{
    public void logout() throws RemoteException;
}
