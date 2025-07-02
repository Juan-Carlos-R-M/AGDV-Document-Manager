package agdv.controller;

import agdv.MainApp;
import agdv.model.Car;
import agdv.util.services.ApiClient;
import agdv.util.services.CarService;
import agdv.util.Screen;
import agdv.util.card.CarCard;
import agdv.util.dialog.AlertScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CarListController {

    @FXML private VBox carListContainer;
    @FXML private Button backButton;
    @FXML private TextField searchCarField;

    private List<Car> carList;

    public void initialize() throws IOException {
        loadCars();
        searchCarField.setOnAction(event -> {
            try {
                searchCar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadCars() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long startTime = System.currentTimeMillis();

        Runnable retryTask = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now - startTime >= 10000) {
                    scheduler.shutdown();
                    Platform.runLater(() ->
                            AlertScreen.showError("Error de conexión", "No se pudo conectar al servidor después de varios intentos.")
                    );
                    return;
                }

                CarService service = ApiClient.getClient().create(CarService.class);
                Call<List<Car>> call = service.getAllCar();
                call.enqueue(new Callback<List<Car>>() {
                    @Override
                    public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            carList = response.body();
                            Platform.runLater(() -> displayCars());
                            scheduler.shutdown();
                        } else {
                            System.err.println("Error en respuesta: " + response.code());
                            try {
                                System.err.println("Error body: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Car>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        };

        scheduler.scheduleAtFixedRate(retryTask, 0, 2, TimeUnit.SECONDS);
    }

    @FXML
    public void searchCar() throws IOException {
        String query = searchCarField.getText().trim();
        if (query.isEmpty()) {
            loadCars();
            return;
        }

        CarService service = ApiClient.getClient().create(CarService.class);
        Call<List<Car>> call = service.searchCar(query);

        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carList = response.body();
                    Platform.runLater(() -> displayCars());
                } else {
                    Platform.runLater(() ->
                            AlertScreen.showError("Error", "No se pudieron cargar los vehículos del servidor.")
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable throwable) {
                Platform.runLater(() ->
                        AlertScreen.showError("Error", "No se pudo conectar al servidor.")
                );
            }
        });
    }

    private void displayCars() {
        carListContainer.getChildren().clear();

        for (Car car : carList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/agdv/util/card/CarCard.fxml"));
                Node node = loader.load();

                CarCard cardController = loader.getController();
                cardController.setCarData(car);

                cardController.setOnCarActionListener(new CarCard.OnCarActionListener() {
                    @Override
                    public void onEditCar(Car car) {
                        // Mejorar aquí para evitar null en backButton
                        Stage currentStage = getStage();
                        if (currentStage != null) {
                            Object controller = MainApp.changeView(Screen.ADD_CAR.getPath());
                            currentStage.setTitle(Screen.ADD_CAR.getTitle());

                            if (controller instanceof CarFormController addCarController) {
                                addCarController.setCarData(car);
                            }
                        } else {
                            System.err.println("No se pudo obtener la ventana para cambiar pantalla");
                        }
                    }

                    @Override
                    public void onDeleteCar(String id) {
                        deleteCar(id);
                    }
                });

                carListContainer.getChildren().add(node);
            } catch (IOException e) {
                AlertScreen.showError("Error al cargar tarjeta", "No se pudo mostrar el auto: " + car.getBrand());
            }
        }
    }

    public void deleteCar(String carId) {
        boolean confirmed = AlertScreen.showConfirmation("Eliminar vehículo", "¿Quieres borrar el vehículo?", "");

        if (!confirmed) return;

        CarService service = ApiClient.getClient().create(CarService.class);
        Call<Void> call = service.deleteCar(carId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("✅ Vehículo eliminado exitosamente.");
                    loadCars();
                } else {
                    AlertScreen.showError("Error", "No se pudo eliminar el vehículo.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getMessage());
                AlertScreen.showError("Error de conexión", "No se pudo conectar al servidor.");
            }
        });
    }

    @FXML
    private void goBack() {
        switchScreen(Screen.MENU);
    }

    @FXML
    private void addCar() {
        switchScreen(Screen.ADD_CAR);
    }

    private void switchScreen(Screen screen) {
        Stage stage = getStage();
        if (stage != null) {
            MainApp.changeView(screen.getPath());
            stage.setTitle(screen.getTitle());
        } else {
            System.err.println("No se pudo obtener la ventana para cambiar pantalla");
        }
    }

    /**
     * Método seguro para obtener la ventana principal desde el botón backButton.
     * Evita NullPointerException si el botón o su escena no están cargados.
     */
    private Stage getStage() {
        if (backButton != null && backButton.getScene() != null) {
            return (Stage) backButton.getScene().getWindow();
        }
        // Si no está disponible el backButton, podrías obtener Stage por otro medio si tienes referencia
        return null;
    }
}
