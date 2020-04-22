package cz.muni.fi.gag.web.services.filters;

import java.util.ArrayList;

public class HistoryArrayList<T> extends ArrayList<T> {
        int historyCount = 25;

    public HistoryArrayList(int historyCount) {
        this.historyCount = historyCount;
    }

    @Override
    public boolean add(T e) {
        if (this.size() > historyCount) {
            this.remove(this.size() - 1);
        }
        return super.add(e);
    }
}