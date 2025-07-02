package agdv.util.card;

import agdv.controller.BackupsController;
import agdv.model.Backup;
import agdv.util.services.ApiClient;
import agdv.util.services.BackupService;
import agdv.util.dialog.AlertScreen;
import agdv.util.explorer.OpenFileExplorer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;

import static agdv.util.dialog.AlertScreen.showError;

/**
 * Clase que representa una tarjeta individual de respaldo (backup) en la interfaz gráfica.
 * Permite mostrar información del respaldo (nombre, fecha, tamaño) y gestionar acciones como:
 * - Descargar el archivo ZIP desde el servidor.
 * - Guardarlo localmente mediante diálogo de selección de carpeta.
 *
 * Esta clase se utiliza típicamente dentro de listas o contenedores de JavaFX que muestran múltiples respaldos.
 */
public class BackupsCard {

    // Componentes visuales vinculados al FXML
    @FXML private Label backupNameLabel;  // Nombre del respaldo
    @FXML private Label backupDateLabel;  // Fecha de creación del respaldo
    @FXML private Label backupSizeLabel;  // Tamaño del archivo de respaldo
    @FXML private Button downloadButton;  // Botón para descargar el respaldo

    private Backup backup;                // Objeto de modelo asociado a esta tarjeta
    private BackupsController controller; // Controlador principal para comunicación externa

    /**
     * Establece el objeto Backup asociado a esta tarjeta y actualiza los campos visuales.
     *
     * @param backup El objeto Backup que contiene los datos del respaldo.
     */
    public void setBackup(Backup backup) {
        this.backup = backup;
        if (backup != null) {
            // Actualizar etiquetas con los datos del respaldo
            backupNameLabel.setText(backup.getName() != null ? backup.getName() : "Respaldo");
            backupDateLabel.setText(backup.getDate());
            backupSizeLabel.setText(backup.getSize() + " KB");
        }
    }

    /**
     * Establece el controlador asociado a esta tarjeta para permitir comunicación entre capas.
     *
     * @param controller El controlador encargado de manejar las operaciones de respaldo.
     */
    public void setController(BackupsController controller) {
        this.controller = controller;
    }

    /**
     * Manejador del evento de clic en el botón de descarga.
     * Inicia la descarga del archivo ZIP desde el servidor usando Retrofit.
     */
    @FXML
    private void downloadBackup() {
        if (backup != null && backup.getId() != null && !backup.getId().isEmpty()) {
            String backupId = backup.getId(); // ID único del respaldo en el servidor
            System.out.println("Iniciando descarga... ID: " + backupId);

            // Crear servicio API para descargar el archivo
            BackupService service = ApiClient.getClient().create(BackupService.class);
            Call<ResponseBody> call = service.downloadBackup(backupId);

            // Ejecutar petición asíncrona
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().contentLength() > 0) {
                            // Usamos Platform.runLater() para interactuar con elementos de UI
                            Platform.runLater(() -> saveFile(response.body(), backup.getName() + ".zip"));
                        } else {
                            showError("Error", "Archivo vacío recibido.");
                        }
                    } else {
                        showError("Error", "No se pudo descargar el respaldo. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showError("Error", "Fallo al descargar: " + t.getMessage());
                }
            });
        } else {
            showError("Error", "ID del respaldo no disponible o inválido.");
        }
    }

    /**
     * Guarda el archivo descargado desde el servidor en el sistema del usuario.
     *
     * @param body     Cuerpo de la respuesta HTTP que contiene el contenido binario del archivo.
     * @param fileName Nombre sugerido para guardar el archivo.
     */
    private void saveFile(ResponseBody body, String fileName) {
        try {
            // Abrir diálogo para que el usuario seleccione ubicación y nombre del archivo
            File file = OpenFileExplorer.saveFile(downloadButton.getScene().getWindow(), fileName);

            if (file == null) return; // Cancelado por el usuario

            // Guardar el archivo en disco
            try (InputStream is = body.byteStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int read;

                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }

                fos.flush();

                // Mostrar mensaje de éxito
                AlertScreen.showInfo("Éxito", "Archivo guardado correctamente en: " + file.getAbsolutePath());

            } catch (IOException e) {
                showError("Error", "No se pudo guardar el archivo.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            showError("Error", "Error al intentar guardar el archivo.");
            e.printStackTrace();
        }
    }
}