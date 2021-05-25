package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.JobGroupRI;
import edu.ufp.inf.sd.project.server.JobShopFactoryRI;
import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.server.SessionRI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerImpl implements WorkerRI {
    private int workerID;
    private JobShopClientRI jobShopClientRI;
    private String uname;
    private JobGroupRI jobGroups;

    public WorkerImpl(int workerID, String uname, JobShopClientRI jobShopClientRI) {
        this.jobShopClientRI = jobShopClientRI;
        this.workerID = workerID;
        this.uname = uname;
    }

    public void workTSS(JobGroupRI jobGroup) throws RemoteException{

        jobGroup.saveResults(this.workerID, 2302);
    }

    public void workTS(JobGroupImpl jobGroup, SessionRI sessionRI, JobShopFactoryRI jobShopFactoryRI) throws RemoteException {

        if(jobGroup.getStrategy().equals("TS")) {
            //============ Call TS remote service ============
            String jsspInstancePath = jobGroup.getJobUrl();
            int makespan = jobShopFactoryRI.runTS(jsspInstancePath);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "[TS] Makespan for {0} = {1}",
                    new Object[]{jsspInstancePath, String.valueOf(makespan)});
            sessionRI.getJobShopFactoryImpl().getDb().getJobGroup(jobGroup.getJobId()).saveResults(this.workerID, makespan);
        }else{
            /*//============ Call GA ============
            String queue = "jssp_ga";
            String resultsQueue = queue + "_results";
            CrossoverStrategies strategy = CrossoverStrategies.ONE;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "GA is running for {0}, check queue {1}",
                    new Object[]{jsspInstancePath,resultsQueue});
            GeneticAlgorithmJSSP ga = new GeneticAlgorithmJSSP(jsspInstancePath, queue, strategy);
            ga.run();*/
        }
    }

    public int getWorkerID() throws RemoteException {
        return workerID;
    }

    public void associateJobGroup(JobGroupRI jobGroup) throws RemoteException{
        this.jobGroups = jobGroup;
    }

    @Override
    public void update(int value, int workerID) throws RemoteException {
        System.out.println("New Value Received! ->" + value + "from: worker-" + workerID);
    }

    public void getState(String error) throws RemoteException{
        this.jobShopClientRI.getState(error);
    }
}
