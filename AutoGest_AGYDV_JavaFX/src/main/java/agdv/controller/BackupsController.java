package agdv.controller;

// Importaciones necesarias para el funcionamiento del controlador
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import agdv.MainApp;
import agdv.model.Backup;
import agdv.model.Car;
import agdv.model.Document;
import agdv.util.services.*;
import agdv.util.Screen;
import agdv.util.card.BackupsCard;
import agdv.util.dialog.AlertScreen;
import agdv.util.loadFile.Image64;
import agdv.util.loadFile.PDF64;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;

/**
 * Controlador para la pantalla de gestión de respaldos.
 * Permite cargar, mostrar y eliminar respaldos desde el servidor.
 */
public class BackupsController {
    // Componentes FXML vinculados a la interfaz gráfica
    @FXML private VBox backupListContainer; // Contenedor donde se muestran los respaldos
    @FXML private Button backButton; // Botón para regresar al menú principal

    // Atributos internos
    private final List<Backup> backupList = new ArrayList<>(); // Lista de respaldos obtenidos del servidor
    private static BackupsController instance; // Instancia única del controlador (patrón Singleton)
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); // Ejecutor programado para tareas asíncronas

    /**
     * Constructor: Asigna la instancia actual como la única existente.
     */
    public BackupsController() {
        instance = this;
    }

    /**
     * Devuelve la instancia única del controlador.
     * @return Instancia de BackupsController
     */
    public static BackupsController getInstance() {
        return instance;
    }

    /**
     * Método llamado automáticamente por JavaFX al cargar la vista.
     * Inicia la carga de los respaldos desde el servidor.
     */
    public void initialize() {
        loadBackups(); // Cargar respaldos del servidor
    }

    /**
     * Muestra los respaldos cargados como tarjetas individuales en la interfaz.
     */
    private void displayBackups() {
        Platform.runLater(() -> {
            backupListContainer.getChildren().clear(); // Limpiar contenido previo
            backupList.forEach(backup -> addBackupCard(backup)); // Agregar cada respaldo como una tarjeta
        });
    }

    /**
     * Crea una tarjeta visual para un respaldo específico y la añade a la interfaz.
     * @param backup Respaldo a mostrar
     */
    private void addBackupCard(Backup backup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agdv/util/card/BackupsCard.fxml"));
            Node node = loader.load();
            BackupsCard cardController = loader.getController();
            cardController.setBackup(backup); // Configurar datos del respaldo en la tarjeta
            cardController.setController(this); // Pasar referencia del controlador
            backupListContainer.getChildren().add(node); // Añadir la tarjeta al contenedor
        } catch (IOException e) {
            AlertScreen.showError("Error", "No se pudo cargar la tarjeta: " + backup.getName());
        }
    }

    /**
     * Navega a otra pantalla definida en el enum Screen.
     * @param screen Vista destino
     */
    private void switchScreen(Screen screen) {
        Platform.runLater(() -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            MainApp.changeView(screen.getPath()); // Cambiar la vista
            currentStage.setTitle(screen.getTitle()); // Actualizar título de la ventana
        });
    }

    /**
     * Obtiene la lista de respaldos desde el servidor y actualiza la UI.
     */
    private void loadBackups() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long startTime = System.currentTimeMillis(); // Tiempo de inicio del intento
        Runnable retryTask = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now - startTime >= 10000) { // Si pasaron más de 10 segundos sin éxito
                    scheduler.shutdown();
                    Platform.runLater(() ->
                            AlertScreen.showError("Error de conexión", "No se pudo conectar al servidor después de varios intentos.")
                    );
                    return;
                }
                BackupService service = ApiClient.getClient().create(BackupService.class); // Crear servicio Retrofit
                Call<List<Backup>> call = service.getAllBackups(); // Llamada a la API
                call.enqueue(new Callback<List<Backup>>() {
                    @Override
                    public void onResponse(Call<List<Backup>> call, Response<List<Backup>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            scheduler.shutdown(); // Detener reintentos
                            Platform.runLater(() -> updateBackupList(response.body())); // Actualizar lista local
                        } else {
                            // Si la respuesta falla, volverá a intentar automáticamente
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Backup>> call, Throwable t) {
                        // No hacemos nada aquí: se reintentará por el scheduler
                    }
                });
            }
        };
        scheduler.scheduleAtFixedRate(retryTask, 0, 2, TimeUnit.SECONDS); // Reintentar cada 2 segundos
    }

    /**
     * Actualiza la lista local de respaldos y muestra las tarjetas en orden inverso.
     * @param result Lista de respaldos recibida del servidor
     */
    private void updateBackupList(List<Backup> result) {
        Platform.runLater(() -> {
            backupList.clear(); // Limpiar la lista actual
            backupList.addAll(result); // Añadir nuevos resultados
            Collections.reverse(backupList); // Mostrar los más recientes primero
            displayBackups(); // Refrescar la interfaz
        });
    }

    /**
     * Crea un archivo ZIP con todos los documentos asociados a cada vehículo.
     */
    public void createBackupZIP() {
        CarService carService = ApiClient.getClient().create(CarService.class); // Servicio para obtener vehículos
        carService.getAllCar().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    new Thread(() -> processCarsAndCreateZip(response.body())).start(); // Procesar en hilo separado
                } else {
                    AlertScreen.showError("Sin datos", "No hay vehículos disponibles.");
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                AlertScreen.showError("Error", "Fallo al obtener vehículos.");
            }
        });
    }

    /**
     * Procesa una lista de vehículos y crea un archivo ZIP con sus documentos.
     * @param cars Lista de vehículos
     */
    private void processCarsAndCreateZip(List<Car> cars) {
        try {
            Path tempDir = Files.createTempDirectory("backup-ZIP-" + LocalDate.now()); // Directorio temporal
            int totalDocs = 0;
            int savedDocs = 0;
            for (Car car : cars) {
                List<Document> documents = loadDocumentsSync(car.getId()); // Cargar documentos sincrónicamente
                if (documents == null || documents.isEmpty()) continue;
                totalDocs += documents.size();
                Path carFolder = tempDir.resolve(car.getId());
                Files.createDirectories(carFolder); // Crear carpeta para este vehículo
                savedDocs += saveDocumentsToFolder(documents, carFolder); // Guardar documentos localmente
            }

            if (savedDocs == 0) {
                Platform.runLater(() -> AlertScreen.showError("Sin archivos", "No se guardó ningún documento para comprimir."));
                return;
            }

            List<Path> files = listFiles(tempDir); // Obtener todos los archivos del directorio
            if (files.isEmpty()) {
                Platform.runLater(() -> AlertScreen.showError("Sin archivos", "No hay documentos para comprimir."));
                return;
            }

            Path zipFile = createZipFromFiles(files, tempDir); // Crear archivo ZIP
            if (zipFile != null) {
                addBackup(zipFile.toFile()); // Subir el ZIP al servidor
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> AlertScreen.showError("Error", "Error al crear el respaldo: " + e.getMessage()));
        }
    }

    /**
     * Obtiene una lista de archivos dentro de un directorio.
     * @param dir Directorio a explorar
     * @return Lista de rutas absolutas de los archivos encontrados
     * @throws IOException En caso de error al leer el directorio
     */
    private List<Path> listFiles(Path dir) throws IOException {
        return Files.walk(dir)
                .filter(Files::isRegularFile)
                .toList();
    }

    /**
     * Crea un archivo ZIP a partir de una lista de archivos.
     * @param files Archivos a incluir en el ZIP
     * @param rootDir Directorio raíz del que se generará el ZIP
     * @return Ruta del archivo ZIP creado
     * @throws IOException En caso de error durante la escritura
     */
    private Path createZipFromFiles(List<Path> files, Path rootDir) throws IOException {
        Path zipFile = Files.createTempFile("respaldo-", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            for (Path file : files) {
                ZipEntry entry = new ZipEntry(rootDir.relativize(file).toString());
                zos.putNextEntry(entry);
                Files.copy(file, zos);
                zos.closeEntry();
            }
        }
        return zipFile;
    }

    /**
     * Guarda los documentos descargados en el sistema de archivos.
     * @param documents Documentos a guardar
     * @param folder Carpeta destino
     * @return Número de documentos guardados exitosamente
     * @throws IOException En caso de error al guardar
     */
    private int saveDocumentsToFolder(List<Document> documents, Path folder) throws IOException {
        int savedCount = 0;
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
        for (Document doc : documents) {
            String name = doc.getName();
            String base64 = doc.getFile();
            if (name == null || base64 == null) continue;

            try {
                String baseName;
                String extension;
                int dotIndex = name.lastIndexOf('.');
                if (dotIndex > 0) {
                    baseName = name.substring(0, dotIndex);
                    extension = name.substring(dotIndex);
                } else {
                    baseName = name;
                    extension = "";
                }

                String uniqueFileName = getUniqueFileName(folder, baseName, extension); // Evitar duplicados
                if (extension.equalsIgnoreCase(".pdf")) {
                    PDF64.saveBase64ToPDF(base64, folder.toString(), uniqueFileName);
                } else {
                    Image img = Image64.convertBase64ToImage(base64);
                    if (img != null) {
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                        File out = new File(folder.toFile(), uniqueFileName);
                        ImageIO.write(bufferedImage, "png", out);
                    } else {
                        System.err.println("Error al convertir imagen base64 para documento: " + name);
                        continue;
                    }
                }
                savedCount++; // Incrementar contador solo si se guardó correctamente
            } catch (Exception e) {
                System.err.println("Error guardando documento " + name + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return savedCount;
    }

    /**
     * Genera un nombre único para un archivo evitando solapamientos.
     * @param folder Carpeta destino
     * @param baseName Nombre base del archivo
     * @param extension Extensión del archivo
     * @return Nombre único para el archivo
     */
    public String getUniqueFileName(Path folder, String baseName, String extension) {
        int count = 0;
        String fileName;
        Path filePath;
        do {
            fileName = (count == 0) ? baseName + extension : baseName + "_" + count + extension;
            filePath = folder.resolve(fileName);
            count++;
        } while (Files.exists(filePath));
        return fileName;
    }

    /**
     * Carga documentos sincrónicamente de un vehículo específico.
     * @param carId ID del vehículo
     * @return Lista de documentos del vehículo
     */
    private List<Document> loadDocumentsSync(String carId) {
        try {
            DocumentService service = ApiClient.getClient().create(DocumentService.class);
            Call<List<Document>> call = service.getAllDocument(carId);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Response<List<Document>>> future = executor.submit(call::execute);

            try {
                Response<List<Document>> response = future.get(5, TimeUnit.SECONDS);
                return Optional.ofNullable(response.body()).orElse(Collections.emptyList());
            } catch (TimeoutException te) {
                System.err.println("Timeout al obtener documentos para auto " + carId);
                call.cancel();
                return Collections.emptyList();
            } finally {
                executor.shutdown();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar documentos sincrónicamente para auto " + carId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Envía el archivo ZIP al servidor para almacenarlo como un nuevo respaldo.
     * @param zipFile Archivo ZIP a enviar
     */
    public void addBackup(File zipFile) {
        try {
            BackupService service = ApiClient.getClient().create(BackupService.class);
            RequestBody requestFile = RequestBody.create(MediaType.get("application/zip"), zipFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", zipFile.getName(), requestFile);
            Call<String> call = service.addBackup(body);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        AlertScreen.showInfo("Éxito", "Respaldo subido correctamente.");
                    } else {
                        AlertScreen.showError("Error", "La API no aceptó el respaldo. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    loadBackups(); // Volver a cargar los respaldos actuales
                }
            });
        } catch (Exception e) {
            AlertScreen.showError("Error", "Fallo al enviar el archivo.");
        }
    }

    /**
     * Maneja el evento del botón "Volver" para regresar al menú principal.
     */
    @FXML
    private void goBack() {
        switchScreen(Screen.MENU);
    }
}