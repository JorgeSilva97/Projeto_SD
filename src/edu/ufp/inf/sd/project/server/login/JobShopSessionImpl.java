package edu.ufp.inf.sd.project.server.login;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class JobShopSessionImpl extends UnicastRemoteObject implements JobShopSessionRI
{


    private JobShopFactoryImpl digLibFactory;
    private String uname;

    public JobShopSessionImpl(JobShopFactoryImpl digLibFactory, String uname) throws RemoteException {
        super();
        this.digLibFactory = digLibFactory;
        this.uname = uname;
    }

    @Override
    public void logout() throws RemoteException
    {
        digLibFactory.remove(this.uname);
    }




}
