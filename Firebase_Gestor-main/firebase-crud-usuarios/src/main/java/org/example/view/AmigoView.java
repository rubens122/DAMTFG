package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.example.controller.AmigoController;
import org.example.model.Amigo;

// TODO: Auto-generated Javadoc
/**
 * La Clase AmigoView.
 */
public class AmigoView extends VBox {

    /** The tabla. */
    private final TableView<Amigo> tabla;
    
    /** The datos. */
    private final ObservableList<Amigo> datos;
    
    /** The controller. */
    private final AmigoController controller;
    
    /** The user id. */
    private final String userId;

    /**
     * IInstancia una nueva amigo view.
     *
     * @param userId the user id
     */
    public AmigoView(String userId) {
        this.userId = userId;
        this.controller = new AmigoController();
        this.datos = FXCollections.observableArrayList();
        this.tabla = new TableView<>();

        tabla.setItems(datos);
        tabla.setPrefHeight(250);

        TableColumn<Amigo, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCorreo.prefWidthProperty().bind(tabla.widthProperty().multiply(1));

        tabla.getColumns().add(colCorreo);

        Button eliminarBtn = new Button("Eliminar amigo");
        eliminarBtn.setOnAction(e -> {
            Amigo sel = tabla.getSelectionModel().getSelectedItem();
            if (sel != null) {
                controller.eliminarAmigo(userId, sel.getId());
                mostrarAlerta("Amigo eliminado.", Alert.AlertType.INFORMATION);
                cargarAmigos();
            } else {
                mostrarAlerta("Selecciona un amigo primero.", Alert.AlertType.WARNING);
            }
        });

        HBox barra = new HBox(10, eliminarBtn);
        barra.setPadding(new Insets(10));

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.getChildren().addAll(tabla, barra);

        cargarAmigos();
    }

    /**
     * Cargar amigos.
     */
    private void cargarAmigos() {
        controller.obtenerAmigos(userId, amigos -> {
            datos.setAll(amigos.stream().filter(a -> a.getCorreo() != null && !a.getCorreo().isEmpty()).toList());
        });
    }

    /**
     * Mostrar alerta.
     *
     * @param mensaje the mensaje
     * @param tipo the tipo
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Amigos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Mostrar la ventana.
     *
     * @param parent the parent
     * @param nombreUsuario the nombre usuario
     */
    public void mostrar(Stage parent, String nombreUsuario) {
        Stage stage = new Stage();
        Scene scene = new Scene(this, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/socialmusic.png")));
        stage.setTitle("Amigos de " + nombreUsuario);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
