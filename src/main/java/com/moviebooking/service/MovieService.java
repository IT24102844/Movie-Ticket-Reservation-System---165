package com.moviebooking.service;

import com.moviebooking.model.Movie;
import com.moviebooking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAll() {
        return movieRepository.findAllSortedByReleaseDate();
    }

    public void add(Movie movie, MultipartFile imageFile) throws IOException {
        movieRepository.save(movie, imageFile);
    }

    public void update(String movieId, Movie updatedMovie) {
        movieRepository.update(movieId, updatedMovie);
    }

    public void delete(String movieId) {
        movieRepository.delete(movieId);
    }

    public Optional<Movie> getById(String movieId) {
        return movieRepository.findById(movieId);
    }
}