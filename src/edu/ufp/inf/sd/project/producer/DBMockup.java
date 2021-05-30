package edu.ufp.inf.sd.project.producer;

import edu.ufp.inf.sd.project.consumer.Worker;
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
    private HashMap<Integer, JobGroup> jobgroups;
    ArrayList<Worker> worker;

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
        this.users = new ArrayList();
        this.jobgroups = new HashMap<>();
        this.sessions = new HashMap<>();
        this.worker = new ArrayList<>();
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

    /**
     * Adicionar JobGroup ao array de Jobgroups
     * @param jobGroup que vai ser guardado
     */
    public void addJobGroup(JobGroup jobGroup){
        this.jobgroups.put(jobGroup.getJobId(),jobGroup);
    }

    /**
     * Envia uma string com os jobs pertencentes ao consumer
     * @param uname nome do consumer
     * @return
     */
    public String getmyJobs(String uname){
        String jobs = "";
        for (int i = 0; i < this.jobgroups.size(); i++) {
            JobGroup jobGroup = this.jobgroups.get(i + 1);
            if(jobGroup.getUser().getUname().equals(uname))
            jobs += jobGroup.toString() + "\n";
        }
        return jobs;
    }

    /**
     * Remover um jobGroup
     * @param jobId ID do job a remover
     */
    public void removeJobGroup(int jobId){
        this.jobgroups.remove(jobId);
    }

    /**
     * Retorna um JobGroup com o id especificado
     * @param jobID
     * @return
     */
    public JobGroup getJobGroup(int jobID){
        for (int i = 0; i < this.jobgroups.size(); i++) {
            JobGroup jobGroup = this.jobgroups.get(i + 1);
            if(jobGroup.getJobId() == jobID)
                return jobGroup;
        }
        return null;
    }

    /**
     * Insere worker dentro de um jobGroup
     * @param jobId ID onde vai ser inserido
     * @param worker worker que vai ser inserido
     * @throws IOException
     */
    public void addWorkers(Integer jobId, Worker worker) throws IOException {
        this.getJobgroups().get(jobId).addWorkers(worker);
        this.worker.add(worker);
    }

    public User getUser(String uname){
        for(User u : this.users){
            if(u.getUname().equals(uname))
                return u;
        }
        return null;
    }

    /**
     *
     * @return uma string com todos os jobgroups à espera de iniciar
     */
    public String getJobgroupsString() {
        String jobs = "";
        for (int i = 0; i < this.jobgroups.size(); i++) {
            JobGroup jobGroup = this.jobgroups.get(i + 1);
            if(jobGroup.getState() == 2)
               jobs += jobGroup.toString() + "\n";
        }
        return jobs;
    }

    public HashMap<String, SessionRI> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, SessionRI> sessions) {
        this.sessions = sessions;
    }

    public HashMap<Integer, JobGroup> getJobgroups() {
        return jobgroups;
    }

    public void setJobgroups(HashMap<Integer, JobGroup> jobgroups) {
        this.jobgroups = jobgroups;
    }

    public ArrayList<Worker> getWorkers() {
        return worker;
    }

    public void setWorkers(ArrayList<Worker> workerRI) {
        this.worker = workerRI;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
