package edu.ufp.inf.sd.project.server.login;

import java.util.ArrayList;

/**
 * This class simulates a DBMockup for managing users and books.
 *
 * @author rmoreira
 *
 */
public class DBMockup {

    //private final ArrayList<Book> books;// = new ArrayList();
    private final ArrayList<User> users;// = new ArrayList();

    /**
     * This constructor creates and inits the database with some books and users.
     */
    public DBMockup() {
       // books = new ArrayList();
        users = new ArrayList();
        //Add 3 books
       // books.add(new Book("Distributed Systems: principles and paradigms", "Tanenbaum"));
       // books.add(new Book("Distributed Systems: concepts and design", "Colouris"));
       // books.add(new Book("Distributed Computing Networks", "Tanenbaum"));
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


}
