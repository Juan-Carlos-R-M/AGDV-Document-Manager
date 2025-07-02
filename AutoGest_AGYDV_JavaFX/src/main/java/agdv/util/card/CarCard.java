package agdv.util.card;

import agdv.model.Car;
import agdv.util.loadFile.Image64;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Componente reutilizable que representa un vehículo (Car) en forma de tarjeta.
 * Muestra los datos principales del vehículo y proporciona botones para:
 * - Editar el vehículo
 * - Eliminar el vehículo
 *
 * Utiliza FXML para definir su interfaz visual y se comunica con el controlador padre
 * a través de un listener personalizado.
 */
public class CarCard {

    // Contenedor principal de la tarjeta
    @FXML private HBox carCardContainer;

    // Imagen del auto (mostrada mediante Base64)
    @FXML private ImageView imageCarImageView;

    // Etiquetas para mostrar información del vehículo
    @FXML private Label serialNumberLabel; // ID único del vehículo
    @FXML private Label brandLabel;        // Marca del vehículo
    @FXML private Label modelLabel;        // Modelo del vehículo
    @FXML private Label yearLabel;         // Año de fabricación
    @FXML private Label colorLabel;        // Color del vehículo

    // Botones de acción
    @FXML private Button editButton;       // Botón para editar el vehículo
    @FXML private Button deleteButton;     // Botón para eliminar el vehículo

    /**
     * Objeto Car asociado a esta tarjeta.
     * Contiene todos los datos del vehículo a mostrar.
     */
    private Car car;

    /**
     * Listener que permite comunicarse con el controlador o vista contenedora.
     * Se usa para notificar acciones como "editar" o "eliminar".
     */
    private OnCarActionListener listener;

    /**
     * Establece los datos del vehículo en la tarjeta.
     * Actualiza todos los componentes visuales con los valores del objeto Car.
     *
     * @param car Objeto Car con los datos del vehículo a mostrar.
     */
    public void setCarData(Car car) {
        this.car = car;

        if (car != null) {
            serialNumberLabel.setText(car.getId());
            brandLabel.setText(car.getBrand());
            modelLabel.setText(car.getModel());
            yearLabel.setText(String.valueOf(car.getYear()));
            colorLabel.setText(car.getColor());

            // Mostrar imagen si existe
            if (car.getImage() != null && !car.getImage().isEmpty()) {
                Image64.showImage64(imageCarImageView, car.getImage());
            }
        }
    }

    /**
     * Manejador del evento de clic en el botón "Editar".
     * Llama al método onEditCar del listener pasando el objeto Car completo.
     */
    @FXML
    private void carEdit() {
        if (listener != null && car != null) {
            listener.onEditCar(car);
        }
    }

    /**
     * Manejador del evento de clic en el botón "Eliminar".
     * Llama al método onDeleteCar del listener pasando el ID del vehículo.
     */
    @FXML
    private void carDelete() {
        if (listener != null && car != null) {
            listener.onDeleteCar(car.getId());
        }
    }

    /**
     * Asigna un listener para recibir eventos desde esta tarjeta.
     * Debe ser implementado por la vista o controlador que contiene varias instancias de CarCard.
     *
     * @param listener El listener que maneja las acciones de edición y eliminación.
     */
    public void setOnCarActionListener(OnCarActionListener listener) {
        this.listener = listener;
    }

    /**
     * Interfaz funcional utilizada para notificar acciones realizadas sobre un vehículo.
     * Debe ser implementada por la vista o controlador que utilice esta tarjeta.
     */
    public interface OnCarActionListener {
        /**
         * Notifica cuando se selecciona la acción de editar un vehículo.
         *
         * @param car Objeto Car con los datos del vehículo a editar.
         */
        void onEditCar(Car car);

        /**
         * Notifica cuando se selecciona la acción de eliminar un vehículo.
         *
         * @param id ID único del vehículo a eliminar.
         */
        void onDeleteCar(String id);
    }
}