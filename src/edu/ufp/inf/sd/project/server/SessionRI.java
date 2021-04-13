package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface SessionRI extends Remote
{
    public void logout(String uname) throws RemoteException;
    public JobShopRI createJobGroup (String uname, int length) throws RemoteException;
    public ArrayList<String> listJobGoups () throws RemoteException;
}
