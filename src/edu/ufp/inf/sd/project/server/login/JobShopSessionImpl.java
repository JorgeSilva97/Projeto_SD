package edu.ufp.inf.sd.project.server.login;

import edu.ufp.inf.sd.project.server.JobShopImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class JobShopSessionImpl extends UnicastRemoteObject implements JobShopSessionRI
{


    private JobShopImpl jobShopImpl;
    private String uname;

    public JobShopSessionImpl(JobShopImpl jobShopImpl, String uname) throws RemoteException {
        super();
        this.jobShopImpl = jobShopImpl;
        this.uname = uname;
    }

    @Override
    public void logout() throws RemoteException
    {
        jobShopImpl.remove(uname);
    }




}
