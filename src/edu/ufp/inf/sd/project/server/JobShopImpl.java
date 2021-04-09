package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.util.tabusearch.TabuSearchJSSP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JobShopImpl extends UnicastRemoteObject implements JobShopRI {

    private DBMockup db;
    private HashMap<String, JobShopSessionRI> sessions;

    public JobShopImpl() throws RemoteException
    {
        super();
        db = new DBMockup();
        sessions = new HashMap<>();
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
    public JobShopSessionRI login(String uname, String pword) throws RemoteException
    {
        if (db.exists(uname, pword))
        {
            if (!this.sessions.containsKey(uname))
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão iniciada com sucesso!");
                JobShopSessionRI jobShopSessionRI = new JobShopSessionImpl(this, uname);
                this.sessions.put(uname, jobShopSessionRI);
                return jobShopSessionRI;
            }
            else
            {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão já iniciada!");
                return this.sessions.get(uname);
            }

        }
        return null;
    }



  public HashMap<String, JobShopSessionRI> getSessions()
  {
      return sessions;
  }

    public DBMockup getDb()
    {
        return this.db;
    }
}
