package agdv.util.services;

import java.util.List;
import agdv.model.Insurance;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interfaz que define los métodos para interactuar con los endpoints REST relacionados con seguros.
 * Utiliza Retrofit para mapear solicitudes HTTP a llamadas Java.
 *
 * Actualmente permite:
 * - Obtener una lista de todos los seguros disponibles en el sistema.
 */
public interface InsuranceService {

    /**
     * Obtiene una lista con todos los seguros almacenados en el sistema.
     *
     * @return Un objeto Call que contiene una lista de objetos Insurance.
     */
    @GET("insurance/getAll")
    Call<List<Insurance>> getAllInsurance();
}