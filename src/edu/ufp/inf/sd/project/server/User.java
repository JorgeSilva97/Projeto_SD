package edu.ufp.inf.sd.project.server;


import java.io.Serializable;

public class User implements Serializable {

    private String uname;
    private String pword;
    private int creditos;

    public User(String uname, String pword)
    {
        this.uname = uname;
        this.pword = pword;
        this.creditos = 100;
    }

    @Override
    public String toString() {
        return "User{" + "uname=" + uname + ", pword=" + pword + '}';
    }

    /**
     * @return the uname
     */
    public String getUname() {
        return uname;
    }

    /**
     * @param uname the uname to set
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * @return the pword
     */
    public String getPword() {
        return pword;
    }

    /**
     * @param pword the pword to set
     */
    public void setPword(String pword) {
        this.pword = pword;
    }

    public void setCredits(int credits){this.creditos = credits;}
    public void addCredits(int credits){this.creditos = this.creditos + credits;}
    public int getCredits(){return creditos;}
}

