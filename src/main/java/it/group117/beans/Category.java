package it.group117.beans;

import java.io.Serial;
import java.io.Serializable;

/**
 * Category bean. Used to store the category code and name
 */
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private boolean parentable;

    /**
     * Category code getter method
     * 
     * @return category code
     */
    public String getCode() {
        return code;
    }

    /**
     * Category code setter method
     * 
     * @param code code to be set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Category name getter method
     * 
     * @return category name
     */
    public String getName() {
        return name;
    }

    /**
     * Category name setter method
     * 
     * @param name name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Category name getter method
     *
     * @return if category is parent-able
     */
    public boolean getParentable() {
        return parentable;
    }

    /**
     * Category name setter method
     *
     * @param parentable if the category is parent-able or not
     */
    public void setParentable(boolean parentable) {
        this.parentable = parentable;
    }
}
