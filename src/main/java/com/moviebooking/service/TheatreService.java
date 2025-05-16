package com.moviebooking.service;

import com.moviebooking.model.Theatre;
import com.moviebooking.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    public List<Theatre> getAll() {
        return theatreRepository.findAll();
    }

    public void add(Theatre t) {
        t.setTheatreId(UUID.randomUUID().toString().substring(0, 6));
        theatreRepository.save(t);
    }

    public void update(String id, Theatre t) {
        t.setTheatreId(id);
        theatreRepository.update(id, t);
    }

    public void delete(String id) {
        theatreRepository.delete(id);
    }

    public Optional<Theatre> getById(String theatreId) {
        return theatreRepository.findById(theatreId);
    }

}

