package com.moviebooking.repository;

import com.moviebooking.model.Screen;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ScreenRepository {

    private final String filePath = "data/screens.txt";
    private final Path screenFile = Paths.get(filePath);

    public ScreenRepository() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Screen> findAll() {
        List<Screen> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(screenFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    list.add(new Screen(parts[0], parts[1], parts[2],
                            Integer.parseInt(parts[3]), Double.parseDouble(parts[4])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void save(Screen s) {
        try (BufferedWriter writer = Files.newBufferedWriter(screenFile, StandardOpenOption.APPEND)) {
            writer.write(format(s));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(String screenId, Screen updated) {
        List<Screen> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(screenFile)) {
            for (Screen s : all) {
                if (s.getScreenId().equalsIgnoreCase(screenId)) {
                    writer.write(format(updated));
                } else {
                    writer.write(format(s));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String screenId) {
        List<Screen> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(screenFile)) {
            for (Screen s : all) {
                if (!s.getScreenId().equalsIgnoreCase(screenId)) {
                    writer.write(format(s));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String format(Screen s) {
        return String.join("|",
                s.getScreenId(), s.getTheatreId(),
                s.getScreenName(), String.valueOf(s.getTotalSeats()),
                String.valueOf(s.getTicketPrice()));
    }

    public Optional<Screen> findById(String screenId) {
        return findAll().stream()
                .filter(s -> s.getScreenId().equalsIgnoreCase(screenId))
                .findFirst();
    }

}
