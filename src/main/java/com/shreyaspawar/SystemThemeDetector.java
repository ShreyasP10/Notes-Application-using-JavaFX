package com.shreyaspawar;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemThemeDetector {

    public static String getSystemTheme() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return getWindowsTheme();
        } else if (os.contains("mac")) {
            return getMacOSTheme();
        } else if (os.contains("linux")) {
            return getLinuxTheme();
        }
        return "light"; // Default to light if detection is not supported
    }

    private static String getWindowsTheme() {
        try {
            Process process = Runtime.getRuntime().exec("reg query HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize /v AppsUseLightTheme");
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("AppsUseLightTheme")) {
                        String[] parts = line.trim().split("\\s+");
                        return parts[parts.length - 1].equals("0x0") ? "dark" : "light";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "light"; // Default to light if detection fails
    }

    private static String getMacOSTheme() {
        try {
            Process process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                return line != null && line.equalsIgnoreCase("Dark") ? "dark" : "light";
            }
        } catch (Exception e) {
            return "light"; // Default to light if detection fails
        }
    }

    private static String getLinuxTheme() {
        try {
            Process process = Runtime.getRuntime().exec("gsettings get org.gnome.desktop.interface gtk-theme");
            process.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String theme = reader.readLine().toLowerCase();
                return theme.contains("dark") ? "dark" : "light";
            }
        } catch (Exception e) {
            return "light"; // Default to light if detection fails
        }
    }
}
