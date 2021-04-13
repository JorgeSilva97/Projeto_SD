package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface SessionRI extends Remote
{
    public void logout(String uname) throws RemoteException;
}
