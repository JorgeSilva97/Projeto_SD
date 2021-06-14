package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.JobShopClientRI;
import edu.ufp.inf.sd.project.util.tabusearch.TabuSearchJSSP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JobShopFactoryImpl extends UnicastRemoteObject implements JobShopFactoryRI {

    private DBMockup db;

    public JobShopFactoryImpl() throws RemoteException
    {
        super();
        this.db = new DBMockup();
    }

    @Override
    public boolean register(String uname, String pword) throws RemoteException
    {
        if (!db.exists(uname, pword))
        {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Utilizador {0}, registado com sucesso!", uname);
            db.register(uname, pword);
            return true;
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Erro, Utilizador já existente!");
        return false;
    }

    @Override
    public SessionRI login(String uname, String pword) throws RemoteException
    {
        if (db.exists(uname, pword))
        {
            if (!this.db.getSessions().containsKey(uname))
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão iniciada com sucesso!");
                SessionRI sessionRI = new SessionImpl(new User(uname,pword), this);
                this.db.getSessions().put(uname, sessionRI);
                return sessionRI;
            }
            else
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão já iniciada!");
                return this.db.getSessions().get(uname);
            }

        }
        return null;
    }

    public DBMockup getDb() throws RemoteException {
        return db;
    }

    public void setDb(DBMockup db) {
        this.db = db;
    }
}
