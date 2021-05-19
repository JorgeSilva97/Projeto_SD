package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.Worker;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class simulates a DBMockup for managing users
 *
 * @author rmoreira
 *
 */
public class DBMockup implements Serializable {

    private final ArrayList<User> users;
    private HashMap<String, SessionRI> sessions;

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
        this.sessions = new HashMap<>();
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

    public JobGroupImpl getJobGroup(Integer jobId){
        if(jobgroups.containsKey(jobId)){
            return jobgroups.get(jobId);
        }
        return null;
    }

    public void removeJobGroup(int jobId){
        this.jobgroups.remove(jobId);
    }


    public void addWorkers(Integer jobId, Worker worker){
        this.getJobgroups().get(jobId).workers.add(worker);
        this.workers.add(worker);
    }

    public User getUser(String uname){
        for(User u : this.users){
            if(u.getUname().equals(uname))
                return u;
        }
        return null;
    }

    public HashMap<String, SessionRI> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, SessionRI> sessions) {
        this.sessions = sessions;
    }

    public HashMap<Integer, JobGroupImpl> getJobgroups() {
        return jobgroups;
    }

    public void setJobgroups(HashMap<Integer, JobGroupImpl> jobgroups) {
        this.jobgroups = jobgroups;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
