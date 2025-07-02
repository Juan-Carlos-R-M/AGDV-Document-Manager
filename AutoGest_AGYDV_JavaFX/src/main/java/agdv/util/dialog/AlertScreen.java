package agdv.util.dialog;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Clase utilitaria que proporciona métodos estándar para mostrar diferentes tipos de alertas y diálogos modales.
 * Incluye:
 * - Alertas de error, información, éxito y advertencia.
 * - Diálogos de confirmación (Sí/No).
 * - Diálogos para ingresar texto (por ejemplo: nombre de archivo).
 *
 * Todos los métodos son invocados en el hilo de JavaFX usando Platform.runLater() para evitar problemas de concurrencia.
 */
public class AlertScreen {

    /**
     * Escenario principal de la aplicación necesario para establecer el propietario de las alertas modales.
     */
    private static Stage primaryStage;

    /**
     * Establece el escenario principal de la aplicación.
     *
     * @param stage El escenario principal a asignar.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Muestra una alerta de tipo ERROR al usuario.
     *
     * @param header  Título breve de la alerta.
     * @param content Mensaje detallado del error.
     */
    public static void showError(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if (primaryStage != null) alert.initOwner(primaryStage);
            alert.setTitle("Error");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Muestra una alerta de tipo INFORMACIÓN al usuario.
     *
     * @param header  Título breve de la alerta.
     * @param content Mensaje informativo.
     */
    public static void showInfo(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (primaryStage != null) alert.initOwner(primaryStage);
            alert.setTitle("Información");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Muestra una alerta de tipo ÉXITO al usuario.
     *
     * @param header  Título breve de la alerta.
     * @param content Mensaje sobre la acción exitosa.
     */
    public static void showSuccess(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (primaryStage != null) alert.initOwner(primaryStage);
            alert.setTitle("Éxito");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Muestra una alerta de tipo ADVERTENCIA al usuario.
     *
     * @param header  Título breve de la alerta.
     * @param content Mensaje de advertencia.
     */
    public static void showWarning(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if (primaryStage != null) alert.initOwner(primaryStage);
            alert.setTitle("Advertencia");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Muestra un diálogo de confirmación con opciones Sí/No.
     *
     * @param title   Título del diálogo.
     * @param header  Título breve del mensaje de confirmación.
     * @param content Mensaje detallado de la pregunta.
     * @return true si el usuario seleccionó "Aceptar", false si seleccionó "Cancelar".
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Muestra un diálogo para que el usuario ingrese un nombre de archivo.
     *
     * @return El nombre introducido por el usuario, o null si se canceló.
     */
    public static String askFileName() {
        TextInputDialog dialog = new TextInputDialog("nuevo_archivo");
        dialog.setTitle("Guardar archivo");
        dialog.setHeaderText("Nombre del archivo");
        dialog.setContentText("Ingresa el nombre que deseas para el archivo:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Muestra un diálogo de confirmación con título predeterminado.
     *
     * @param header  Título breve del mensaje de confirmación.
     * @param content Mensaje detallado de la pregunta.
     * @return true si el usuario seleccionó "Aceptar", false si seleccionó "Cancelar".
     */
    public static boolean showConfirmation(String header, String content) {
        return showConfirmation("Confirmar acción", header, content);
    }
}