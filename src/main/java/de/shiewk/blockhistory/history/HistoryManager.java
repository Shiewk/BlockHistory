package de.shiewk.blockhistory.history;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class HistoryManager {

    private final ObjectArrayList<HistoryElement> elements = new ObjectArrayList<>();

    public void add(HistoryElement element){
        elements.add(element);
    }

}
