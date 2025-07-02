package agdv.util.selfBackup;

import agdv.controller.BackupsController;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * Clase que gestiona la programación de respaldos automáticos semanales.
 * Utiliza un {@link ScheduledExecutorService} para ejecutar tareas periódicas.
 *
 * El siguiente respaldo se almacena en un archivo de texto que guarda la fecha del próximo evento.
 */
public class BackupScheduler {

    /**
     * Servicio de programación de tareas.
     */
    private final ScheduledExecutorService scheduler;

    /**
     * Objeto que representa la tarea programada actual (si existe).
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * Ruta del archivo donde se almacena la fecha del próximo respaldo.
     */
    private final String backupFile;

    /**
     * Formateador de fechas utilizado para guardar y leer la fecha del próximo respaldo.
     */
    private final DateTimeFormatter dateFormat;

    /**
     * Controlador encargado de crear el respaldo ZIP.
     */
    private final BackupsController backupsController;

    /**
     * Constructor de la clase BackupScheduler.
     *
     * @param backupFile Ruta del archivo que almacena la próxima fecha de respaldo.
     * @param backupsController Controlador usado para realizar el respaldo.
     */
    public BackupScheduler(String backupFile, BackupsController backupsController) {
        this.backupFile = backupFile;
        this.dateFormat = DateTimeFormatter.ISO_DATE;
        this.backupsController = backupsController;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); // Hilo demonio: no bloquea el cierre de la aplicación
            t.setName("BackupScheduler-Thread");
            return t;
        });
    }

    /**
     * Inicia el planificador de respaldos.
     * Programa la próxima ejecución según la fecha guardada.
     */
    public void start() {
        System.out.println("Iniciando BackupScheduler...");
        scheduleNextBackup();
        System.out.println("Ruta actual de ejecución: " + System.getProperty("user.dir"));
    }

    /**
     * Obtiene la fecha del próximo respaldo desde el archivo almacenado.
     * Si no existe o hay error, devuelve una fecha predeterminada (1 semana después).
     *
     * @return Fecha del próximo respaldo como LocalDate.
     */
    private LocalDate getNextBackupDate() {
        File file = new File(backupFile);
        if (!file.exists()) {
            System.out.println("Archivo de respaldo no existe. Se creará uno nuevo.");
            LocalDate fallbackDate = LocalDate.now().plusWeeks(1);
            saveNextBackupDate(fallbackDate);
            return fallbackDate;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            LocalDate date = LocalDate.parse(line, dateFormat);
            return date;
        } catch (Exception e) {
            System.err.println("Error al leer la fecha del próximo respaldo: " + e.getMessage());
            LocalDate fallbackDate = LocalDate.now().plusWeeks(1);
            saveNextBackupDate(fallbackDate);
            return fallbackDate;
        }
    }

    /**
     * Guarda la fecha del próximo respaldo en un archivo de texto.
     *
     * @param date Fecha a guardar.
     */
    private void saveNextBackupDate(LocalDate date) {
        try (FileWriter writer = new FileWriter(backupFile)) {
            writer.write(date.format(dateFormat));
            System.out.println("Fecha del próximo respaldo guardada: " + date);
        } catch (IOException e) {
            System.err.println("Error al guardar la fecha del próximo respaldo: " + e.getMessage());
        }
    }

    /**
     * Programa la próxima ejecución del respaldo automático.
     */
    private void scheduleNextBackup() {
        LocalDate nextBackupDate = getNextBackupDate();
        LocalDate today = LocalDate.now();

        if (!nextBackupDate.isAfter(today)) {
            // Ejecutar inmediatamente si ya pasó la fecha
            scheduledFuture = scheduler.schedule(this::executeBackupTask, 0, TimeUnit.MILLISECONDS);
        } else {
            // Programar con retraso hasta la fecha indicada
            long delayMillis = Duration.between(LocalDateTime.now(), nextBackupDate.atStartOfDay()).toMillis();
            System.out.println("📅 Respaldo programado para: " + nextBackupDate + " (en " + delayMillis / 1000 + " segundos)");
            scheduledFuture = scheduler.schedule(this::executeBackupTask, delayMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Tarea principal que ejecuta el respaldo y programa el siguiente.
     */
    private void executeBackupTask() {
        System.out.println("✅ Ejecutando respaldo automático semanal...");
        try {
            backupsController.createBackupZIP();
            System.out.println("Respaldo completado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al crear respaldo: " + e.getMessage());
            e.printStackTrace();
        }

        // Programar el siguiente respaldo dentro de 1 semana
        LocalDate nextDate = LocalDate.now().plusWeeks(1);
        saveNextBackupDate(nextDate);
        scheduleNextBackup();
    }

    /**
     * Detiene el planificador de forma segura.
     *
     * @return true si el planificador se detuvo correctamente, false en caso contrario.
     */
    public boolean shutdown() {
        System.out.println("Deteniendo BackupScheduler...");
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("No terminó a tiempo, forzando shutdownNow()...");
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("No se pudo detener el scheduler completamente.");
                    return false;
                }
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
        System.out.println("BackupScheduler detenido.");
        return true;
    }
}