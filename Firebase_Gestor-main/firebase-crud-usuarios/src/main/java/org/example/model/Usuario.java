package org.example.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// TODO: Auto-generated Javadoc
/**
 * La Clase Usuario.
 */
public class Usuario {
    
    /** The id. */
    private final StringProperty id = new SimpleStringProperty();
    
    /** The nombre. */
    private final StringProperty nombre = new SimpleStringProperty();
    
    /** The correo. */
    private final StringProperty correo = new SimpleStringProperty();
    
    /** The imagen perfil. */
    private final StringProperty imagenPerfil = new SimpleStringProperty();
    
    /** The imagen perfil base 64. */
    private final StringProperty imagenPerfilBase64 = new SimpleStringProperty();

    /**
     * Instantiates a new usuario.
     */
    public Usuario() {}

    /**
     * Instantiates a new usuario.
     *
     * @param id the id
     * @param nombre the nombre
     * @param correo the correo
     */
    public Usuario(String id, String nombre, String correo) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.correo.set(correo);
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
     * Gets the correo.
     *
     * @return the correo
     */
    public String getCorreo() { return correo.get(); }
    
    /**
     * Sets the correo.
     *
     * @param correo the new correo
     */
    public void setCorreo(String correo) { this.correo.set(correo); }
    
    /**
     * Correo property.
     *
     * @return the string property
     */
    public StringProperty correoProperty() { return correo; }

    /**
     * Gets the imagen perfil.
     *
     * @return the imagen perfil
     */
    public String getImagenPerfil() { return imagenPerfil.get(); }
    
    /**
     * Sets the imagen perfil.
     *
     * @param imagenPerfil the new imagen perfil
     */
    public void setImagenPerfil(String imagenPerfil) { this.imagenPerfil.set(imagenPerfil); }
    
    /**
     * Imagen perfil property.
     *
     * @return the string property
     */
    public StringProperty imagenPerfilProperty() { return imagenPerfil; }

    /**
     * Gets the imagen perfil base 64.
     *
     * @return the imagen perfil base 64
     */
    public String getImagenPerfilBase64() { return imagenPerfilBase64.get(); }
    
    /**
     * Sets the imagen perfil base 64.
     *
     * @param imagenPerfilBase64 the new imagen perfil base 64
     */
    public void setImagenPerfilBase64(String imagenPerfilBase64) { this.imagenPerfilBase64.set(imagenPerfilBase64); }
    
    /**
     * Imagen perfil base 64 property.
     *
     * @return the string property
     */
    public StringProperty imagenPerfilBase64Property() { return imagenPerfilBase64; }
}
