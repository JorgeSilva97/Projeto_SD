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
    private String uname;
    private ArrayList<JobGroupRI> jobGroups = new ArrayList<>();

    public WorkerImpl(int workerID, String uname) {
        this.workerID = workerID;
        this.uname = uname;
    }

    public void workTSS(JobGroupRI jobGroup) throws RemoteException{
        System.out.println(jobGroup);
        jobGroup.saveResults(this.workerID, 2302);
    }

    public void workTS(JobGroupImpl jobGroup, SessionRI sessionRI, JobShopFactoryRI jobShopFactoryRI) throws RemoteException {
        this.jobGroups.add(jobGroup);

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

    @Override
    public void update(int value, int workerID) throws RemoteException {
        System.out.println("New Value Received! ->" + value + "from: worker-" + workerID);
    }
}
