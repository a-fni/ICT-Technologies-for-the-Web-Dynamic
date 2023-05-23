package it.group117.beans;

import java.io.Serial;
import java.io.Serializable;

/**
 * User bean. Only really needs the username, the password must not be extracted from the
 * database
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;

    /**
     * Username getter method
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Username setter method
     * 
     * @param username username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
