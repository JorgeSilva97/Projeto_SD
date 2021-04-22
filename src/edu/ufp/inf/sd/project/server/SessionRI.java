package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SessionRI extends Remote
{
    public void logout(String uname) throws RemoteException;
    public ArrayList<JobGroupImpl> createJobGroup(String uname, int length) throws RemoteException;
    public void listJobGoups() throws RemoteException;
    public ArrayList<JobGroupImpl> removeJobGroup(String uname) throws RemoteException;
}
