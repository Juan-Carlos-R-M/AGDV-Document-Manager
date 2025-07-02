package agdv;

// Importaciones necesarias para JavaFX, utilidades y componentes del sistema
import agdv.controller.BackupsController;
import agdv.util.batch.BatchExecutor;
import agdv.util.selfBackup.BackupScheduler;
import agdv.util.properties.ConfigManager;
import agdv.util.dialog.AlertScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Clase principal de la aplicación.
 * Punto de entrada del programa (entry point).
 *
 * Esta clase:
 * - Inicia la interfaz gráfica.
 * - Configura la conexión a la API (local/remota).
 * - Ejecuta automáticamente un servidor local si es necesario.
 * - Programa respaldos automáticos periódicos.
 */
public class Main extends Application {

    // === Atributos internos ===
    private ConfigManager configManager = ConfigManager.getInstance(); // Gestor de configuración persistente
    private static Process apiProcess;       // Proceso del servidor API local (si se ejecuta)
    private static BackupScheduler backupScheduler; // Programador de respaldos automáticos

    /**
     * Método inicial llamado por JavaFX al iniciar la aplicación.
     * Configura la pantalla principal y, si es necesario, inicia el servidor local.
     *
     * @param primaryStage Ventana principal de la aplicación
     */
    @Override
    public void start(Stage primaryStage) {
        // Asignar la ventana principal a la clase auxiliar MainApp
        MainApp.setPrimaryStage(primaryStage);

        // Verificar si ya hay una URL guardada para la API
        if (configManager.getString("api.url") == null || configManager.getString("api.url").equals("null")) {
            // Si no hay configuración previa, mostrar pantalla de configuración
            MainApp.showModalWindow("/agdv/fxml/ConfigureApplication.fxml", "Conexión de API");
        } else {
            // Si está usando modo local, ejecutar servidor local automáticamente
            if (configManager.getBoolean("api.run")) {
                try {
                    String batFile = "runAPIAGDV.bat"; // Nombre del script BAT
                    String command = "cmd /c dir " + batFile + " /s /b";
                    Process process = Runtime.getRuntime().exec(command, null, new File("C://"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String route = reader.readLine();

                    // Validar que se encontró el archivo .bat
                    if (route == null || route.trim().isEmpty()) {
                        AlertScreen.showError("Error", "No se encontró el archivo BAT necesario para iniciar la API.");
                        return;
                    }

                    // Iniciar el servidor API local en un hilo separado
                    Thread serverThread = new Thread(() -> {
                        apiProcess = BatchExecutor.ejecutarBat(route);
                        if (apiProcess != null) {
                            System.out.println("✅ Servidor iniciado correctamente.");
                        } else {
                            System.err.println("❌ No se pudo iniciar el servidor.");
                        }
                    });
                    serverThread.setDaemon(true); // Hilo demonio: termina con la app
                    serverThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("❌ Error al ejecutar el archivo BAT desde la ruta fija.");
                }
            }

            // Cargar la ventana principal de la aplicación
            MainApp.startApplication("/agdv/util/toolbar/ToolBar.fxml");
        }

        // Iniciar programador de respaldos automáticos después de un retraso de 10 segundos
        new Thread(() -> {
            try {
                System.out.println("⏳ Esperando 10 segundos para asegurar que el servidor haya iniciado...");
                Thread.sleep(10_000);
                backupScheduler = new BackupScheduler("next_backup.txt", new BackupsController());
                backupScheduler.start(); // Iniciar programador de respaldos automáticos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * Lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados en este caso)
     */
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando aplicación...");
        launch(args); // Iniciar la aplicación JavaFX
    }
}