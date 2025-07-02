package agdv.util;

/**
 * Represents the different views/screens available in the application.
 */
public enum Screen {

    MENU("MainMenu", "Menú"),

    CAR_LIST("CarList", "Consulta de Vehículos"),
    ADD_CAR("CarForm", "Agregar Vehículo"),
    DOCUMENT("Document", "Documentación"),
    BACKUPS("Backups", "Respaldos"),
    CONFIGURE_APPLICATION("ConfigureApplication","Configurar aplicación");

    private final String fxmlFile;
    private final String title;

    Screen(String fxmlFile, String title) {
        this.fxmlFile = fxmlFile;
        this.title = title;
    }

    /**
     * Returns the FXML file path for this view.
     */
    public String getPath() {
        return "/agdv/fxml/" + fxmlFile + ".fxml";
    }

    /**
     * Returns the title to be shown in the window.
     */
    public String getTitle() {
        return title;
    }
}