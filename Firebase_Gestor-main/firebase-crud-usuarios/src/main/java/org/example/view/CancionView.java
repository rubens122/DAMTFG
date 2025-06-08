package org.example.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.CancionController;
import org.example.model.Cancion;

// TODO: Auto-generated Javadoc
/**
 * La Clase CancionView.
 */
public class CancionView extends VBox {

    /** The user id. */
    private final String userId;
    
    /** The playlist id. */
    private final String playlistId;
    
    /** The playlist nombre. */
    private final String playlistNombre;
    
    /** The list view. */
    private final ListView<Cancion> listView;

    /**
     * Instancia nueva cancion view.
     *
     * @param userId the user id
     * @param playlistId the playlist id
     * @param playlistNombre the playlist nombre
     */
    public CancionView(String userId, String playlistId, String playlistNombre) {
        this.userId = userId;
        this.playlistId = playlistId;
        this.playlistNombre = playlistNombre;
        this.listView = new ListView<>();
        listView.getStyleClass().add("cancion-lista");

        setSpacing(10);
        setPadding(new Insets(10));
        getChildren().add(listView);
    }

    /**
     * Mostrar la ventana.
     */
    public void mostrar() {
        Stage stage = new Stage();

        Scene scene = new Scene(this, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Canciones de " + playlistNombre);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/socialmusic.png")));
        stage.centerOnScreen();
        stage.show();

        cargarCanciones();
    }

    /**
     * Cargar canciones.
     */
    private void cargarCanciones() {
        CancionController controller = new CancionController();
        controller.obtenerCanciones(userId, playlistId, canciones -> {
            Platform.runLater(() -> {
                listView.getItems().clear();
                if (canciones.isEmpty()) {
                    listView.getItems().add(new Cancion("Sin canciones", ""));
                } else {
                    listView.getItems().addAll(canciones);
                }
            });
        });
    }
}
