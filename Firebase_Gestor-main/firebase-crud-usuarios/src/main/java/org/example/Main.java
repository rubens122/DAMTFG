package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.view.LoginView;
import org.example.view.UsuarioView;

// TODO: Auto-generated Javadoc
/**
 * La Clase Main.
 */
public class Main extends Application {

    /**
     * El inicio.
     *
     * @param stage the stage
     */
    @Override
    public void start(Stage stage) {
        FirebaseConfig.initialize();

        LoginView login = new LoginView();
        login.mostrar(stage);
    }

    /**
     * El metodo principal.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Mostrar la ventana principal.
     *
     * @param stage the stage
     */
    public static void mostrarVentanaPrincipal(Stage stage) {
        UsuarioView root = new UsuarioView();
        Scene scene = new Scene(root, 1000, 600);

        scene.getStylesheets().add(Main.class.getResource("/style.css").toExternalForm());
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/socialmusic.png")));

        stage.setTitle("Social Music - Gestión de Usuarios");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.show();
    }
}
