package agdv.util.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase encargada de configurar y proporcionar una instancia única de Retrofit para realizar llamadas HTTP.
 * Implementa un patrón Singleton para evitar crear múltiples instancias innecesarias.
 *
 * Además, utiliza Gson como serializador/deserializador JSON con soporte flexible (leniente) para respuestas mal formadas.
 */
public class ApiClient {

    /**
     * Instancia única de Retrofit utilizada para las llamadas a la API REST.
     */
    private static Retrofit retrofit = null;

    /**
     * Instancia de Gson configurada con modo leniente para manejar respuestas JSON poco estrictas.
     */
    private static final Gson gson = new GsonBuilder().setLenient().create();

    /**
     * Devuelve la instancia actual de Retrofit.
     * Si aún no existe, la crea llamando a {@link #createClient()}.
     *
     * @return La instancia única de Retrofit.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            createClient();
        }
        return retrofit;
    }

    /**
     * Crea una nueva instancia de Retrofit si es necesario.
     * Configura la URL base desde {@link Config#getUrlBase()} y registra el convertidor Gson.
     */
    public static void createClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Config.getUrlBase())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /**
     * Reinicia la instancia de Retrofit a null.
     * Útil cuando se necesita recrear el cliente, por ejemplo al cambiar la URL base dinámicamente.
     */
    public static void resetClient() {
        retrofit = null;
    }
}