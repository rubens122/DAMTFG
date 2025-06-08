package org.example.controller;

import com.google.firebase.database.*;
import org.example.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// TODO: Auto-generated Javadoc
/**
 * La Clase UsuarioController.
 */
public class UsuarioController {

    /** El ref. */
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios");

    /**
     * Obtener usuarios.
     *
     * @param callback el callback
     */
    public void obtenerUsuarios(Consumer<List<Usuario>> callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Usuario> lista = new ArrayList<>();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Usuario u = hijo.getValue(Usuario.class);
                    if (u != null && u.getCorreo() != null) {
                        lista.add(u);
                    }
                }
                callback.accept(lista);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al obtener usuarios: " + error.getMessage());
            }
        });
    }

    /**
     * Agregar usuario.
     *
     * @param nombre el nombre
     * @param correo el correo
     * @param imagen la imagen
     */
    public void agregarUsuario(String nombre, String correo, String imagen) {
        String id = ref.push().getKey();
        if (id == null) return;
        Usuario nuevo = new Usuario(id, nombre, correo);
        if (imagen != null) {
            nuevo.setImagenPerfil(imagen);
        }
        ref.child(id).setValueAsync(nuevo);
    }

    /**
     * Actualizar usuario.
     *
     * @param usuario el usuario
     */
    public void actualizarUsuario(Usuario usuario) {
        ref.child(usuario.getId()).child("nombre").setValueAsync(usuario.getNombre());
    }

    /**
     * Eliminar usuario.
     *
     * @param id el id
     */
    public void eliminarUsuario(String id) {
        ref.child(id).removeValueAsync();
    }

    /**
     * Correo ya existe.
     *
     * @param correo el correo
     * @param callback el callback
     */
    public void correoYaExiste(String correo, Consumer<Boolean> callback) {
        ref.orderByChild("correo").equalTo(correo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.accept(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(false);
            }
        });
    }
}
