package org.example.controller;

import com.google.firebase.database.*;
import org.example.model.Amigo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// TODO: Auto-generated Javadoc
/**
 * La Clase AmigoController.
 */
public class AmigoController {

    /** El ref. */
    private final DatabaseReference ref;

    /**
     * Instancia un nuevo amigo controller.
     */
    public AmigoController() {
        this.ref = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    /**
     * Obtener amigos.
     *
     * @param userId el user id
     * @param callback el callback
     */
    public void obtenerAmigos(String userId, Consumer<List<Amigo>> callback) {
        ref.child(userId).child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot amigosSnapshot) {
                List<Amigo> listaAmigos = new ArrayList<>();

                for (DataSnapshot amigoSnap : amigosSnapshot.getChildren()) {
                    String amigoId = amigoSnap.getKey();

                    ref.child(amigoId).child("correo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot correoSnapshot) {
                            String correo = correoSnapshot.getValue(String.class);
                            if (correo != null && !correo.isEmpty()) {
                                listaAmigos.add(new Amigo(amigoId, correo));
                            }
                            callback.accept(listaAmigos);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            callback.accept(new ArrayList<>());
                        }
                    });
                }

                if (!amigosSnapshot.hasChildren()) {
                    callback.accept(listaAmigos);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    /**
     * Eliminar amigo.
     *
     * @param userId el user id
     * @param amigoId el amigo id
     */
    public void eliminarAmigo(String userId, String amigoId) {
        ref.child(userId).child("amigos").child(amigoId).removeValueAsync();
        ref.child(amigoId).child("amigos").child(userId).removeValueAsync();
    }
}
