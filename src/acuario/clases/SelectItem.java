/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

/**
 *
 * @author Marcelo
 */
public class SelectItem {

    private Object id;
    private String description;

    public SelectItem(Object id, String description) {
        this.id = id;
        this.description = description;
    }

    public Object getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
