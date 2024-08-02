package de.shiewk.blockhistory.history;

import de.shiewk.blockhistory.BlockHistoryPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HistoryManager {

    private final ObjectArrayList<HistoryElement> elements = new ObjectArrayList<>();

    public void add(HistoryElement element){
        elements.add(element);
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

    public CompletableFuture<Integer> searchAsync(Predicate<HistoryElement> predicate, Consumer<HistoryElement> consumer){
        return CompletableFuture.supplyAsync(() -> search(predicate, consumer));
    }

    public void forEach(Consumer<HistoryElement> consumer){
        elements.forEach(consumer);
    }

}
