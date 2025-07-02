package agdv.util.toolbar;

import agdv.MainApp;
import agdv.util.batch.BatchExecutor;
import agdv.util.Screen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Componente que representa una barra de herramientas personalizada.
 * Proporciona funcionalidades comunes como:
 * - Navegación entre pantallas.
 * - Acciones de ventana (cerrar, minimizar, pantalla completa).
 * - Botones de acceso rápido a secciones importantes de la aplicación.
 */
public class ToolBar {

    // Botones de control de ventana
    @FXML private Button closeButton;     // Cierra la aplicación
    @FXML private Button minimizeButton;  // Minimiza la ventana
    @FXML private Button fullscreenButton; // Alterna entre modo normal y pantalla completa

    // Botones de navegación funcional
    @FXML private Button configButton;    // Configuración de la aplicación
    @FXML private Button consultButton;   // Consultar información
    @FXML private Button addCarButton;    // Agregar nuevo vehículo
    @FXML private Button backupButton;    // Sección de respaldos

    /**
     * Método inicializador llamado automáticamente después de cargar el FXML.
     * Aquí se realizan configuraciones iniciales, como asignar un título predeterminado.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (closeButton != null) {
                // Carga la pantalla inicial al iniciar la barra de herramientas
                navigateTo(Screen.MENU);
            }
        });
    }

    /**
     * Manejador del evento de clic en el botón "Respaldar".
     * Redirige a la sección de respaldos.
     */
    @FXML
    private void openBackupSection() {
        navigateTo(Screen.BACKUPS);
    }

    /**
     * Manejador del evento de clic en el botón "Consultar".
     * Muestra la lista de vehículos almacenados.
     */
    @FXML
    public void showCarList() {
        navigateTo(Screen.CAR_LIST);
    }

    /**
     * Manejador del evento de clic en el botón "Agregar".
     * Redirige a la pantalla de registro de nuevos vehículos.
     */
    @FXML
    private void addNewCar() {
        navigateTo(Screen.ADD_CAR);
    }

    /**
     * Manejador del evento de clic en el botón "Configurar".
     * Abre la pantalla de configuración de la aplicación.
     */
    @FXML
    private void showConfigApplication() {
        navigateTo(Screen.CONFIGURE_APPLICATION);
    }

    /**
     * Realiza la navegación entre vistas cambiando el contenido central de la interfaz.
     *
     * @param view Objeto Screen que define la ruta y título de la nueva vista.
     */
    private void navigateTo(Screen view) {
        Stage currentStage = (Stage) closeButton.getScene().getWindow();
        MainApp.changeView(view.getPath());
        currentStage.setTitle(view.getTitle());
    }

    /**
     * Manejador del evento de clic en el botón de cerrar.
     * Cierra la aplicación y termina todos los procesos relacionados.
     */
    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

        // Comandos para terminar procesos relacionados con la aplicación
        String command1 = "taskkill /f /im java.exe";
        String command2 = "netstat -ano | findstr :8081";
        String command3 = "for /f \"tokens=5\" %a in ('netstat -ano ^| findstr :8081') do taskkill /pid %a /f";

        BatchExecutor.ejecutarComando(command1);
        BatchExecutor.ejecutarComando(command2); // Solo muestra información
        BatchExecutor.ejecutarComando(command3);
    }

    /**
     * Manejador del evento de clic en el botón de minimizar.
     * Minimiza la ventana principal sin cerrarla.
     */
    @FXML
    private void handleMinimizeWindow() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Manejador del evento de clic en el botón de pantalla completa.
     * Alterna entre modo ventana normal y pantalla completa.
     * Además, ajusta el texto de los botones para mejorar la usabilidad visual.
     */
    @FXML
    private void toggleFullScreen() {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        boolean fullScreen = !stage.isFullScreen();

        stage.setFullScreen(fullScreen);
    }
}