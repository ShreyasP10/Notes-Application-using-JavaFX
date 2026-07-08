package com.shreyaspawar.data;

import com.shreyaspawar.model.Task;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class DataManager {
    private static final String DELIM = "\u0001";
    private static final String SUB_DELIM = "\u0002";
    private static final Path DATA_DIR = Path.of(System.getProperty("user.home"), ".notesapp");
    private static final Path DATA_FILE = DATA_DIR.resolve("tasks.dat");

    private DataManager() {}

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) return tasks;

        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Task task = parseTask(line);
                if (task != null) tasks.add(task);
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks: " + e.getMessage());
        }
        return tasks;
    }

    public static void saveTasks(List<Task> tasks) {
        try {
            Files.createDirectories(DATA_DIR);
            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(serializeTask(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }

    private static String serializeTask(Task task) {
        return String.join(DELIM,
            escape(task.getTitle()),
            String.valueOf(task.isCompleted()),
            String.valueOf(task.isImportant()),
            escape(task.getPriority()),
            task.getDueDate() != null ? task.getDueDate().toString() : "",
            escape(task.getTag())
        );
    }

    private static Task parseTask(String line) {
        String[] parts = line.split(DELIM, -1);
        if (parts.length < 6) return null;
        try {
            Task task = new Task(unescape(parts[0]));
            task.setCompleted(Boolean.parseBoolean(parts[1]));
            task.setImportant(Boolean.parseBoolean(parts[2]));
            task.setPriority(unescape(parts[3]));
            String dueStr = parts[4];
            if (!dueStr.isEmpty()) {
                task.setDueDate(LocalDate.parse(dueStr));
            }
            task.setTag(unescape(parts[5]));
            return task;
        } catch (Exception e) {
            System.err.println("Failed to parse task: " + e.getMessage());
            return null;
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(DELIM, SUB_DELIM);
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace(SUB_DELIM, DELIM).replace("\\\\", "\\");
    }
}
