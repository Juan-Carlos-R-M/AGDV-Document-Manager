package agdv.util.explorer;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Clase utilitaria que proporciona funcionalidad para interactuar con el explorador de archivos del sistema.
 * Incluye métodos para:
 * - Cargar un archivo desde el sistema (abrir diálogo de selección).
 * - Guardar un archivo en una ubicación elegida por el usuario (guardar como...).
 *
 * Ideal para integrarse con operaciones como carga de documentos, imágenes o respaldos ZIP.
 */
public class OpenFileExplorer {

    /**
     * Abre un diálogo para que el usuario seleccione un archivo.
     * Solo se permiten ciertos tipos de archivos multimedia y PDF.
     *
     * @param parentWindow Ventana padre donde se mostrará el diálogo.
     * @return El archivo seleccionado por el usuario, o null si se canceló la operación.
     */
    public static File loadFile(Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona el documento");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes y PDF (*.jpg, *.jpeg, *.png, *.gif, *.pdf)",
                        "*.jpg", "*.jpeg", "*.png", "*.gif", "*.pdf")
        );

        return fileChooser.showOpenDialog(parentWindow);
    }

    /**
     * Abre un diálogo para que el usuario elija una ubicación y nombre para guardar un archivo.
     * Se sugiere un nombre predeterminado.
     *
     * @param parentWindow     Ventana padre donde se mostrará el diálogo.
     * @param suggestedFileName Nombre sugerido para el archivo a guardar.
     * @return El archivo con la ruta y nombre elegidos por el usuario, o null si se canceló la operación.
     */
    public static File saveFile(Window parentWindow, String suggestedFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar respaldo como...");
        fileChooser.setInitialFileName(suggestedFileName);

        // Filtros de archivo opcionales
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivo ZIP", "*.zip"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        return fileChooser.showSaveDialog(parentWindow);
    }
}