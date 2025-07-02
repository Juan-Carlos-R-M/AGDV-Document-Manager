package agdv.util.services;

import agdv.util.properties.ConfigManager;

/**
 * Clase de configuración que proporciona acceso a los parámetros de la aplicación,
 * principalmente la URL base de la API utilizada por el sistema.
 *
 * Esta clase utiliza el patrón Singleton a través de {@link ConfigManager} para cargar
 * y gestionar las propiedades de configuración desde un archivo externo (por ejemplo: config.properties).
 */
public class Config {

    /**
     * Instancia del gestor de configuración que permite acceder a las propiedades del sistema.
     * Se utiliza para obtener valores dinámicos como la URL base de la API.
     */
    private static final ConfigManager configManager = ConfigManager.getInstance();

    /**
     * URL base del servicio REST utilizado por la aplicación.
     * Obtenida desde el archivo de configuración usando la clave "api.url".
     */
    public static String URL_BASE = configManager.getString("api.url");

    /**
     * Método alternativo para obtener la URL base del servicio REST.
     * Puede usarse directamente o mediante el campo estático {@link #URL_BASE}.
     *
     * @return El valor de la propiedad "api.url" desde el archivo de configuración.
     */
    public static String getUrlBase() {
        return configManager.getString("api.url");
    }
}