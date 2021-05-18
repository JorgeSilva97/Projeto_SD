package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.FactoryRI;
import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.util.geneticalgorithm.CrossoverStrategies;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker
{
    int workerID;
    String uname;
    JobGroupImpl jobGroup;

    public Worker(int workerID, String uname) {
        this.workerID = workerID;
        this.uname = uname;
    }

    public void workTS(JobGroupImpl jobGroup, FactoryRI factoryRI) throws RemoteException {
        this.jobGroup = jobGroup;

        if(this.jobGroup.getStrategy().equals("TS")) {
            //============ Call TS remote service ============
            String jsspInstancePath = jobGroup.getJoburl();
            int makespan = factoryRI.runTS(jsspInstancePath);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "[TS] Makespan for {0} = {1}",
                    new Object[]{jsspInstancePath, String.valueOf(makespan)});
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
}
