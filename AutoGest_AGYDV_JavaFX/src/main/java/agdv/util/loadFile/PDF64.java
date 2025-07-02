package agdv.util.loadFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Clase utilitaria que proporciona métodos estándar para:
 * - Convertir archivos PDF a cadena Base64.
 * - Convertir cadenas Base64 a archivos PDF.
 * - Abrir archivos PDF usando el visor predeterminado del sistema.
 *
 * Útil para el manejo de documentos PDF en aplicaciones Java que necesitan almacenarlos
 * o transmitirlos como datos codificados (por ejemplo, en servicios REST).
 */
public class PDF64 {

    /**
     * Convierte un archivo PDF a una cadena codificada en Base64.
     *
     * @param pdfPath Ruta del archivo PDF a convertir.
     * @return Cadena Base64 que representa el contenido del archivo PDF.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static String convertPDFToBase64(String pdfPath) throws IOException {
        byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    /**
     * Guarda una cadena Base64 como un archivo PDF en la ubicación especificada.
     *
     * @param base64     Contenido del archivo PDF en formato Base64.
     * @param folderPath Carpeta donde se guardará el archivo.
     * @param fileName   Nombre del archivo a crear (con o sin extensión .pdf).
     * @throws IOException Si ocurre un error durante la escritura del archivo.
     */
    public static void saveBase64ToPDF(String base64, String folderPath, String fileName) throws IOException {
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("La cadena Base64 está vacía o es nula.");
        }

        if (!fileName.toLowerCase().endsWith(".pdf")) {
            fileName += ".pdf";
        }

        Path folder = Paths.get(folderPath);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder); // Crea la carpeta si no existe
        }

        Path filePath = folder.resolve(fileName);

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            Files.write(filePath, decodedBytes);
            System.out.println("Archivo PDF guardado en: " + filePath.toAbsolutePath());
        } catch (IllegalArgumentException e) {
            System.err.println("Error al decodificar Base64: cadena inválida.");
            throw e;
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo PDF: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Abre un archivo PDF utilizando la aplicación predeterminada del sistema operativo.
     *
     * @param pdfFile Archivo PDF a abrir.
     */
    public static void openPDF(File pdfFile) {
        try {
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.err.println("El entorno de escritorio no es compatible.");
                }
            } else {
                System.err.println("El archivo PDF no existe: ");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo abrir el archivo PDF.");
        }
    }
}