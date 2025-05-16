package com.moviebooking.controller;

import com.moviebooking.model.Movie;
import com.moviebooking.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService service;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> allMovies = service.getAll();
        LocalDate today = LocalDate.now();

        List<Movie> nowShowing = allMovies.stream()
                .filter(m -> !m.getReleaseDate().isAfter(today))
                .collect(Collectors.toList());

        List<Movie> comingSoon = allMovies.stream()
                .filter(m -> m.getReleaseDate().isAfter(today))
                .collect(Collectors.toList());

        model.addAttribute("movies", nowShowing);
        model.addAttribute("comingSoonMovies", comingSoon);
        return "movies/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "movies/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String add(@ModelAttribute Movie movie,
                      @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        service.add(movie, imageFile);
        return "redirect:/admin/manage-movies";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{movieId}")
    public String delete(@PathVariable String movieId) {
        service.delete(movieId);
        return "redirect:/admin/manage-movies";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{movieId}")
    public String editForm(@PathVariable String movieId, Model model) {
        Movie found = service.getById(movieId).orElse(null);
        model.addAttribute("movie", found);
        return "movies/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String update(@ModelAttribute Movie movie) {
        service.update(movie.getMovieId(), movie);
        return "redirect:/admin/manage-movies";
    }
}