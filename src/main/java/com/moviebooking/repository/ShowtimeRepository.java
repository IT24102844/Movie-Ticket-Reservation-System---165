package com.moviebooking.repository;

import com.moviebooking.model.Showtime;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ShowtimeRepository {

    private final String filePath = "data/showtimes.txt";
    private final Path showtimeFile = Paths.get(filePath);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ShowtimeRepository() {
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

    public List<Showtime> findAll() {
        List<Showtime> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(showtimeFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    list.add(new Showtime(
                            parts[0], parts[1], parts[2], parts[3],
                            LocalDate.parse(parts[4], dateFormatter),
                            LocalTime.parse(parts[5], timeFormatter)
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void save(Showtime s) {
        try (BufferedWriter writer = Files.newBufferedWriter(showtimeFile, StandardOpenOption.APPEND)) {
            writer.write(format(s));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(String id, Showtime updated) {
        List<Showtime> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(showtimeFile)) {
            for (Showtime s : all) {
                if (s.getShowtimeId().equalsIgnoreCase(id)) {
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

    public void delete(String id) {
        List<Showtime> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(showtimeFile)) {
            for (Showtime s : all) {
                if (!s.getShowtimeId().equalsIgnoreCase(id)) {
                    writer.write(format(s));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String format(Showtime s) {
        return String.join("|",
                s.getShowtimeId(), s.getMovieId(), s.getTheatreId(),
                s.getScreenId(), s.getDate().format(dateFormatter), s.getTime().format(timeFormatter));
    }

    public Optional<Showtime> findById(String showtimeId) {
        return findAll().stream()
                .filter(s -> s.getShowtimeId().equalsIgnoreCase(showtimeId))
                .findFirst();
    }

}

