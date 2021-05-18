package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SessionRI extends Remote
{
    public void logout(String uname) throws RemoteException;
    public JobGroupImpl createJobGroup(String uname, String path, String strategy) throws RemoteException;
    public void listJobGoups(String uname) throws RemoteException;
    public void removeJobGroup(String uname, int jobId) throws RemoteException;
}
