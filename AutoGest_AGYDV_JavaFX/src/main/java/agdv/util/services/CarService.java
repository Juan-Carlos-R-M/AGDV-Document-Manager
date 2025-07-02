package agdv.util.services;

import agdv.model.Car;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Interfaz que define los métodos para interactuar con los endpoints REST relacionados con vehículos.
 * Utiliza Retrofit para mapear las solicitudes HTTP a llamadas Java.
 *
 * Los métodos definidos aquí se conectan con un backend que expone funcionalidades como:
 * - Obtener todos los vehículos
 * - Agregar, actualizar y eliminar vehículos
 * - Buscar vehículos por término clave
 */
public interface CarService {

    /**
     * Obtiene una lista con todos los vehículos almacenados en el sistema.
     *
     * @return Un objeto Call que contiene una lista de objetos Car.
     */
    @GET("car/getAll")
    Call<List<Car>> getAllCar();

    /**
     * Agrega un nuevo vehículo al sistema.
     *
     * @param car Objeto Car con los datos del vehículo a agregar.
     * @return Un objeto Call que devuelve el vehículo agregado desde el servidor.
     */
    @POST("car/add")
    Call<Car> addCar(@Body Car car);

    /**
     * Elimina un vehículo del sistema por su ID.
     *
     * @param idCar ID único del vehículo a eliminar.
     * @return Un objeto Call vacío (Void) que indica si la operación fue exitosa.
     */
    @DELETE("car/delete/{idCar}")
    Call<Void> deleteCar(@Path("idCar") String idCar);

    /**
     * Actualiza los datos de un vehículo existente.
     *
     * @param car Objeto Car con los nuevos datos del vehículo.
     * @return Un objeto Call que devuelve el vehículo actualizado desde el servidor.
     */
    @PATCH("car/update")
    Call<Car> updateCar(@Body Car car);

    /**
     * Busca vehículos cuyos campos coincidan con el término de búsqueda dado.
     *
     * @param query Término de búsqueda utilizado para filtrar resultados.
     * @return Un objeto Call que contiene una lista de vehículos coincidentes.
     */
    @GET("car/searchCar")
    Call<List<Car>> searchCar(@Query("query") String query);
}