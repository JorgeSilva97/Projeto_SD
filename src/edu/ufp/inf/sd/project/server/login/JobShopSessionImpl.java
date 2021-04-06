package edu.ufp.inf.sd.project.server.login;

import edu.ufp.inf.sd.project.server.JobShopImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class JobShopSessionImpl implements JobShopSessionRI
{

    DBMockup dbMockup;
    User client;

    public JobShopSessionImpl(User client, DBMockup dbMockup){
        //Export client
        try {
            Remote exportObject = java.rmi.server.UnicastRemoteObject.exportObject(this, 0);
            this.dbMockup = dbMockup;
            this.client = client;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(JobShopFactoryRI jobShopFactoryRI) throws RemoteException {
        jobShopFactoryRI.removeSession(this.client.getUname());
    }
}
