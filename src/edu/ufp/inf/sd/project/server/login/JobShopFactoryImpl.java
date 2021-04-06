package edu.ufp.inf.sd.project.server.login;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobShopFactoryImpl extends UnicastRemoteObject implements JobShopFactoryRI
{

    private DBMockup db;
    private HashMap<String, JobShopSessionImpl> sessions;

    public JobShopFactoryImpl() throws RemoteException
    {
        super();
        db = new DBMockup();
        sessions = new HashMap<>();
    }

    @Override
    public boolean register(String uname, String pword) throws RemoteException
    {
        if (!db.exists(uname, pword))
        {
            db.register(uname, pword);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Utilizador {0}, registado com sucesso", uname);
            return true;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Error, username already in use");
            return false;
        }
    }

    @Override
    public JobShopSessionRI login(String uname, String pword) throws RemoteException
    {
        if (db.exists(uname, pword))
        {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sessão iniciada com sucesso!");
            JobShopSessionImpl jobShopSession = sessions.get(uname);
            if (jobShopSession == null)
            {
                jobShopSession = new JobShopSessionImpl(db.getUser(uname), db);
                this.sessions.put(uname, jobShopSession);
                return jobShopSession;
            }
        } else {
            this.register(uname, pword);
            JobShopSessionImpl jobShopSession = new JobShopSessionImpl(db.getUser(uname), db);
            this.sessions.put(uname, jobShopSession);
            return jobShopSession;
        }
        return null;
    }

    public void removeSession(String name){
        this.sessions.remove(name);
        //imprimir hash
    }

}
