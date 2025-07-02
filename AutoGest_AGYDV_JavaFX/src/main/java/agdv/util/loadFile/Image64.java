package agdv.util.loadFile;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Clase utilitaria que proporciona métodos estándar para:
 * - Convertir archivos de imagen a cadena Base64.
 * - Convertir cadenas Base64 a objetos Image de JavaFX.
 * - Mostrar imágenes en un ImageView desde Base64.
 * - Abrir imágenes con el visor predeterminado del sistema.
 *
 * Especialmente útil para almacenamiento, transmisión y visualización segura de imágenes como datos codificados.
 */
public class Image64 {

    /**
     * Muestra una imagen en un ImageView desde una cadena Base64.
     *
     * @param imageView ImageView donde se mostrará la imagen.
     * @param base64    Cadena Base64 que representa la imagen.
     */
    public static void showImage64(ImageView imageView, String base64) {
        if (base64 == null || base64.isEmpty()) {
            System.err.println("Base64 recibido es nulo o vacío.");
            return;
        }
        Image image = convertBase64ToImage(base64);
        if (image != null) {
            imageView.setImage(image);
        } else {
            System.err.println("No se pudo cargar la imagen desde Base64.");
        }
    }


    /**
     * Convierte una imagen del sistema de archivos a una cadena codificada en Base64.
     *
     * @param imagePath Ruta del archivo de imagen a convertir.
     * @return Cadena Base64 que representa el contenido de la imagen.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static String convertImageToBase64(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("La ruta de la imagen es nula o vacía.");
        }

        File file = new File(imagePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("El archivo no existe o no es válido: " + imagePath);
        }

        byte[] imageBytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Convierte una cadena Base64 a un objeto Image de JavaFX.
     *
     * @param base64 Cadena Base64 que contiene los datos de la imagen.
     * @return Objeto Image si la conversión fue exitosa, o null si falló.
     */
    public static Image convertBase64ToImage(String base64) {
        if (base64 == null || base64.trim().isEmpty()) {
            System.err.println("Base64 vacío");
            return null;
        }
        try {
            if (base64.startsWith("data:image")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(base64);
            return new Image(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Valida que una cadena sea una representación válida de Base64.
     *
     * @param base64 Cadena a validar.
     * @return true si la cadena tiene formato Base64 válido, false en caso contrario.
     */
    private static boolean isValidBase64(String base64) {
        base64 = base64.trim().replaceAll("[=\\s]+", ""); // Permite relleno, espacios y saltos
        return Pattern.matches("^[A-Za-z0-9+/]+$", base64);
    }
    /**
     * Abre un archivo de imagen utilizando la aplicación predeterminada del sistema operativo.
     *
     * @param imageFile Archivo de imagen a abrir.
     */
    public static void openImage(File imageFile) {
        try {
            if (imageFile != null && imageFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(imageFile);
                } else {
                    System.err.println("El entorno de escritorio no es compatible.");
                }
            } else {
                System.err.println("La imagen no existe: " + (imageFile != null ? imageFile.getAbsolutePath() : "null"));
            }
        } catch (IOException e) {
            System.err.println("No se pudo abrir la imagen: " + e.getMessage());
        }
    }
}