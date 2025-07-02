package agdv.controller;

import agdv.MainApp;
import agdv.model.Car;
import agdv.model.Document;
import agdv.model.Insurance;
import agdv.util.services.*;
import agdv.util.Screen;
import agdv.util.card.DocumentCard;
import agdv.util.dialog.AlertScreen;
import agdv.util.loadFile.Image64;
import agdv.util.loadFile.PDF64;
import agdv.util.explorer.OpenFileExplorer;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controlador para la pantalla de añadir o editar un automóvil.
 */
public class CarFormController {

    // Componentes FXML
    @FXML private Button backButton;
    @FXML private Button saveButton;
    @FXML private Button loadDocumentsBtn;
    @FXML private ComboBox<Insurance> insuranceComboBox;
    @FXML private VBox listDocumentVbox;
    @FXML private VBox documentContainer;
    @FXML private TextField serialNumberField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TextField colorField;
    @FXML private TextArea notesTextArea;
    @FXML private ImageView imageView;
    @FXML private Label serialNumberLabel, brandLabel, modelLabel, yearLabel, colorLabel, notesLabel, insuranceLabel;

    // Atributos internos
    private boolean edit = false;
    private List<Insurance> listInsurance;
    private final List<Document> documentList = new ArrayList<>();
    private Car car = new Car();                     // Modelo principal del vehículo
    private String base64Image = "";     // Imagen nueva seleccionada
    private  File selectedFile ;
    /**
     * Método inicializador de la vista (llamado por JavaFX).
     */
    public void initialize() {
        getAllInsurance();
        setupYearComboBox();
        loadDocumentsBtn.setDisable(true);
        saveButton.setDisable(true);
        serialNumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText().toUpperCase();
            if (newText.length() <= 17) {
                change.setText(change.getText().toUpperCase());
                return change;
            } else {
                return null;
            }
        }));
    }
    @FXML
    private void setupDragOver() {
        documentContainer.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
    }
    @FXML
    private void setupDragDropped() {
        documentContainer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()){
                success = true;
                for (File file : db.getFiles()){
                    selectedFile=file;
                    addDocument();
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    private void setupYearComboBox() {
        yearComboBox.getItems().clear();
        for (int year = LocalDate.now().getYear(); year >= 1900; year--) {
            yearComboBox.getItems().add(String.valueOf(year));
        }
        yearComboBox.getSelectionModel().select(0); // Seleccionar año actual
    }

    /**
     * Establece los datos del vehículo usando el modelo completo.
     */
    public void setCarData(Car car) {
        this.car = car;

        if (car != null) {
            serialNumberField.setText(car.getId());
            brandField.setText(car.getBrand());
            modelField.setText(car.getModel());

            yearComboBox.getSelectionModel().select(String.valueOf(car.getYear()));
            colorField.setText(car.getColor());
            notesTextArea.setText(car.getObservations());

            if (car.getImage() != null && !car.getImage().isEmpty()) {
                imageView.setImage(Image64.convertBase64ToImage(car.getImage()));
            }

            this.base64Image = car.getImage(); // Guardamos imagen base64
            setupInsuranceSelection(car.getInsurance());

            edit = true;
            serialNumberField.setDisable(true);
            loadDocumentsBtn.setDisable(false);
            getAllDocument();
        }
    }

    private void setupInsuranceSelection(Insurance insurance) {
        if (listInsurance != null && !listInsurance.isEmpty()) {
            Insurance selected = listInsurance.stream()
                    .filter(i -> i.getId() == insurance.getId())
                    .findFirst()
                    .orElse(null);
            insuranceComboBox.setValue(selected);
        } else {
            insuranceComboBox.setValue(insurance);
        }
    }

    // === Navegación entre pantallas ===
    @FXML
    private void goBack() {
        switchScreen(Screen.MENU);
        edit = false;
    }

    private void switchScreen(Screen screen) {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        MainApp.changeView(screen.getPath());
        currentStage.setTitle(screen.getTitle());
    }

    // === Carga de imagen ===
    @FXML
    public void loadImage() {
        try {
            File file = OpenFileExplorer.loadFile(null);
            if (file != null) {
                // Convertir imagen a Base64
                base64Image = Image64.convertImageToBase64(file.getAbsolutePath());

                // Agregar encabezado MIME si es necesario
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null || !mimeType.startsWith("image/")) {
                    mimeType = "image/jpeg";
                }

                base64Image = "data:" + mimeType + ";base64," + base64Image;

                // Mostrar imagen en ImageView
                Image64.showImage64(imageView, base64Image);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }
    }

    // === Manejo de documentos ===
    public void displayDocuments() {
        Platform.runLater(() -> {
            listDocumentVbox.getChildren().clear();
            for (Document document : documentList) {
                try {
                    FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/agdv/util/card/DocumentCard.fxml"));
                    Node node = loader.load();
                    DocumentCard docCard = loader.getController();
                    docCard.setDocumentCard(document.getId(), document.getName(), document.getDate(), document.getFile());
                    docCard.setOnDocumentActionListener(this::deleteDocument);
                    listDocumentVbox.getChildren().add(node);
                } catch (IOException e) {
                    AlertScreen.showError("Error", "No se pudo cargar un documento.");
                }
            }
        });
    }

    // === Servicios REST - Aseguradoras ===
    public void getAllInsurance() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long startTime = System.currentTimeMillis();
        final AtomicBoolean requestInProgress = new AtomicBoolean(false);

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

                if (requestInProgress.get()) return;
                requestInProgress.set(true);

                Call<List<Insurance>> call = ApiClient.getClient().create(InsuranceService.class).getAllInsurance();
                call.enqueue(new Callback<List<Insurance>>() {
                    @Override
                    public void onResponse(Call<List<Insurance>> call, Response<List<Insurance>> response) {
                        requestInProgress.set(false);

                        if (response.isSuccessful() && response.body() != null) {
                            scheduler.shutdown();
                            Platform.runLater(() -> {
                                listInsurance = response.body();
                                insuranceComboBox.getItems().clear();
                                insuranceComboBox.getItems().addAll(listInsurance);
                                setupInsuranceComboBox();

                                if (edit && car != null && car.getInsurance() != null) {
                                    Insurance selected = listInsurance.stream()
                                            .filter(i -> i.getId() == car.getInsurance().getId())
                                            .findFirst()
                                            .orElse(null);
                                    insuranceComboBox.setValue(selected);
                                }
                            });
                        }
                        // Si no fue exitosa, simplemente reintenta
                    }

                    @Override
                    public void onFailure(Call<List<Insurance>> call, Throwable t) {
                        requestInProgress.set(false);
                        // No mostrar alerta aquí, se reintentará automáticamente
                    }
                });
            }
        };

        scheduler.scheduleAtFixedRate(retryTask, 0, 2, TimeUnit.SECONDS);
    }


    private void setupInsuranceComboBox() {
        insuranceComboBox.setConverter(new StringConverter<Insurance>() {
            @Override
            public String toString(Insurance insurance) {
                return (insurance != null) ? insurance.getName() : "";
            }

            @Override
            public Insurance fromString(String name) {
                return listInsurance.stream()
                        .filter(i -> i.getName().equals(name))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    // === Acciones de guardar / editar vehículo ===
    @FXML
    public void saveValueCar() {
        try {
            if (edit) {
                editCar();
            } else {
                addCar();
            }
        } catch (IOException e) {
            System.out.println("Error al procesar acción: " + e.getMessage());
        }
    }

    public void addCar() throws IOException {
        if (car == null) return;
        if (serialNumberField.getText().length() < 17) return;;

        CarService service = ApiClient.getClient().create(CarService.class);
        Call<Car> call = service.addCar(car);

        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        AlertScreen.showSuccess("Se guardo corectamente","✅ Vehículo agregado exitosamente.");
                        switchScreen(Screen.MENU);
                    } else {
                        AlertScreen.showError("Error", "No se pudo agregar el vehículo.");
                    }
                });
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Platform.runLater(() ->
                        AlertScreen.showError("Error", "No se pudo conectar al servidor.")
                );
            }
        });
    }

    public void editCar() throws IOException {
        if (car == null) return;

        CarService service = ApiClient.getClient().create(CarService.class);
        Call<Car> call = service.updateCar(car);

        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        System.out.println("✅ Vehículo actualizado exitosamente.");
                        switchScreen(Screen.MENU);
                    } else {
                        AlertScreen.showError("Error", "No se pudo actualizar el vehículo.");
                    }
                });
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Platform.runLater(() ->
                        AlertScreen.showError("Error", "No se pudo conectar al servidor.")
                );
            }
        });
    }
    @FXML
    private  void getCarFromForm() throws IOException {
        // Limpiar estilos y mensajes
        resetValidationStyles();
        // Validaciones
        boolean validated = true;
        String carId = serialNumberField.getText().trim();
        if (carId.isEmpty() || carId.matches(".*[^a-zA-Z0-9ñÑ\\s].*") || carId.length() < 17) {
            serialNumberField.getStyleClass().add("invalid-field");
            serialNumberLabel.setText("Número de serie inválido");
            validated = false;
        } else {
            serialNumberField.getStyleClass().add("valid-field");
        }

        String brand = brandField.getText().trim();
        if (brand.isEmpty() || brand.matches(".*[^a-zA-Z\\s].*")) {
            brandField.getStyleClass().add("invalid-field");
            brandLabel.setText("Marca inválida");
            validated = false;
        } else {
            brandField.getStyleClass().add("valid-field");
        }

        String model = modelField.getText().trim();
        if (model.isEmpty() || model.matches(".*[^a-zA-Z0-9\\s].*")) {
            modelField.getStyleClass().add("invalid-field");
            modelLabel.setText("Modelo inválido");
            validated = false;
        } else {
            modelField.getStyleClass().add("valid-field");
        }

        String color = colorField.getText().trim();
        if (color.isEmpty() || color.matches(".*[^a-zA-Z\\s].*")) {
            colorField.getStyleClass().add("invalid-field");
            colorLabel.setText("Color inválido");
            validated = false;
        } else {
            colorField.getStyleClass().add("valid-field");
        }

        String observations = notesTextArea.getText().trim();
        if (observations.matches(".*[^a-zA-Z0-9\\s.,].*")) {
            notesTextArea.getStyleClass().add("invalid-field");
            notesLabel.setText("Notas inválidas");
            validated = false;
        } else {
            notesTextArea.getStyleClass().add("valid-field");
        }

        Insurance insurance = insuranceComboBox.getSelectionModel().getSelectedItem();
        if (insurance == null) {
            insuranceComboBox.getStyleClass().add("invalid-field");
            insuranceLabel.setText("Seleccione un seguro");
            validated = false;
        }

        String yearStr = yearComboBox.getSelectionModel().getSelectedItem();
        int year = 0;
        if (yearStr == null || yearStr.isEmpty()) {
            yearComboBox.getStyleClass().add("invalid-field");
            yearLabel.setText("Año inválido");
            validated = false;
        } else {
            year = Integer.parseInt(yearStr);
        }


        car.setId(carId);
        car.setBrand(brand);
        car.setModel(model);
        car.setYear(year);
        car.setColor(color);
        car.setObservations(observations);
        car.setInsurance(insurance);
        car.setAsset(1);

        car.setImage(base64Image);


        saveButton.setDisable(!validated);
    }

    private void resetValidationStyles() {
        serialNumberField.getStyleClass().removeAll("invalid-field", "valid-field");
        brandField.getStyleClass().removeAll("invalid-field", "valid-field");
        modelField.getStyleClass().removeAll("invalid-field", "valid-field");
        colorField.getStyleClass().removeAll("invalid-field", "valid-field");
        notesTextArea.getStyleClass().removeAll("invalid-field", "valid-field");
        yearComboBox.getStyleClass().removeAll("invalid-field", "valid-field");
        insuranceComboBox.getStyleClass().remove("invalid-field");

        serialNumberLabel.setText("");
        brandLabel.setText("");
        modelLabel.setText("");
        yearLabel.setText("");
        colorLabel.setText("");
        notesLabel.setText("");
        insuranceLabel.setText("");
    }

    public void getAllDocument() {
        String carId = serialNumberField.getText().trim();
        if (carId.isEmpty()) {
            Platform.runLater(() ->
                    AlertScreen.showError("Error", "Ingresa el número de serie del vehículo.")
            );
            return;
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long startTime = System.currentTimeMillis();
        final AtomicBoolean requestInProgress = new AtomicBoolean(false);

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

                // Evitar múltiples peticiones si una está en curso
                if (requestInProgress.get()) return;
                requestInProgress.set(true);

                DocumentService service = ApiClient.getClient().create(DocumentService.class);
                Call<List<Document>> call = service.getAllDocument(carId);

                call.enqueue(new Callback<List<Document>>() {
                    @Override
                    public void onResponse(Call<List<Document>> call, Response<List<Document>> response) {
                        requestInProgress.set(false);

                        if (response.isSuccessful() && response.body() != null) {
                            scheduler.shutdown();
                            Platform.runLater(() -> {
                                documentList.clear();
                                documentList.addAll(response.body());
                                for (Document document : response.body()){
                                    System.out.println(document.getCarId()+" name: "+document.getName());
                                }
                                displayDocuments();
                            });
                        }
                        // Si la respuesta no es exitosa, no hacemos nada y se reintenta
                    }

                    @Override
                    public void onFailure(Call<List<Document>> call, Throwable t) {
                        requestInProgress.set(false);
                        // Se reintentará automáticamente
                    }
                });
            }
        };

        scheduler.scheduleAtFixedRate(retryTask, 0, 2, TimeUnit.SECONDS);
    }


    public void addDocument() {
        String file = "";
        if (selectedFile == null) {
            selectedFile = OpenFileExplorer.loadFile(null);
            if (selectedFile == null) return;
        }
        String base64File;
        try {
            if (isImage(selectedFile)) {
                base64File = Image64.convertImageToBase64(selectedFile.getAbsolutePath());
                file += ".png";
            } else if (isPDF(selectedFile)) {
                base64File = PDF64.convertPDFToBase64(selectedFile.getAbsolutePath());
                file += ".pdf";
            } else {
                System.err.println("Tipo de archivo no soportado.");
                return;
            }
            String name = AlertScreen.askFileName()+file;

            if (name == null || name.isEmpty()) return;

            String carId = serialNumberField.getText().trim();
            if (carId.isEmpty()) {
                AlertScreen.showError("Error", "El número de serie del auto es requerido.");
                return;
            }

            Document document = new Document();
            document.setName(name);
            document.setDate(LocalDate.now().toString());
            document.setAsset(1);
            document.setFile(base64File);
            DocumentService service = ApiClient.getClient().create(DocumentService.class);
            Call<Document> call = service.addDocument(carId, document);
            call.enqueue(new Callback<Document>() {
                @Override
                public void onResponse(Call<Document> call, Response<Document> response) {
                    Platform.runLater(() -> {
                        if (response.isSuccessful() && response.body() != null) {
                            documentList.add(response.body());
                            displayDocuments();
                            System.out.println("✅ Documento agregado correctamente.");
                        } else {
                            AlertScreen.showError("Error", "No se pudo agregar el documento.");
                        }
                    });
                }

                @Override
                public void onFailure(Call<Document> call, Throwable t) {
                    Platform.runLater(() ->
                            AlertScreen.showError("Error", "No se pudo conectar al servidor.")
                    );
                }
            });

        } catch (IOException e) {
            System.out.println("Error al procesar documento: " + e.getMessage());
        }
        selectedFile=null;
    }


    public void deleteDocument(String documentId) {
        boolean confirmed = AlertScreen.showConfirmation("Eliminar documento", "¿Quieres borrar el Documento?", "");
        if (!confirmed) return;

        DocumentService service = ApiClient.getClient().create(DocumentService.class);
        Call<String> call = service.deleteDocument(documentId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful() && "ok".equals(response.body())) {
                        documentList.removeIf(doc -> documentId.equals(doc.getId()));
                        displayDocuments();
                    } else {
                        AlertScreen.showError("Error", "No se pudo eliminar el documento.");
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Platform.runLater(() ->
                        AlertScreen.showError("Error", "No se pudo conectar al servidor.")
                );
            }
        });
    }

    // === Validación de archivos ===
    private boolean isImage(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    private boolean isPDF(File file) {
        return file.getName().toLowerCase().endsWith(".pdf");
    }
}