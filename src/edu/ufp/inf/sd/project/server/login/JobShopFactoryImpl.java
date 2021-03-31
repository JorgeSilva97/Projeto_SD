package edu.ufp.inf.sd.project.server.login;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class JobShopFactoryImpl extends UnicastRemoteObject implements JobShopFactoryRI
{

    private DBMockup db;
    private HashMap<String, JobShopSessionRI> sessions;

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
            return true;
        }
        return false;
    }

    @Override
    public JobShopSessionRI login(String uname, String pword) throws RemoteException
    {
        if (db.exists(uname, pword))
        {
            if (!this.sessions.containsKey(uname))
            {
                JobShopSessionRI jobShopSessionRI = new JobShopSessionImpl(this, uname);
                this.sessions.put(uname, jobShopSessionRI);
                return jobShopSessionRI;
            }
            else
                return this.sessions.get(uname);
        }
        return null;
    }

    public void remove (String uname)
    {
        this.sessions.remove(uname);
    }

    public DBMockup getDb()
    {
        return this.db;
    }
}
