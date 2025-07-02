package agdv.util.services;

import agdv.model.Backup;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interfaz que define los métodos para interactuar con los endpoints REST relacionados con respaldos (Backups).
 * Utiliza Retrofit para mapear solicitudes HTTP a llamadas Java.
 *
 * Los métodos definidos aquí permiten realizar operaciones como:
 * - Obtener todos los respaldos del sistema.
 * - Subir un nuevo archivo de respaldo al servidor.
 * - Descargar un archivo de respaldo por su ID único.
 */
public interface BackupService {

    /**
     * Obtiene una lista con todos los respaldos almacenados en el sistema.
     *
     * @return Un objeto Call que contiene una lista de objetos Backup.
     */
    @GET("backup/getAll")
    Call<List<Backup>> getAllBackups();

    /**
     * Sube un archivo de respaldo al servidor.
     *
     * @param file Archivo ZIP u otro tipo de archivo de respaldo en formato multipart/form-data.
     * @return Un objeto Call que devuelve una cadena de respuesta del servidor (por ejemplo: mensaje de éxito o ID asignado).
     */
    @Multipart
    @POST("backup/add")
    Call<String> addBackup(@Part MultipartBody.Part file);

    /**
     * Descarga un archivo de respaldo desde el servidor por su ID único.
     *
     * @param id ID del respaldo que se desea descargar.
     * @return Un objeto Call que contiene el cuerpo de la respuesta HTTP con el archivo descargado.
     */
    @GET("backup/download/{id}")
    Call<ResponseBody> downloadBackup(@Path("id") String id);
}