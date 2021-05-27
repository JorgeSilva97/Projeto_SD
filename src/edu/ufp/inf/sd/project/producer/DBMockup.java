package edu.ufp.inf.sd.project.producer;

import edu.ufp.inf.sd.project.client.WorkerRI;
import edu.ufp.inf.sd.project.server.JobGroupImpl;
import edu.ufp.inf.sd.project.server.JobGroupRI;
import edu.ufp.inf.sd.project.server.SessionRI;
import edu.ufp.inf.sd.project.server.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
    private HashMap<Integer, JobGroupRI> jobgroups;
    ArrayList<WorkerRI> workerRI = new ArrayList<>();

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

    public void addJobGroup(JobGroupImpl jobGroup){
        this.jobgroups.put(jobGroup.getJobId(),jobGroup);
    }

    public JobGroupRI getJobGroup(Integer jobId){
        if(jobgroups.containsKey(jobId)){
            return jobgroups.get(jobId);
        }
        return null;
    }

    public void removeJobGroup(int jobId){
        this.jobgroups.remove(jobId);
    }


    public void addWorkers(Integer jobId, WorkerRI workerRI) throws IOException {
        this.getJobgroups().get(jobId).addWorkers(workerRI);
        this.workerRI.add(workerRI);
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

    public HashMap<Integer, JobGroupRI> getJobgroups() {
        return jobgroups;
    }

    public void setJobgroups(HashMap<Integer, JobGroupRI> jobgroups) {
        this.jobgroups = jobgroups;
    }

    public ArrayList<WorkerRI> getWorkers() {
        return workerRI;
    }

    public void setWorkers(ArrayList<WorkerRI> workerRI) {
        this.workerRI = workerRI;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
