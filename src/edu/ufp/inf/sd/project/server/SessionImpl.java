package edu.ufp.inf.sd.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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

    @Override
    public JobShopRI createJobGroup(String uname, int length) throws RemoteException
    {
        for (User u : factoryImpl.getDb().getUsers())
        {
            if (u.getUname().compareTo(uname) == 0)
            {
                //?????
                u.addCredits(100);
            }


        }
        return null;
    }

    @Override
    public ArrayList<String> listJobGoups() throws RemoteException
    {
        ArrayList<String> jobs = new ArrayList<>();
        //for (JobShopImpl jobShop : factoryImpl.ge)
        return null;
    }


}
