package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JobShopSessionImpl extends UnicastRemoteObject implements JobShopSessionRI
{


    private JobShopImpl jobShopImpl;

    public JobShopSessionImpl(JobShopImpl jobShopImpl, String uname) throws RemoteException {
        super();
        this.jobShopImpl = jobShopImpl;
    }

    @Override
    public void logout(String uname) throws RemoteException
    {
        if (this.jobShopImpl.getSessions().containsKey(uname))
        {
            this.jobShopImpl.getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }




}
