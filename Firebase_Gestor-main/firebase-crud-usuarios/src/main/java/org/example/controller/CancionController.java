package org.example.controller;

import com.google.firebase.database.*;
import org.example.model.Cancion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// TODO: Auto-generated Javadoc
/**
 * La clase CancionController.
 */
public class CancionController {

    /**
     * Obtener canciones.
     *
     * @param userId el user id
     * @param playlistId la playlist id
     * @param callback el callback
     */
    public void obtenerCanciones(String userId, String playlistId, Consumer<List<Cancion>> callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("usuarios").child(userId).child("playlists").child(playlistId).child("canciones");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Cancion> lista = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot cancionSnap : snapshot.getChildren()) {
                        Map<String, Object> datos = (Map<String, Object>) cancionSnap.getValue();
                        String titulo = (String) datos.getOrDefault("title", "Sin título");
                        String artista = ((Map<String, Object>) datos.get("artist")).get("name").toString();
                        lista.add(new Cancion(titulo, artista));
                    }
                }
                callback.accept(lista);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }
}
