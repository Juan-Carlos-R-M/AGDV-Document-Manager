package agdv.util.properties;

import java.io.*;
import java.util.Properties;

/**
 * Clase encargada de gestionar las propiedades de configuración de la aplicación.
 * Carga los valores desde un archivo `config.properties` y permite acceder a ellos
 * mediante métodos tipificados (getString, getInt, getBoolean, etc.).
 *
 * Implementa el patrón Singleton para garantizar una única instancia durante la ejecución.
 */
public class ConfigManager {

    /**
     * Instancia única de esta clase (Singleton).
     */
    private static ConfigManager instance;

    /**
     * Objeto Properties que almacena los pares clave-valor leídos del archivo de configuración.
     */
    private Properties properties;

    /**
     * Ruta relativa del archivo de configuración donde se guardan los ajustes.
     */
    private final String FILE_PATH = "config.properties";

    /**
     * Constructor privado que carga las propiedades desde el archivo especificado.
     * Si el archivo no existe, lo crea vacío.
     */
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Devuelve la instancia única de esta clase (patrón Singleton).
     * Si no existe, la crea.
     *
     * @return La instancia única de ConfigManager.
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Carga las propiedades desde el archivo de configuración.
     * Si ocurre un error durante la lectura, lanza una excepción en tiempo de ejecución.
     */
    private void loadProperties() {
        File configFile = new File(FILE_PATH);
        if (!configFile.exists()) {
            saveToFile(); // Crea el archivo vacío si no existe
        }

        try (InputStream input = new FileInputStream(FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el archivo de configuración", e);
        }
    }

    /**
     * Guarda las propiedades actuales en el archivo de configuración.
     * Si ocurre un error durante la escritura, lanza una excepción en tiempo de ejecución.
     */
    private void saveToFile() {
        try (OutputStream output = new FileOutputStream(FILE_PATH)) {
            properties.store(output, "Configuración guardada");
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo de configuración", e);
        }
    }

    /**
     * Guarda una propiedad como cadena sin escapar caracteres especiales.
     * El archivo se reescribe línea por línea manteniendo el formato plano.
     *
     * @param key   Clave de la propiedad.
     * @param value Valor a guardar.
     */
    public void saveStringRaw(String key, String value) {
        File file = new File(FILE_PATH);
        Properties props = new Properties();

        // Cargar propiedades existentes si el archivo ya existe
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Modificar la propiedad deseada
        props.setProperty(key, value);

        // Guardar el archivo sin caracteres escapados
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String propKey : props.stringPropertyNames()) {
                writer.write(propKey + "=" + props.getProperty(propKey));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Actualizar también la instancia en memoria
        properties = props;
    }

    /**
     * Guarda un valor entero en el archivo de configuración.
     *
     * @param key   Clave de la propiedad.
     * @param value Valor entero a guardar.
     */
    public void saveInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
        saveToFile();
    }

    /**
     * Guarda un valor booleano en el archivo de configuración.
     *
     * @param key   Clave de la propiedad.
     * @param value Valor booleano a guardar.
     */
    public void saveBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        saveToFile();
    }

    /**
     * Guarda un valor cadena en el archivo de configuración.
     *
     * @param key   Clave de la propiedad.
     * @param value Valor cadena a guardar.
     */
    public void saveString(String key, String value) {
        properties.setProperty(key, value);
        saveToFile();
    }

    /**
     * Obtiene el valor asociado a una clave como cadena.
     *
     * @param key Clave de la propiedad a obtener.
     * @return Valor asociado a la clave, o null si no existe.
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Obtiene el valor asociado a una clave como entero.
     *
     * @param key          Clave de la propiedad a obtener.
     * @param defaultValue Valor predeterminado si la clave no existe o tiene formato inválido.
     * @return Valor entero asociado a la clave, o el valor predeterminado.
     */
    public int getInt(String key, int defaultValue) {
        String val = properties.getProperty(key);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obtiene el valor asociado a una clave como booleano.
     *
     * @param key Clave de la propiedad a obtener.
     * @return Valor booleano asociado a la clave, o false si no existe o tiene formato inválido.
     */
    public boolean getBoolean(String key) {
        String val = properties.getProperty(key);
        return Boolean.parseBoolean(val);
    }
}