package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JobGroupRI extends Remote
{
    public void changeState(int state);
}
