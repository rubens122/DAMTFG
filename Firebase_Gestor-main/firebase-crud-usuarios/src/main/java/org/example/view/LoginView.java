package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * La Clase LoginView.
 */
public class LoginView {

    /**
     * Mostrar.
     *
     * @param primaryStage the primary stage
     */
    public void mostrar(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/socialmusic.png")));
        logo.setFitHeight(100);
        logo.setPreserveRatio(true);

        TextField correoField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Iniciar Sesión");

        correoField.setPromptText("Correo");
        passwordField.setPromptText("Contraseña");

        loginButton.setOnAction(e -> {
            String correo = correoField.getText();
            String pass = passwordField.getText();
            if (correo.isEmpty() || pass.isEmpty()) {
                mostrarAlerta("Todos los campos son obligatorios.", Alert.AlertType.WARNING);
                return;
            }
            if (validarCredenciales(correo, pass)) {
                Main.mostrarVentanaPrincipal(primaryStage);
            } else {
                mostrarAlerta("Correo o contraseña incorrectos.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                logo,
                new Label("Correo:"), correoField,
                new Label("Contraseña:"), passwordField,
                loginButton
        );

        Scene scene = new Scene(layout, 320, 340);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/socialmusic.png")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Social Music - Login");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * Mostrar alerta.
     *
     * @param mensaje the mensaje
     * @param tipo the tipo
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Inicio de sesión");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Validar credenciales.
     *
     * @param correo the correo
     * @param contrasena the contrasena
     * @return true, if successful
     */
    private boolean validarCredenciales(String correo, String contrasena) {
        try (InputStream input = getClass().getResourceAsStream("/login.properties")) {
            if (input == null) return false;

            Properties props = new Properties();
            props.load(input);

            String storedPass = props.getProperty(correo);
            return storedPass != null && storedPass.equals(contrasena);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
