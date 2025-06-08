package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.PlaylistController;
import org.example.model.Playlist;

// TODO: Auto-generated Javadoc
/**
 * La Clase PlaylistView.
 */
public class PlaylistView extends VBox {
    
    /** The tabla. */
    private final TableView<Playlist> tabla;
    
    /** The playlist data. */
    private final ObservableList<Playlist> playlistData;
    
    /** The controller. */
    private final PlaylistController controller;
    
    /** The user id. */
    private final String userId;

    /**
     * Instancia una nueva playlist view.
     *
     * @param userId the user id
     */
    public PlaylistView(String userId) {
        this.userId = userId;
        this.controller = new PlaylistController();
        this.playlistData = FXCollections.observableArrayList();
        this.tabla = new TableView<>();

        tabla.setItems(playlistData);
        tabla.setPrefHeight(280);

        TableColumn<Playlist, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreCol.prefWidthProperty().bind(tabla.widthProperty().multiply(0.4));

        TableColumn<Playlist, String> descCol = new TableColumn<>("Descripción");
        descCol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descCol.prefWidthProperty().bind(tabla.widthProperty().multiply(0.4));

        TableColumn<Playlist, Boolean> privadaCol = new TableColumn<>("Privada");
        privadaCol.setCellValueFactory(new PropertyValueFactory<>("esPrivada"));
        privadaCol.setCellFactory(CheckBoxTableCell.forTableColumn(privadaCol));
        privadaCol.setEditable(false);
        privadaCol.prefWidthProperty().bind(tabla.widthProperty().multiply(0.2));

        tabla.getColumns().addAll(nombreCol, descCol, privadaCol);

        TextField modificarNombreField = new TextField();
        TextField modificarDescField = new TextField();
        modificarNombreField.setPromptText("Nuevo nombre");
        modificarDescField.setPromptText("Nueva descripción");

        CheckBox privacidadCheck = new CheckBox("Privada");

        Button actualizarBtn = new Button("Actualizar");
        Button eliminarBtn = new Button("Eliminar");
        Button verCancionesBtn = new Button("Ver Canciones");
        verCancionesBtn.setOnAction(e -> {
            Playlist sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                new CancionView(userId, sel.getId(), sel.getNombre()).mostrar();
            } else {
                mostrarAlerta("Selecciona una playlist primero.", Alert.AlertType.WARNING);
            }
        });

        actualizarBtn.setOnAction(e -> {
            Playlist sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setNombre(modificarNombreField.getText());
                sel.setDescripcion(modificarDescField.getText());
                sel.setEsPrivada(privacidadCheck.isSelected());
                controller.actualizarPlaylist(userId, sel);
                mostrarAlerta("Playlist actualizada.", Alert.AlertType.INFORMATION);
                modificarNombreField.clear();
                modificarDescField.clear();
                privacidadCheck.setSelected(false);
                cargarPlaylists();
            } else {
                mostrarAlerta("Selecciona una playlist primero.", Alert.AlertType.WARNING);
            }
        });

        eliminarBtn.setOnAction(e -> {
            Playlist sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.eliminarPlaylist(userId, sel.getId());
                mostrarAlerta("Playlist eliminada.", Alert.AlertType.INFORMATION);
                cargarPlaylists();
            } else {
                mostrarAlerta("Selecciona una playlist primero.", Alert.AlertType.WARNING);
            }
        });

        tabla.setOnMouseClicked(e -> {
            Playlist sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                modificarNombreField.setText(sel.getNombre());
                modificarDescField.setText(sel.getDescripcion());
                privacidadCheck.setSelected(sel.isEsPrivada());
            }
        });

        HBox form = new HBox(10, modificarNombreField, modificarDescField, privacidadCheck, actualizarBtn, eliminarBtn, verCancionesBtn);
        form.setPadding(new Insets(10));

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(tabla, form);

        cargarPlaylists();
    }

    /**
     * Cargar playlists.
     */
    private void cargarPlaylists() {
        controller.obtenerPlaylists(userId, listas -> playlistData.setAll(listas));
    }

    /**
     * Mostrar alerta.
     *
     * @param mensaje the mensaje
     * @param tipo the tipo
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Playlists");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Mostrar.
     *
     * @param parent the parent
     * @param nombreUsuario the nombre usuario
     */
    public void mostrar(Stage parent, String nombreUsuario) {
        Stage stage = new Stage();
        Scene scene = new Scene(this, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/socialmusic.png")));
        stage.setTitle("Playlists de " + nombreUsuario);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
