package org.example.model;

// TODO: Auto-generated Javadoc
/**
 * La Clase Amigo.
 */
public class Amigo {
    
    /** The id. */
    private String id;
    
    /** The correo. */
    private String correo;

    /**
     * Instantiates a new amigo.
     */
    public Amigo() {}

    /**
     * Instantiates a new amigo.
     *
     * @param id the id
     * @param correo the correo
     */
    public Amigo(String id, String correo) {
        this.id = id;
        this.correo = correo;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() { return id; }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the correo.
     *
     * @return the correo
     */
    public String getCorreo() { return correo; }
    
    /**
     * Sets the correo.
     *
     * @param correo the new correo
     */
    public void setCorreo(String correo) { this.correo = correo; }
}
