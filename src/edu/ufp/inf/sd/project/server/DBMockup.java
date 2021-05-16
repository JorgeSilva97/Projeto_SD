package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMockup
{

    //private final ArrayList<Book> books;// = new ArrayList();
    private final ArrayList<User> users;// = new ArrayList();
    //arraylist de workers
    private HashMap<Integer, JobGroupImpl> jobgroups;
    ArrayList<Worker> workers = new ArrayList<>();

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
       // books = new ArrayList();
        this.users = new ArrayList();
        this.jobgroups = new HashMap<>();
        //Add one user
        users.add(new User("guest", "ufp"));
    }

    /**
     * Registers a new user.
     * 
     * @param u username
     * @param p passwd
     */
    public void register(String u, String p)
    {
        if (!exists(u, p))
            users.add(new User(u, p));
    }

    /**
     * Checks the credentials of an user.
     * 
     * @param u username
     * @param p passwd
     * @return
     */
    public boolean exists(String u, String p)
    {
        for (User usr : this.users)
        {
            if (usr.getUname().compareTo(u) == 0 && usr.getPword().compareTo(p) == 0)
                return true;
        }
        return false;

    }

    public void addJobGroup(JobGroupImpl jobGroup) throws RemoteException {
        jobgroups.put(jobGroup.getJobId(),jobGroup);
    }

    public String getJobGroup(Integer jobId){
        if(jobgroups.containsKey(jobId)){
            return jobgroups.get(jobId).getJoburl();
        }
        return null;
    }

    public void removeJobGroup(JobGroupImpl jobGroup){
        this.jobgroups.remove(jobGroup);
    }

    public void addWorkers(Integer jobId){

    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
