package org.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * La Clase FirebaseConfig.
 */
public class FirebaseConfig {
    
    /**
     * Inicializa la base .
     */
    public static void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/proyectofinaldam.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://proyectofinaldam-6d046-default-rtdb.firebaseio.com/")
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado");
        } catch (IOException e) {
            System.err.println("Error al inicializar Firebase: " + e.getMessage());
        }
    }
}
