package com.moviebooking.repository;

import com.moviebooking.model.Movie;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MovieRepository {

    private final String filePath = "data/movies.txt";
    private final Path movieFile = Paths.get(filePath);
    private final String imageUploadDir = "uploads/images/";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MovieRepository() {
        ensureFileExists();
        ensureUploadDirExists();
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

    private void ensureUploadDirExists() {
        try {
            Files.createDirectories(Paths.get(imageUploadDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Movie> findAllSortedByReleaseDate() {
        List<Movie> movies = loadAll();
        insertionSortByDate(movies);
        return movies;
    }

    public void save(Movie movie, MultipartFile imageFile) throws IOException {
        try {
            movie.setMovieId(UUID.randomUUID().toString().substring(0, 6));

            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path destination = Paths.get(imageUploadDir + fileName);
                Files.copy(imageFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                movie.setImageName(fileName);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(movieFile,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
                writer.write(format(movie));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IOException("Failed to save movie: " + e.getMessage(), e);
        }
    }

    public void update(String movieId, Movie updatedMovie) {
        List<Movie> all = loadAll();
        try (BufferedWriter writer = Files.newBufferedWriter(movieFile)) {
            for (Movie m : all) {
                if (m.getMovieId().equalsIgnoreCase(movieId)) {
                    updatedMovie.setMovieId(movieId);
                    writer.write(format(updatedMovie));
                } else {
                    writer.write(format(m));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String movieId) {
        List<Movie> all = loadAll();
        try (BufferedWriter writer = Files.newBufferedWriter(movieFile)) {
            for (Movie m : all) {
                if (!m.getMovieId().equalsIgnoreCase(movieId)) {
                    writer.write(format(m));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Movie> loadAll() {
        List<Movie> list = new ArrayList<>();
        try {
            if (!Files.exists(movieFile)){
                return list;
            }

            List<String> lines = Files.readAllLines(movieFile);
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    Movie movie = new Movie(
                            parts[1],
                            parts[2],
                            LocalDate.parse(parts[3], formatter),
                            Integer.parseInt(parts[4]),
                            parts[5]
                    );
                    movie.setMovieId(parts[0]);
                    list.add(movie);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading movies: " + e.getMessage());
        }
        return list;
    }

    private String format(Movie m) {
        return String.join("|",
                m.getMovieId(),
                m.getTitle(),
                m.getGenre(),
                m.getReleaseDate().format(formatter),
                String.valueOf(m.getDuration()),
                m.getImageName()
        );
    }

    private void insertionSortByDate(List<Movie> movies) {
        for (int i = 1; i < movies.size(); i++) {
            Movie key = movies.get(i);
            int j = i - 1;
            while (j >= 0 && movies.get(j).getReleaseDate().isAfter(key.getReleaseDate())) {
                movies.set(j + 1, movies.get(j));
                j--;
            }
            movies.set(j + 1, key);
        }
    }

    public Optional<Movie> findById(String movieId) {
        return loadAll().stream()
                .filter(m -> m.getMovieId().equalsIgnoreCase(movieId))
                .findFirst();
    }
}