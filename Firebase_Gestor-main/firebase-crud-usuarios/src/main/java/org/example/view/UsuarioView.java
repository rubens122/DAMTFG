package org.example.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.UsuarioController;
import org.example.model.Usuario;

// TODO: Auto-generated Javadoc
/**
 * La Clase UsuarioView.
 */
public class UsuarioView extends VBox {
    
    /** The tabla usuarios. */
    private final TableView<Usuario> tablaUsuarios;
    
    /** The usuarios data. */
    private final ObservableList<Usuario> usuariosData;
    
    /** The controller. */
    private final UsuarioController controller;

    /** The nombre field. */
    private final TextField nombreField = new TextField();
    
    /** The correo field. */
    private final TextField correoField = new TextField();

    /**
     * Instancia una nueva usuario view.
     */
    public UsuarioView() {
        tablaUsuarios = new TableView<>();
        usuariosData = FXCollections.observableArrayList();
        controller = new UsuarioController();

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.prefWidthProperty().bind(tablaUsuarios.widthProperty().multiply(0.5));

        TableColumn<Usuario, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCorreo.prefWidthProperty().bind(tablaUsuarios.widthProperty().multiply(0.5));

        tablaUsuarios.getColumns().addAll(colNombre, colCorreo);
        tablaUsuarios.setItems(usuariosData);

        tablaUsuarios.setPrefHeight(280);
        VBox.setVgrow(tablaUsuarios, Priority.ALWAYS);

        nombreField.setPromptText("Nombre");
        correoField.setPromptText("Correo");

        Button agregarBtn = new Button("Agregar");
        Button actualizarBtn = new Button("Actualizar");
        Button eliminarBtn = new Button("Eliminar");
        Button verPlaylistsBtn = new Button("Ver Playlists");
        Button verAmigosBtn = new Button("Ver Amigos");

        agregarBtn.setOnAction(e -> mostrarVentanaAgregar());

        actualizarBtn.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                seleccionado.setNombre(nombreField.getText());
                controller.actualizarUsuario(seleccionado);
                mostrarAlerta("Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
                cargarUsuarios();
                correoField.setDisable(false);
            }
        });

        eliminarBtn.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                controller.eliminarUsuario(seleccionado.getId());
                mostrarAlerta("Usuario eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarUsuarios();
                nombreField.clear();
                correoField.clear();
                correoField.setDisable(false);
            }
        });

        verPlaylistsBtn.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                PlaylistView vista = new PlaylistView(seleccionado.getId());
                vista.mostrar(((Stage) this.getScene().getWindow()), seleccionado.getNombre());
            } else {
                mostrarAlerta("Selecciona un usuario primero", Alert.AlertType.WARNING);
            }
        });

        verAmigosBtn.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                AmigoView vista = new AmigoView(seleccionado.getId());
                vista.mostrar(((Stage) this.getScene().getWindow()), seleccionado.getNombre());
            } else {
                mostrarAlerta("Selecciona un usuario primero", Alert.AlertType.WARNING);
            }
        });

        tablaUsuarios.setOnMouseClicked(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                nombreField.setText(seleccionado.getNombre());
                correoField.setText(seleccionado.getCorreo());
                correoField.setDisable(true);
            }
        });

        HBox controles = new HBox(10, nombreField, correoField, actualizarBtn, agregarBtn, eliminarBtn, verPlaylistsBtn, verAmigosBtn);
        controles.setPadding(new Insets(10));

        this.getChildren().addAll(tablaUsuarios, controles);
        this.setPadding(new Insets(10));
        cargarUsuarios();
    }

    /**
     * Cargar usuarios.
     */
    private void cargarUsuarios() {
        controller.obtenerUsuarios(lista -> Platform.runLater(() -> usuariosData.setAll(lista)));
    }

    /**
     * Mostrar alerta.
     *
     * @param mensaje the mensaje
     * @param tipo the tipo
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Mostrar la ventana agregar.
     */
    private void mostrarVentanaAgregar() {
        Stage ventana = new Stage();
        ventana.setTitle("Agregar nuevo usuario");

        TextField nombreNuevo = new TextField();
        TextField correoNuevo = new TextField();
        nombreNuevo.setPromptText("Nombre");
        correoNuevo.setPromptText("Correo");

        Button guardarBtn = new Button("Guardar");
        guardarBtn.setOnAction(event -> {
            String nombre = nombreNuevo.getText();
            String correo = correoNuevo.getText();

            if (nombre.isEmpty() || correo.isEmpty()) {
                mostrarAlerta("Los campos no pueden estar vacíos.", Alert.AlertType.WARNING);
                return;
            }

            if (!correo.matches("^.+@.+\\..+$")) {
                mostrarAlerta("Correo no válido.", Alert.AlertType.WARNING);
                return;
            }

            controller.correoYaExiste(correo, existe -> {
                if (existe) {
                    Platform.runLater(() -> mostrarAlerta("Ya existe un usuario con ese correo.", Alert.AlertType.WARNING));
                } else {
                    controller.agregarUsuario(nombre, correo, null);
                    Platform.runLater(() -> {
                        mostrarAlerta("Usuario agregado correctamente.", Alert.AlertType.INFORMATION);
                        cargarUsuarios();
                        ventana.close();
                    });
                }
            });
        });

        VBox layout = new VBox(10, nombreNuevo, correoNuevo, guardarBtn);
        layout.setPadding(new Insets(20));
        Scene escena = new Scene(layout, 300, 150);
        escena.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        ventana.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/socialmusic.png")));
        ventana.setScene(escena);
        ventana.show();
    }
}
