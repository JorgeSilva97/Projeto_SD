package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.DBMockup;
import edu.ufp.inf.sd.project.server.SessionRI;

import java.rmi.RemoteException;

public class SessionInfo {
    private String username;
    private SessionRI sessionRI;


    public SessionInfo(SessionRI sessionRI, String username) throws RemoteException {
        super();
        this.sessionRI = sessionRI;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SessionRI getSessionRI() {
        return sessionRI;
    }

}
