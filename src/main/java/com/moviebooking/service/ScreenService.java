package com.moviebooking.service;

import com.moviebooking.model.Screen;
import com.moviebooking.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    public List<Screen> getAll() {
        return screenRepository.findAll();
    }

    public void add(Screen s) {
        s.setScreenId(UUID.randomUUID().toString().substring(0, 6));
        screenRepository.save(s);
    }

    public void update(String id, Screen s) {
        s.setScreenId(id);
        screenRepository.update(id, s);
    }

    public void delete(String id) {
        screenRepository.delete(id);
    }

    public Optional<Screen> getById(String screenId) {
        return screenRepository.findById(screenId);
    }

}
