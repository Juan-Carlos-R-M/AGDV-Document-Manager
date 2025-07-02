package agdv.controller;

// Importaciones necesarias para JavaFX, utilidades y servicios del sistema
import agdv.Main;
import agdv.util.batch.BatchExecutor;
import agdv.util.dialog.AlertScreen;
import agdv.util.properties.ConfigManager;
import agdv.util.services.ApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Controlador para la pantalla de configuración inicial de la aplicación.
 * Permite al usuario:
 * - Seleccionar entre usar una API local o remota (en línea).
 * - Ingresar manualmente la dirección y puerto del servidor.
 * - Ejecutar un script `.bat` para instalar dependencias necesarias.
 * - Guardar la configuración usando `ConfigManager`.
 * - Reiniciar el cliente HTTP (`ApiClient`) con la nueva URL.
 */
public class ConfigureApplication {

    // === Componentes FXML vinculados desde el archivo FXML ===
    @FXML private ToggleButton toggleBtn;     // Botón para alternar entre modos
    @FXML private TextField adressField;      // Campo para ingresar la dirección del servidor
    @FXML private TextField portField;        // Campo para ingresar el puerto del servidor
    @FXML private Label portLabel;            // Etiqueta visual del puerto
    @FXML private Button installBtn;          // Botón para ejecutar instalación de herramientas
    @FXML private Button continueBtn;         // Botón para continuar a la siguiente pantalla
    @FXML private ToggleButton onlineBtn;     // Botón adicional para activar/desactivar modo en línea
    @FXML private HBox hbox;                  // Contenedor visual horizontal

    // === Atributos internos ===
    private boolean activeButton = false;               // Indica si se está en modo local o en línea
    private final ConfigManager configManager = ConfigManager.getInstance(); // Gestor de propiedades persistentes

    /**
     * Método llamado automáticamente por JavaFX al cargar la vista.
     * Establece los valores iniciales, como usar el servidor local por defecto.
     */
    public void initialize() {
        configManager.saveBoolean("api.run", true); // Por defecto: usar servidor local
    }

    /**
     * Navega a la pantalla principal de la aplicación.
     * Cierra esta ventana actual y abre la interfaz principal.
     */
    private void switchScreen() {
        Platform.runLater(() -> {
            try {
                Stage newStage = new Stage();
                new Main().start(newStage); // Cargar la pantalla principal
                Stage currentStage = (Stage) continueBtn.getScene().getWindow();
                currentStage.close(); // Cerrar esta ventana
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Ejecuta un archivo `.bat` que instala programas y dependencias necesarias.
     * Busca el archivo primero y luego lo ejecuta mediante `BatchExecutor`.
     */
    @FXML
    public void installPrograms() {
        try {
            String file = "installProgramsAPI.bat"; // Nombre del archivo BAT
            String command = "cmd /c dir " + file + " /s /b";
            Process process = Runtime.getRuntime().exec(command, null, new File("C://")); // Buscar archivo
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String route = bufferedReader.readLine(); // Ruta donde se encuentra el archivo

            if (route == null || route.trim().isEmpty()) {
                AlertScreen.showError("Error", "Archivo BAT no encontrado: " + file +
                        ". Es necesario para la instalación. Desinstale y vuelva a instalar el programa.");
                return;
            }

            // Ejecutar el archivo BAT
            Process installProcess = BatchExecutor.ejecutarBat(route);
            if (installProcess == null) {
                AlertScreen.showError("Error fatal", "No se pudo completar la instalación.");
            } else {
                AlertScreen.showSuccess("Éxito", "Todos los programas se instalaron correctamente.");
                configManager.saveBoolean("api.run", true); // Marcar como instalado
            }

        } catch (Exception e) {
            AlertScreen.showError("Error", "Error al ejecutar la instalación.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura la conexión a la base de datos/API según los datos ingresados.
     * Valida los campos, construye la URL y cambia a la pantalla principal.
     */
    @FXML
    public void connectDatabase() {
        String direccion = adressField.getText().trim();
        String puerto = portField.getText().trim();

        if (direccion.isEmpty()) {
            AlertScreen.showError("Error de conexión", "La dirección no puede estar vacía.");
            return;
        }

        // Construir URL con o sin puerto
        String url = puerto.isEmpty() ? direccion : direccion + ":" + puerto;

        // Guardar configuración persistente
        configManager.saveString("api.url", url);

        // Reiniciar cliente Retrofit con nueva URL
        ApiClient.getClient();

        // Cambiar a la pantalla principal
        switchScreen();
    }

    /**
     * Alterna entre usar una API local (`localhost`) o remota (`railway.app`).
     * Actualiza los campos de texto, visibilidad de componentes y guarda la configuración.
     */
    @FXML
    public void onlineAPI() {
        if (!activeButton) {
            // Modo en línea: ocultar puerto y establecer URL remota
            portField.setVisible(false);
            portLabel.setVisible(false);
            adressField.setText("https://fastapi-api.up.railway.app ");
            portField.setText("");
            activeButton = true;
            configManager.saveBoolean("api.run", false); // Usar API externa
        } else {
            // Modo local: mostrar puerto y usar localhost
            portField.setVisible(true);
            portLabel.setVisible(true);
            portField.setText("8081");
            adressField.setText("https://localhost");
            activeButton = false;
            configManager.saveBoolean("api.run", true); // Usar API local
        }
    }
}