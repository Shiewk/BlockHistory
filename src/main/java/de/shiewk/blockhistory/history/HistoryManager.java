package de.shiewk.blockhistory.history;

import de.shiewk.blockhistory.BlockHistoryPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileSystemException;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HistoryManager implements AutoCloseable {

    private final ComponentLogger logger;
    private final ObjectArrayList<HistoryElement> cache = new ObjectArrayList<>();
    private final File latestLog;
    private final File saveDir;
    private OutputStream logOut;

    public HistoryManager(@NotNull ComponentLogger logger, @NotNull File saveDir) throws IOException {
        this.saveDir = saveDir;
        this.logger = logger;
        logger.info("Creating new history manager (saves in {})", saveDir.getPath());
        if (!saveDir.isDirectory()){
            throw new IllegalArgumentException("saveDir file must be a directory");
        }
        latestLog = new File(saveDir.getPath() + "/latest.blh");
        if (latestLog.isFile()){
            archiveLog(latestLog);
        }
        latestLog.createNewFile();
    }

    public void save() throws IOException {
        logger.info("Saving history");
        final ObjectArrayList<HistoryElement> cached = this.cache.clone();
        this.cache.clear();
        ensureLogOpen();
        for (HistoryElement element : cached) {
            element.saveTo(logOut);
        }
        logger.info("Saved history");
    }

    private void archiveLog(File logFile) throws IOException {
        if (logFile.length() > 0){
            logger.info("Archiving log file {}", logFile.getPath());
            File archiveFile = null;
            int fileInd = 0;
            final Calendar calendar = Calendar.getInstance();
            while (archiveFile == null || archiveFile.isFile()){
                final String path = saveDir.getPath() + "/archived-%s-%s-%s-%s.blh.gz".formatted(
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), fileInd
                );
                logger.info(path);
                archiveFile = new File(path);
                logger.info(String.valueOf(archiveFile.exists()));
            }
            if (archiveFile.createNewFile()){
                try (FileOutputStream fos = new FileOutputStream(archiveFile)){
                    try (GZIPOutputStream gzout = new GZIPOutputStream(fos)){
                        try (FileInputStream fin = new FileInputStream(logFile)){
                            fin.transferTo(gzout);
                        }
                    }
                }
            } else {
                throw new FileSystemException("Archive file could not be created");
            }
            logFile.delete();
            logger.info("Archived log file {} to {}", logFile.getPath(), archiveFile.getPath());
        } else {
            logFile.delete();
            logger.info("Deleted log file {} (was empty)", logFile.getPath());
        }
    }

    public void add(HistoryElement element){
        cache.add(element);
    }

    public int search(Predicate<HistoryElement> predicate, Consumer<HistoryElement> consumer){
        final AtomicInteger count = new AtomicInteger();
        forEach(element -> {
            try {
                if (predicate.test(element)){
                    consumer.accept(element);
                    count.getAndIncrement();
                }
            } catch (Exception e){
                BlockHistoryPlugin.logThrowable("Error while searching for history elements", e);
            }
        });
        return count.get();
    }

    private void ensureLogOpen() throws IOException {
        if (logOut == null) {
            if (latestLog.isFile()){
                archiveLog(latestLog);
            }
            latestLog.createNewFile();
            logOut = new FileOutputStream(latestLog);
        }
    }

    public CompletableFuture<Integer> searchAsync(Predicate<HistoryElement> predicate, Consumer<HistoryElement> consumer){
        return CompletableFuture.supplyAsync(() -> search(predicate, consumer));
    }

    public void forEach(Consumer<HistoryElement> consumer){
        Consumer<HistoryElement> wrapper = elem -> {
            try {
                consumer.accept(elem);
            } catch (Exception e){
                BlockHistoryPlugin.logThrowable("Error while searching history:", e);
            }
        };
        final File[] files = saveDir.listFiles(file -> file.isFile() && file.getName().endsWith(".blh.gz"));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fin = new FileInputStream(file)) {
                    try (GZIPInputStream gzin = new GZIPInputStream(fin)){
                        while (gzin.available() > 0){
                            final HistoryElement element = HistoryElement.load(gzin);
                            wrapper.accept(element);
                        }
                    }
                } catch (Exception e){
                    BlockHistoryPlugin.logThrowable("Save file " + file.getName() + "corrupted:", e);
                }
            }
        }
        try (FileInputStream fin = new FileInputStream(latestLog)) {
            while (fin.available() > 0){
                final HistoryElement element = HistoryElement.load(fin);
                wrapper.accept(element);
            }
        } catch (Exception e){
            BlockHistoryPlugin.logThrowable("Latest log file corrupted:", e);
        }
        cache.clone().forEach(wrapper);
    }

    @Override
    public void close() throws IOException {
        if (logOut != null) {
            logOut.close();
        }
    }
}
