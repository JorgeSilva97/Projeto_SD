package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.FactoryRI;
import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.server.SessionRI;
import edu.ufp.inf.sd.project.server.User;
import edu.ufp.inf.sd.project.util.rmisetup.SetupContextRMI;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class JobShopClient {

    /**
     * Context for connecting a RMI client MAIL_TO_ADDR a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private FactoryRI factoryRI;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi.PROJETO.server.Project <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            JobShopClient hwc=new JobShopClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public JobShopClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(JobShopClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);

                
                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                factoryRI = (FactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return factoryRI;
    }
    
    private void playService() {
        try {
            /*//============ Call TS remote service ============
            String jsspInstancePath = "edu/ufp/inf/sd/project/data/la01.txt";
            int makespan = this.factoryRI.runTS(jsspInstancePath);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "[TS] Makespan for {0} = {1}",
                    new Object[]{jsspInstancePath,String.valueOf(makespan)});


            //============ Call GA ============
            String queue = "jssp_ga";
            String resultsQueue = queue + "_results";
            CrossoverStrategies strategy = CrossoverStrategies.ONE;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    "GA is running for {0}, check queue {1}",
                    new Object[]{jsspInstancePath,resultsQueue});
            GeneticAlgorithmJSSP ga = new GeneticAlgorithmJSSP(jsspInstancePath, queue, strategy);
            ga.run();*/

            //==========================================================================================================

            /*this.factoryRI.register("jorge", "ufp");

            SessionRI sessionRI = this.factoryRI.login("jorge", "ufp");

            if (sessionRI != null)
            {
                //CRIAR JOB'S

            }*/

            String username, pw;
            SessionRI sessionRI = null;
            SessionInfo sessionInfo = null;
            System.out.println("1 - Register and Login");
            System.out.println("2 - Login");

            //SCAN FOR OPTION
            Scanner myObj = new Scanner(System.in);
            String opt = myObj.nextLine();

            switch (opt){
                case "1":
                    System.out.print("Username: ");
                    username = myObj.nextLine();
                    System.out.print("Password: ");
                    pw = myObj.nextLine();
                    factoryRI.register(username,pw);
                    sessionRI = factoryRI.login(username,pw);
                    sessionInfo = new SessionInfo(sessionRI,username);
                    menu(sessionInfo);
                    break;
                case "2":
                    System.out.print("Username: ");
                    username = myObj.nextLine();
                    System.out.print("Password: ");
                    pw = myObj.nextLine();
                    sessionRI = factoryRI.login(username,pw);
                    sessionInfo = new SessionInfo(sessionRI,username);
                    menu(sessionInfo);
                    break;
            }



            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR finish, GOODBYE. ;)");

        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void menu(SessionInfo sessionInfo) throws RemoteException {
        int credits = 0;
        for(User u: this.factoryRI.getDb().getUsers()){
            if(u.getUname().compareTo(sessionInfo.getUsername())==0){
                credits = u.getCredits();
            }
        }


        System.out.println("User: " + sessionInfo.getUsername()+"\tCredits: " + credits+"\n");
        System.out.println("1 - Create JobGroup");
        System.out.println("2 - List JobGroup");
        System.out.println("3 - Join JobGroup");
        System.out.println("4 - Delete JobGroup");
        System.out.println("5 - Pause JobGroup");
        System.out.println("6 - Logout");

        Scanner myObj = new Scanner(System.in);
        String opt = myObj.nextLine();

        switch (opt){
            //CREATE TASK
            case "1":
                System.out.println("What strategy do you want to use? (1 - TS or 2 - GA)");
                String opt2 = myObj.nextLine();

                if(opt2.compareTo("1") == 0 || opt2.compareTo("2") == 0){
                    System.out.println("Path to file");
                    String path = myObj.nextLine();
                    if(opt2.compareTo("1") == 0)
                    sessionInfo.getSessionRI().createJobGroup(sessionInfo.getUsername(),path, "TS");
                    else
                        sessionInfo.getSessionRI().createJobGroup(sessionInfo.getUsername(),path, "GA");

                }
                System.out.println();
                break;
            //LIST JobGroups
            case "2":
                System.out.println("Jobs: ");
                for (Map.Entry me : factoryRI.getDb().getJobgroups().entrySet()) {
                    System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                }

                break;
            //JOIN JobGroup
            case "3":
                System.out.println("How many workers do you want to make available?");
                int workers = myObj.nextInt();
                // Criar workers
                for (Map.Entry me : factoryRI.getDb().getJobgroups().entrySet()) {
                    System.out.println("Key: " + me.getKey() + " & Value: " + me.getValue());
                }
                System.out.println("What task do you want to join?");
                int jobId = myObj.nextInt();

                for(int i = 0; i < workers; i++){
                    Worker worker = new Worker(this.factoryRI.getDb().getWorkers().size()+1, sessionInfo.getUsername());
                    this.factoryRI.getDb().addWorkers(jobId,worker);
                    worker.workTS(this.factoryRI.getDb().getJobgroups().get(jobId), this.factoryRI);
                }

/*                for(User user: factoryRI.getDb().getUsers()){
                    if(user.getUname().compareTo(sessionInfo.getUsername()) == 0){

                        observer = new ObserverImpl(factoryRI.getDb().getTasks().get(taskId),user);

                        String strategy = ((SubjectRI)factoryRI.getDb().getTasks().get(taskId)).getState().getStrategy();
                        if(strategy.compareTo("1") == 0){
                            observer.workOnTask1v2();
                            //observer.workOnTask1Threaded();
                        } else if (strategy.compareTo("2") == 0){
                            observer.workOnTask2v2();
                        } else  if (strategy.compareTo("3") == 0){
                            observer.workOnTask3();
                        }


                    }
                }*/
                break;
            //DELETE TASK
            /*case "4":
                System.out.print("Which task do you want to delete? ");
                int id = Integer.parseInt(myObj.nextLine());
                sessionInfo.getSessionRI().deleteTask(id,sessionInfo.getUsername());
                System.out.println();
                tasksMenu(sessionInfo);
                break;
            //PAUSE TASK
            case "5":
                System.out.println("Which task do you want to pause? ");
                int task = myObj.nextInt();
                ((SubjectRI)factoryRI.getDb().getTasks().get(task)).setState("PAUSE","");
                tasksMenu(sessionInfo);
                break;
            //LOGOUT
            case "6":
                sessionInfo.getSessionRI().logout(sessionInfo.getUsername());
                break;*/
        }
    }
}
