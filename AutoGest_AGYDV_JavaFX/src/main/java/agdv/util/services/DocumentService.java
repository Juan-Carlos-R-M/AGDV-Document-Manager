package agdv.util.services;

import agdv.model.Document;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interfaz que define los métodos para interactuar con los endpoints REST relacionados con documentos.
 * Utiliza Retrofit para mapear solicitudes HTTP a llamadas Java.
 *
 * Los métodos definidos permiten realizar operaciones como:
 * - Agregar un documento asociado a un vehículo.
 * - Eliminar un documento por su ID.
 * - Obtener todos los documentos asociados a un vehículo específico.
 */
public interface DocumentService {

    /**
     * Agrega un nuevo documento asociado a un vehículo.
     *
     * @param idCar      ID del vehículo al que se asociará el documento.
     * @param document   Objeto Document que contiene los datos del documento a agregar.
     * @return Un objeto Call que devuelve el documento creado desde el servidor.
     */
    @POST("document/add")
    Call<Document> addDocument(@Query("idCar") String idCar, @Body Document document);

    /**
     * Elimina un documento del sistema por su ID único.
     *
     * @param documentId ID único del documento a eliminar.
     * @return Un objeto Call que devuelve una cadena indicando el resultado de la operación (por ejemplo: mensaje de éxito).
     */
    @DELETE("document/delete/{documentId}")
    Call<String> deleteDocument(@Path("documentId") String documentId);

    /**
     * Obtiene una lista de todos los documentos asociados a un vehículo específico.
     *
     * @param carId ID del vehículo cuyos documentos se desean obtener.
     * @return Un objeto Call que contiene una lista de objetos Document.
     */
    @GET("document/byCar")
    Call<List<Document>> getAllDocument(@Query("idCar") String carId);
}