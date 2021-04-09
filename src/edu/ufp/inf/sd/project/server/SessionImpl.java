package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionImpl extends UnicastRemoteObject implements SessionRI
{


    private FactoryImpl factoryImpl;

    public SessionImpl(FactoryImpl factoryImpl, String uname) throws RemoteException {
        super();
        this.factoryImpl = factoryImpl;
    }

    @Override
    public void logout(String uname) throws RemoteException
    {
        if (this.factoryImpl.getSessions().containsKey(uname))
        {
            this.factoryImpl.getSessions().remove(uname);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LOGOUT efetuado pelo Utilizador @ {0}", uname);
        }
    }




}
