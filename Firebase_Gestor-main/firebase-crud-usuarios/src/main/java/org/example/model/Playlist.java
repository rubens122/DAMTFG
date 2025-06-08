package org.example.model;

import javafx.beans.property.*;

// TODO: Auto-generated Javadoc
/**
 * La Clase Playlist.
 */
public class Playlist {
    
    /** The id. */
    private final StringProperty id = new SimpleStringProperty();
    
    /** The nombre. */
    private final StringProperty nombre = new SimpleStringProperty();
    
    /** The descripcion. */
    private final StringProperty descripcion = new SimpleStringProperty();
    
    /** The es privada. */
    private final BooleanProperty esPrivada = new SimpleBooleanProperty(false);

    /**
     * Instantiates a new playlist.
     */
    public Playlist() {}

    /**
     * Instantiates a new playlist.
     *
     * @param id the id
     * @param nombre the nombre
     * @param descripcion the descripcion
     * @param esPrivada the es privada
     */
    public Playlist(String id, String nombre, String descripcion, boolean esPrivada) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.descripcion.set(descripcion);
        this.esPrivada.set(esPrivada);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() { return id.get(); }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) { this.id.set(id); }
    
    /**
     * Id property.
     *
     * @return the string property
     */
    public StringProperty idProperty() { return id; }

    /**
     * Gets the nombre.
     *
     * @return the nombre
     */
    public String getNombre() { return nombre.get(); }
    
    /**
     * Sets the nombre.
     *
     * @param nombre the new nombre
     */
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    
    /**
     * Nombre property.
     *
     * @return the string property
     */
    public StringProperty nombreProperty() { return nombre; }

    /**
     * Gets the descripcion.
     *
     * @return the descripcion
     */
    public String getDescripcion() { return descripcion.get(); }
    
    /**
     * Sets the descripcion.
     *
     * @param descripcion the new descripcion
     */
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    
    /**
     * Descripcion property.
     *
     * @return the string property
     */
    public StringProperty descripcionProperty() { return descripcion; }

    /**
     * Checks if is es privada.
     *
     * @return true, if is es privada
     */
    public boolean isEsPrivada() { return esPrivada.get(); }
    
    /**
     * Sets the es privada.
     *
     * @param esPrivada the new es privada
     */
    public void setEsPrivada(boolean esPrivada) { this.esPrivada.set(esPrivada); }
    
    /**
     * Es privada property.
     *
     * @return the boolean property
     */
    public BooleanProperty esPrivadaProperty() { return esPrivada; }
}