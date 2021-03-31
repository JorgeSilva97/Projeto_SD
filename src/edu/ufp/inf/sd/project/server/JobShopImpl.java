package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.util.tabusearch.TabuSearchJSSP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JobShopImpl extends UnicastRemoteObject implements JobShopRI {

    public JobShopImpl() throws RemoteException {
        super();
    }

    @Override
    public int runTS(String jsspInstance) throws RemoteException {

        TabuSearchJSSP ts = new TabuSearchJSSP(jsspInstance);
        int makespan = ts.run();

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[TS] Makespan for {0} = {1}", new Object[]{jsspInstance,String.valueOf(makespan)});

        return makespan;
    }

}
