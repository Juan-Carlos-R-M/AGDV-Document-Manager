package agdv.controller;

// Importaciones necesarias para JavaFX y utilidades del sistema
import agdv.MainApp;
import agdv.util.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controlador para la pantalla principal del menú.
 * Gestiona las acciones de navegación a otras vistas.
 *
 * Este controlador se encarga de:
 * - Manejar los clics en los botones del menú.
 * - Navegar entre diferentes pantallas definidas en el enum `Screen`.
 */
public class MainMenuController {

    // === Componentes FXML vinculados desde el archivo FXML ===
    @FXML private Button addCarButton;     // Botón para ir al formulario de añadir vehículo
    @FXML private Button carListButton;    // Botón para ver la lista de vehículos
    @FXML private Button downloadButton;   // Botón para gestionar respaldos (backups)

    /**
     * Método llamado automáticamente por JavaFX al cargar la vista.
     * Aquí puedes inicializar componentes adicionales o aplicar estilos personalizados.
     */
    @FXML
    public void initialize() {
        // Puedes agregar aquí inicializaciones adicionales si es necesario
    }

    /**
     * Navega a otra pantalla definida en el enum `Screen`.
     *
     * @param screen Vista destino a la cual navegar
     */
    private void switchScreen(Screen screen) {
        Stage currentStage = (Stage) addCarButton.getScene().getWindow();
        MainApp.changeView(screen.getPath());
        currentStage.setTitle(screen.getTitle());
    }

    // === Eventos FXML: acciones asociadas a los botones del menú ===

    /**
     * Acción ejecutada al hacer clic en el botón "Descargar Respaldo".
     * Navega a la pantalla de gestión de respaldos (`BackupsController`).
     */
    @FXML
    private void downloadBackup() {
        switchScreen(Screen.BACKUPS);
    }

    /**
     * Acción ejecutada al hacer clic en el botón "Lista de Vehículos".
     * Navega a la pantalla de listado de vehículos (`CarListController`).
     */
    @FXML
    private void showCarList() {
        switchScreen(Screen.CAR_LIST);
    }

    /**
     * Acción ejecutada al hacer clic en el botón "Agregar Vehículo".
     * Navega a la pantalla de formulario para añadir un nuevo vehículo (`CarFormController`).
     */
    @FXML
    private void goToAddCar() {
        switchScreen(Screen.ADD_CAR);
    }
}