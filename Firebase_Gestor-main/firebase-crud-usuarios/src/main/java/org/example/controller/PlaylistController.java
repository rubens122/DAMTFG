package org.example.controller;

import com.google.firebase.database.*;
import org.example.model.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// TODO: Auto-generated Javadoc
/**
 * La Clase PlaylistController.
 */
public class PlaylistController {

    /** El db ref. */
    private final DatabaseReference dbRef;

    /**
     * Instancia una nueva playlist controller.
     */
    public PlaylistController() {
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    /**
     * Obtener playlists.
     *
     * @param userId el user id
     * @param callback el callback
     */
    public void obtenerPlaylists(String userId, Consumer<List<Playlist>> callback) {
        dbRef.child(userId).child("playlists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Playlist> playlists = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Playlist p = data.getValue(Playlist.class);
                    if (p != null) {
                        p.setId(data.getKey());
                        playlists.add(p);
                    }
                }
                callback.accept(playlists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    /**
     * Eliminar playlist.
     *
     * @param userId el user id
     * @param playlistId el playlist id
     */
    public void eliminarPlaylist(String userId, String playlistId) {
        dbRef.child(userId).child("playlists").child(playlistId).removeValueAsync();
    }

    /**
     * Actualizar playlist.
     *
     * @param userId el user id
     * @param playlist el playlist
     */
    public void actualizarPlaylist(String userId, Playlist playlist) {
        DatabaseReference ref = dbRef.child(userId).child("playlists").child(playlist.getId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", playlist.getNombre());
        updates.put("descripcion", playlist.getDescripcion());
        updates.put("esPrivada", playlist.isEsPrivada());

        ref.updateChildrenAsync(updates);
    }

}