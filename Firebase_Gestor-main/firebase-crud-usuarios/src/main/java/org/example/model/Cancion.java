package org.example.model;

// TODO: Auto-generated Javadoc
/**
 * La Clase Cancion.
 */
public class Cancion {
    
    /** The titulo. */
    private String titulo;
    
    /** The artista. */
    private String artista;

    /**
     * Instantiates a new cancion.
     *
     * @param titulo the titulo
     * @param artista the artista
     */
    public Cancion(String titulo, String artista) {
        this.titulo = titulo;
        this.artista = artista;
    }

    /**
     * Gets the titulo.
     *
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Gets the artista.
     *
     * @return the artista
     */
    public String getArtista() {
        return artista;
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return titulo + " - " + artista;
    }
}
