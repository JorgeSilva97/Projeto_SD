package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;
import edu.ufp.inf.sd.project.util.tabusearch.TabuSearchJSSP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FactoryImpl extends UnicastRemoteObject implements FactoryRI {

    private DBMockup db;

    public FactoryImpl() throws RemoteException
    {
        super();
        this.db = new DBMockup();
    }

    @Override
    public int runTS(String jsspInstance) throws RemoteException
    {

        TabuSearchJSSP ts = new TabuSearchJSSP(jsspInstance);
        int makespan = ts.run();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[TS] Makespan for {0} = {1}", new Object[]{jsspInstance,String.valueOf(makespan)});

        return makespan;
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
            if (!this.getDb().getSessions().containsKey(uname))
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão iniciada com sucesso!");
                SessionRI sessionRI = new SessionImpl(this, uname);
                this.getDb().getSessions().put(uname, sessionRI);
                return sessionRI;
            }
            else
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão já iniciada!");
                return this.getDb().getSessions().get(uname);
            }

        }
        return null;
    }


    public DBMockup getDb()
    {
        return this.db;
    }
}
