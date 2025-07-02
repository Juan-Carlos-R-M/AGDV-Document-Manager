package agdv;

import agdv.controller.BackupsController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Clase principal de la aplicación que gestiona:
 * - La navegación entre vistas.
 * - La configuración inicial de la ventana.
 * - Comportamiento global como atajos de teclado, pantalla completa, arrastre de ventana.
 * - Diálogos modales personalizados.
 * - Respaldo automático al cerrar la aplicación.
 */
public class MainApp {

    /** Escenario principal de la aplicación */
    public static Stage primaryStage;

    /**
     * Establece el escenario principal de la aplicación.
     *
     * @param stage El escenario principal a asignar.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Cambia dinámicamente la vista central de la interfaz cargando un nuevo archivo FXML.
     *
     * @param fxmlPath Ruta relativa del archivo FXML a cargar.
     * @return El controlador asociado al nuevo FXML cargado o null si ocurre un error.
     */
    public static Object changeView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent newContent = loader.load();
            Object controller = loader.getController();

            BorderPane currentRoot = (BorderPane) primaryStage.getScene().getRoot();
            currentRoot.setCenter(newContent);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inicia la aplicación con la pantalla inicial especificada.
     *
     * @param fxmlPath Ruta relativa del archivo FXML inicial.
     */
    public static void startApplication(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(MainApp.class.getResource(fxmlPath)));
            Scene scene = new Scene(root);
            Image image = new Image("/agdv/images/logoAutosGutierrezNegro.png");
            primaryStage.getIcons().add(image);
            primaryStage.setScene(scene);
            primaryStage.setTitle("AutoGestAGYDV");
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(900);
            primaryStage.setResizable(false);

            // Configurar funcionalidad de arrastre manual para ventanas sin bordes
            makeDraggable(root, primaryStage);

            // Configurar atajos de teclado globales
            configureKeyboardShortcuts(scene);

            primaryStage.setFullScreen(true);

            // Mostrar mensaje cuando se salga de pantalla completa
            primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    showFullScreenExitMessage("Presiona Alt + Enter para volver a pantalla completa", root);
                }
            });

            // Realizar respaldo antes de cerrar la aplicación
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Aplicación cerrada. Creando respaldo...");
                new BackupsController().createBackupZIP();
            });

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configura los atajos de teclado globales para la aplicación.
     * Actualmente permite alternar entre modo normal y pantalla completa con Alt + Enter.
     *
     * @param scene La escena donde se aplicará esta configuración.
     */
    private static void configureKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });
    }

    /**
     * Hace que cualquier nodo raíz sea arrastrable en ventanas sin decoración.
     * Desactiva el arrastre cuando está en modo pantalla completa.
     *
     * @param root  Nodo raíz de la interfaz.
     * @param stage Escenario de la aplicación.
     */
    public static void makeDraggable(Parent root, Stage stage) {
        final double[] x = new double[1];
        final double[] y = new double[1];

        stage.fullScreenProperty().addListener((obs, wasFullScreen, isFullScreen) -> {
            if (isFullScreen) {
                root.setOnMousePressed(null);
                root.setOnMouseDragged(null);
            } else {
                root.setOnMousePressed(event -> {
                    x[0] = stage.getX() - event.getScreenX();
                    y[0] = stage.getY() - event.getScreenY();
                });

                root.setOnMouseDragged(event -> {
                    stage.setX(event.getScreenX() + x[0]);
                    stage.setY(event.getScreenY() + y[0]);
                });
            }
        });
    }

    /**
     * Muestra un mensaje temporal cuando la aplicación sale de modo pantalla completa.
     *
     * @param text Mensaje a mostrar.
     * @param root Nodo raíz donde se agregará el mensaje.
     */
    private static void showFullScreenExitMessage(String text, Node root) {
        Label messageLabel = new Label(text);
        messageLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px; " +
                "-fx-background-radius: 5px;");
        messageLabel.setOpacity(0.9);

        if (root instanceof Pane container) {
            messageLabel.setLayoutX(50);
            messageLabel.setLayoutY(50);
            container.getChildren().add(messageLabel);

            // Animación de desvanecimiento después de 3 segundos
            PauseTransition fadeOut = new PauseTransition(Duration.seconds(3));
            fadeOut.setOnFinished(e -> container.getChildren().remove(messageLabel));
            fadeOut.play();
        }
    }

    /**
     * Actualiza el mensaje mostrado al salir de pantalla completa en cada cambio de vista.
     *
     * @param root Nodo raíz actual donde mostrar el mensaje.
     */
    public static void updateFullScreenMessage(Parent root) {
        primaryStage.fullScreenProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue && root instanceof Pane container) {
                showFullScreenExitMessage("Presiona Alt + Enter para volver a pantalla completa", container);
            }
        });
    }

    /**
     * Muestra una ventana modal personalizada sin barra de título ni botones de sistema.
     *
     * @param fxmlPath Ruta relativa del archivo FXML para la ventana modal.
     * @param title    Título de la ventana (no visible por estilo UNDECORATED).
     */
    public static void showModalWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED); // Ventana sin bordes
            stage.initModality(Modality.WINDOW_MODAL); // Modal respecto a la ventana principal
            stage.initOwner(MainApp.primaryStage); // Asociada al escenario principal
            stage.setTitle("AutoGestAGYDV");
            Image image = new Image("/agdv/images/car_icon.png");
            stage.getIcons().add(image);

            Scene scene = new Scene(root);

            // Bloquear manualmente la tecla ESCAPE (opcional pero recomendado)
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("ESCAPE")) {
                    event.consume(); // Evita cierre accidental
                }
            });

            stage.setScene(scene);
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Obliga a permanecer en pantalla completa
            stage.setFullScreen(true);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}