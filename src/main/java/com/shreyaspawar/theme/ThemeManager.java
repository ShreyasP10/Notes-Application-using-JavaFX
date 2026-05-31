package com.shreyaspawar.theme;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public enum Theme { DARK, LIGHT }

    private static Theme current = Theme.DARK;
    private static final List<Scene> registeredScenes = new ArrayList<>();

    public static void registerScene(Scene scene) {
        registeredScenes.add(scene);
        applyTheme(scene);
    }

    public static void setTheme(Theme theme) {
        current = theme;
        for (Scene s : registeredScenes) {
            applyTheme(s);
        }
    }

    private static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ThemeManager.class.getResource("/styles/base.css").toExternalForm());
        if (current == Theme.DARK) {
            scene.getStylesheets().add(ThemeManager.class.getResource("/styles/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource("/styles/light-theme.css").toExternalForm());
        }
    }
}