package com.moviebooking.repository;

import com.moviebooking.model.Theatre;
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
public class TheatreRepository {

    private final String filePath = "data/theatres.txt";
    private final Path theatreFile = Paths.get(filePath);

    public TheatreRepository() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Theatre> findAll() {
        List<Theatre> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(theatreFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    list.add(new Theatre(parts[0], parts[1], parts[2], Integer.parseInt(parts[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void save(Theatre theatre) {
        try (BufferedWriter writer = Files.newBufferedWriter(theatreFile, StandardOpenOption.APPEND)) {
            writer.write(format(theatre));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(String theatreId, Theatre updated) {
        List<Theatre> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(theatreFile)) {
            for (Theatre t : all) {
                if (t.getTheatreId().equalsIgnoreCase(theatreId)) {
                    writer.write(format(updated));
                } else {
                    writer.write(format(t));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String theatreId) {
        List<Theatre> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(theatreFile)) {
            for (Theatre t : all) {
                if (!t.getTheatreId().equalsIgnoreCase(theatreId)) {
                    writer.write(format(t));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String format(Theatre t) {
        return String.join("|", t.getTheatreId(), t.getName(), t.getLocation(), String.valueOf(t.getSeatCapacity()));
    }

    public Optional<Theatre> findById(String theatreId) {
        return findAll().stream()
                .filter(t -> t.getTheatreId().equalsIgnoreCase(theatreId))
                .findFirst();
    }

}
