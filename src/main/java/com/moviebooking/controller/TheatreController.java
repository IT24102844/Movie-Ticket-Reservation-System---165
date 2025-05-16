package com.moviebooking.controller;

import com.moviebooking.model.Theatre;
import com.moviebooking.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/theatres")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("theatres", theatreService.getAll());
        return "theatres/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("theatre", new Theatre());
        return "theatres/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String add(@ModelAttribute Theatre t) {
        theatreService.add(t);
        return "redirect:/theatres";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Theatre found = theatreService.getAll().stream().filter(t -> t.getTheatreId().equalsIgnoreCase(id)).findFirst().orElse(null);
        model.addAttribute("theatre", found);
        return "theatres/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String edit(@ModelAttribute Theatre t) {
        theatreService.update(t.getTheatreId(), t);
        return "redirect:/theatres";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        theatreService.delete(id);
        return "redirect:/theatres";
    }
}
