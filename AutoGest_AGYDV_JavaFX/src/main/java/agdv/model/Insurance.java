package agdv.model;


/**
 *
 * @author Juan Carlos Rizo Mendoza
 */

public class Insurance {
    private int id;
    private String name;

    public Insurance() {}

    public Insurance(int id, String nombre) {
        this.id = id;
        this.name = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
