package agdv.util.batch;

import java.io.File;
import java.io.IOException;

/**
 * Clase utilitaria para ejecutar comandos del sistema y archivos batch (.bat) desde Java.
 * Proporciona métodos estáticos para interactuar con scripts del sistema operativo,
 * útiles para tareas como respaldos, automatización, o integraciones externas.
 */
public class BatchExecutor {

    /**
     * Ejecuta un archivo .bat (script por lotes de Windows) y muestra su salida en consola.
     *
     * @param rutaBat Ruta absoluta o relativa del archivo .bat a ejecutar.
     * @return El objeto Process asociado a la ejecución del script, o null si falló.
     */
    public static Process ejecutarBat(String rutaBat) {
        Process process = null;
        try {
            File archivo = new File(rutaBat);

            // Verificar que el archivo exista antes de intentar ejecutarlo
            if (!archivo.exists()) {
                System.out.println("El archivo .bat no existe: " + rutaBat);
                return null;
            }

            // Configurar el ProcessBuilder para ejecutar el archivo .bat
            ProcessBuilder pb = new ProcessBuilder(rutaBat);
            pb.directory(archivo.getParentFile()); // Establece el directorio de trabajo al de la carpeta del archivo
            pb.inheritIO(); // Redirige la entrada/salida del proceso al de la aplicación para mostrar resultados en consola

            // Iniciar y esperar a que termine el proceso
            process = pb.start();
            process.waitFor();

            System.out.println("El archivo .bat se ejecutó correctamente.");
            return process;

        } catch (IOException | InterruptedException e) {
            System.err.println("Error al ejecutar el archivo .bat:");
            e.printStackTrace();
        }
        return process;
    }

    /**
     * Ejecuta un comando en la línea de comandos de Windows (cmd.exe).
     *
     * @param comando Comando a ejecutar en el símbolo del sistema.
     * @return El objeto Process asociado a la ejecución del comando, o null si falló.
     */
    public static Process ejecutarComando(String comando) {
        Process process = null;
        try {
            // Configurar el ProcessBuilder para ejecutar un comando en CMD
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comando);
            pb.inheritIO(); // Mostrar salida del comando en la consola de Java

            // Iniciar y esperar a que termine el proceso
            process = pb.start();
            process.waitFor();

            System.out.println("El comando se ejecutó correctamente.");
            return process;

        } catch (IOException | InterruptedException e) {
            System.err.println("Error al ejecutar el comando:");
            e.printStackTrace();
        }
        return process;
    }
}