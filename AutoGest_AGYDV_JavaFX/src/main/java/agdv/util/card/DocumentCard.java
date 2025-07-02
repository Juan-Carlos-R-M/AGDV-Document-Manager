package agdv.util.card;

import agdv.util.dialog.AlertScreen;
import agdv.util.loadFile.Image64;
import agdv.util.loadFile.PDF64;
import agdv.util.explorer.OpenFileExplorer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Componente reutilizable que representa un documento asociado a un vehículo.
 * Muestra información básica como:
 * - Nombre del documento
 * - Fecha de carga
 * - Miniatura o ícono del archivo (imagen o PDF)
 *
 * Permite realizar las siguientes acciones:
 * - Ver imagen o PDF en tamaño completo
 * - Descargar el documento al sistema local
 * - Eliminar el documento (notificando al controlador padre)
 */
public class DocumentCard {

    // Etiquetas para mostrar información del documento
    @FXML private Label nameLabel;     // Nombre del documento
    @FXML private Label dateLabel;    // Fecha de creación o carga
    @FXML private ImageView imageImg; // Vista previa del documento (imagen o ícono PDF)

    // Botones de acción
    @FXML private Button deleteButton;      // Elimina el documento
    @FXML private Button loadImageButton;   // Carga o muestra el documento completo

    /**
     * ID único del documento almacenado en el sistema.
     */
    private String idDocument;

    /**
     * Representación en Base64 del contenido del documento (imagen o PDF).
     */
    private String base64Image;

    /**
     * Indicador si el documento es un archivo PDF.
     */
    private boolean isPDF = false;

    /**
     * Listener utilizado para notificar al controlador padre sobre eventos importantes,
     * como la eliminación del documento.
     */
    private OnDocumentActionListener listener;

    /**
     * Constructor por defecto requerido por JavaFX.
     */
    public DocumentCard() {
        // Requerido por FXMLLoader
    }

    /**
     * Inicializa los datos del documento en esta tarjeta.
     *
     * @param id ID único del documento.
     * @param name Nombre del documento.
     * @param date Fecha de carga del documento.
     * @param base64 Contenido del documento en formato Base64.
     */
    public void setDocumentCard(String id, String name, String date, String base64) {
        this.idDocument = id;
        this.base64Image = base64;
        this.nameLabel.setText(name);
        this.dateLabel.setText(date);
        this.isPDF = name.toLowerCase().endsWith(".pdf");

        if (!this.isPDF) {
            Image image = Image64.convertBase64ToImage(this.base64Image);
            if (image != null) {
                this.imageImg.setImage(image);
            } else {
                System.err.println("No se pudo cargar la miniatura.");
            }
        }
    }


    /**
     * Manejador del evento de clic en el botón "Ver documento".
     * Abre una nueva ventana con la imagen completa o el PDF asociado.
     */
    @FXML
    private void loadImage() {
        if (this.base64Image != null && !this.base64Image.isEmpty()) {
            if (this.isPDF) {
                try {
                    File tempPDF = File.createTempFile("documento", ".pdf");
                    String folderPath = tempPDF.getParent();
                    String fileName = tempPDF.getName();
                    PDF64.saveBase64ToPDF(this.base64Image, folderPath, fileName);
                    PDF64.openPDF(tempPDF);
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertScreen.showError("Error", "No se pudo abrir el archivo PDF.");
                }
            } else {
                Image image = Image64.convertBase64ToImage(this.base64Image);
                if (image == null) {
                    AlertScreen.showError("Error", "No se pudo decodificar la imagen.");
                    return;
                }

                Stage stage = new Stage();
                ImageView largeImageView = new ImageView(image);
                largeImageView.setPreserveRatio(true);
                largeImageView.setFitWidth(800.0);
                largeImageView.setFitHeight(600.0);
                // CORRECCIÓN: pasar directamente la imagen sin usar new Node[]{...}
                StackPane root = new StackPane(largeImageView);
                Scene scene = new Scene(root, 800.0, 600.0);
                stage.setTitle("Imagen ampliada - " + this.nameLabel.getText());
                stage.setScene(scene);
                stage.show();
            }
        } else {
            AlertScreen.showError("Error", "No hay documento disponible.");
        }
    }

    /**
     * Manejador del evento de clic en el botón "Eliminar".
     * Notifica al listener para eliminar este documento del sistema.
     */
    @FXML
    private void deleteDocument() {
        if (this.listener != null && this.idDocument != null) {
            this.listener.onDeleteDocument(this.idDocument);
        }
    }
    /**
     * Manejador del evento de clic en el botón "Descargar".
     * Guarda el documento localmente en el equipo del usuario.
     */
    @FXML
    private void downloadImage() {
        if (this.base64Image != null && !this.base64Image.isEmpty()) {
            // CORRECCIÓN: validar que el componente esté en escena antes de obtener la ventana
            if (this.nameLabel.getScene() == null) {
                AlertScreen.showError("Error", "El componente aún no está en pantalla.");
                return;
            }
            Window window = this.nameLabel.getScene().getWindow();

            File saveTo = OpenFileExplorer.saveFile(window, this.nameLabel.getText());

            if (saveTo != null) {
                try {
                    String folderPath = saveTo.getParent();
                    String fileName = saveTo.getName();
                    if (this.isPDF) {
                        PDF64.saveBase64ToPDF(this.base64Image, folderPath, fileName);
                        AlertScreen.showSuccess("Archivo descargado correctamente.", "");
                    } else {
                        Image image = Image64.convertBase64ToImage(this.base64Image);
                        if (image != null) {
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                            // CORRECCIÓN: definir extensión según nombre del archivo
                            String extension = "png";
                            if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                                extension = "jpg";
                            }
                            ImageIO.write(bufferedImage, extension, saveTo);
                            AlertScreen.showSuccess("Imagen descargada correctamente.", "");
                        } else {
                            AlertScreen.showError("Error", "No se pudo decodificar la imagen.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertScreen.showError("Error", "No se pudo descargar el archivo.");
                }
            }
        } else {
            AlertScreen.showError("Error", "No hay archivo disponible para descargar.");
        }
    }


    /**
     * Establece el listener que recibirá eventos desde esta tarjeta.
     * Debe ser implementado por el controlador o vista contenedora.
     *
     * @param listener El listener que manejará las acciones del documento.
     */
    public void setOnDocumentActionListener(OnDocumentActionListener listener) {
        this.listener = listener;
    }

    /**
     * Interfaz funcional utilizada para notificar acciones realizadas sobre un documento.
     * Debe ser implementada por la vista o controlador que utilice varias instancias de DocumentCard.
     */
    public interface OnDocumentActionListener {
        /**
         * Notifica cuando se selecciona la acción de eliminar un documento.
         *
         * @param documentId ID único del documento a eliminar.
         */
        void onDeleteDocument(String documentId);
    }
}