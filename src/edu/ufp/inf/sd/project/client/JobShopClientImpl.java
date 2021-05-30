package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.server.JobGroupRI;
import edu.ufp.inf.sd.project.server.JobShopFactoryRI;
import edu.ufp.inf.sd.project.server.SessionRI;
import edu.ufp.inf.sd.project.util.rmisetup.SetupContextRMI;
import edu.ufp.inf.sd.project.util.threading.ThreadPool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JobShopClientImpl implements JobShopClientRI{

    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private JobShopFactoryRI jobShopFactoryRI;
    private SessionRI sessionRI = null;

    private ArrayList<WorkerRI> workerRI;
    

    public JobShopClientImpl(SetupContextRMI contextRMI) throws RemoteException {
        this.workerRI = new ArrayList<>();
        this.contextRMI = contextRMI;
        lookupService();
        Remote exportObject = java.rmi.server.UnicastRemoteObject.exportObject(this, 0);
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
                jobShopFactoryRI = (JobShopFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return jobShopFactoryRI;
    }

    public void playService() {
        try {
            String username, pw;
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
                    jobShopFactoryRI.register(username,pw);
                    sessionRI = jobShopFactoryRI.login(username,pw);
                    sessionRI.associateClient(this);
                    menu();
                    break;
                case "2":
                    System.out.print("Username: ");
                    username = myObj.nextLine();
                    System.out.print("Password: ");
                    pw = myObj.nextLine();
                    sessionRI = jobShopFactoryRI.login(username,pw);
                    sessionRI.associateClient(this);
                    menu();
                    break;
            }

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR finish, GOODBYE. ;)");

        } catch (RemoteException | FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void menu() throws IOException {

        while (this.sessionRI.getJobShopFactoryImpl().getDb().getSessions().containsKey(this.sessionRI.getUser().getUname())) {

            System.out.println("User: " + this.sessionRI.getUser().getUname() + "\tCredits: " + this.sessionRI.getUser().getCredits() + "\n");
            System.out.println("1 - Create JobGroup");
            System.out.println("2 - Start JobGroup");
            System.out.println("3 - List JobGroup");
            System.out.println("4 - Join JobGroup");
            System.out.println("5 - Delete JobGroup");
            System.out.println("6 - Pause JobGroup");
            System.out.println("7 - Logout");

            Scanner myObj = new Scanner(System.in);
            String opt = myObj.nextLine();

            switch (opt) {
                //CREATE TASK
                case "1":
                    System.out.println("What strategy do you want to use? (1 - TS or 2 - GA)");
                    String opt2 = myObj.nextLine();

                    if (opt2.compareTo("1") == 0 || opt2.compareTo("2") == 0) {
                        System.out.println("Path to file");
                        String path = myObj.nextLine();
                        if (opt2.compareTo("1") == 0)
                            this.sessionRI.createJobGroup(path, "TS");
                        else
                            this.sessionRI.createJobGroup(path, "GA");

                    }
                    System.out.println();
                    break;
                //Start JobGroup
                case "2":
                    printJobs();
                    System.out.println("What Job do you want to start?");
                    opt2 = myObj.nextLine();
                    this.sessionRI.changeJobGroupState(Integer.parseInt(opt2),1);
                    break;
                //LIST JobGroups
                case "3":
                    printJobs();

                    break;
                //JOIN JobGroup
                case "4":
                    System.out.println("How many workers do you want to make available?");
                    int workers = myObj.nextInt();
                    // Criar workers
                    for (JobGroupRI me : this.sessionRI.listJobGroups()) {
                        System.out.println("Key: " + me.getJobId() + " & Value: " + me);
                    }
                    System.out.println("What Job do you want to join?");
                    int jobId = myObj.nextInt();

                    createWorkers(workers, jobId);

                    break;
                    //DELETE TASK
                case "5":
                    System.out.print("Which Job do you want to delete? ");
                    if(!this.sessionRI.listJobGroups().isEmpty()) {
                        System.out.println(this.sessionRI.listJobGroups());
                    }
                    jobId = myObj.nextInt();
                    this.sessionRI.removeJobGroup(this.sessionRI.getUser().getUname(),jobId);
                    System.out.println("Job removed");
                    break;
                //PAUSE TASK
                case "6":
                    System.out.println("Which task do you want to pause? ");
                    if(!this.sessionRI.listJobGroups().isEmpty()) {
                        System.out.println(this.sessionRI.listJobGroups());
                    }
                    jobId = myObj.nextInt();
                    this.sessionRI.changeJobGroupState(jobId,2);
                    break;
                //LOGOUT
                case "7":
                    this.sessionRI.logout(this.sessionRI.getUser().getUname());
                    break;
            }
        }
    }

    private void printJobs() throws RemoteException {
        System.out.println("Jobs: ");
        for (JobGroupRI me : this.sessionRI.listJobGroups()) {
            System.out.println("Key: " + me.getJobId() + " & Value: " + me);
        }
    }

    private void createWorkers(int workers, int jobId) throws IOException {
        //create workers com threads

        int workersSize = this.sessionRI.getWorkersSize();
        ThreadPool threadPool = new ThreadPool(workers);
        ArrayList<WorkerRunnable> workersTh = new ArrayList<>();

        for (int i = 0; i < workers; i++) {
            WorkerRunnable wR = new WorkerRunnable(workersSize + i, this.sessionRI.getUser().getUname(), this.sessionRI, this);
            threadPool.execute(wR);
            this.sessionRI.associateWorkers(wR.workerRI, jobId);
            this.workerRI.add(wR.workerRI);
        }
    }

    public void addWorker(WorkerRI workerRI) throws RemoteException{
        this.workerRI.add(workerRI);
    }

    public void getState(String error) throws RemoteException{
        System.out.println(error);
    }


    public void stopWorker(int workerId, int jobGroupId) throws RemoteException{
        System.out.println("O worker - " + workerId + "acabou o seu serviço no Job -" + jobGroupId + "e vai ser desligado");
        this.workerRI.remove(workerId);
    }

    public void addCredits(int credits, int workerId) throws RemoteException{
        this.sessionRI.getUser().addCredits(credits);
        System.out.println("Worker -> " + workerId + "recebeu " + credits + "creditos");
    }
}
