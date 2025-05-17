package com.moviebooking.service;

import com.moviebooking.model.Showtime;
import com.moviebooking.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    public List<Showtime> getAll() {
        return showtimeRepository.findAll();
    }

    public void add(Showtime s) {
        s.setShowtimeId(UUID.randomUUID().toString().substring(0, 6));
        showtimeRepository.save(s);
    }

    public void update(String id, Showtime s) {
        s.setShowtimeId(id);
        showtimeRepository.update(id, s);
    }

    public void delete(String id) {
        showtimeRepository.delete(id);
    }

    public Optional<Showtime> getById(String showtimeId) {
        return showtimeRepository.findById(showtimeId);
    }

    public List<Showtime> getByMovieId(String movieId) {
        return showtimeRepository.findAll().stream()
                .filter(s -> s.getMovieId().equalsIgnoreCase(movieId))
                .toList();
    }

}

