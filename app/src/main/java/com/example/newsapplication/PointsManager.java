package com.example.newsapplication;

import java.util.ArrayList;
import java.util.List;

public class PointsManager {
    private static PointsManager instance;
    private int points;
    private final List<PointsChangeListener> listeners;

    private PointsManager() {
        points = 0;
        listeners = new ArrayList<>();
    }

    public static synchronized PointsManager getInstance() {
        if (instance == null) {
            instance = new PointsManager();
        }
        return instance;
    }

    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
        notifyListeners();
    }

    public int getPoints() {
        return points;
    }

    public void addListener(PointsChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PointsChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (PointsChangeListener listener : listeners) {
            listener.onPointsChanged(points);
        }
    }

    public interface PointsChangeListener {
        void onPointsChanged(int newPoints);
    }
}


