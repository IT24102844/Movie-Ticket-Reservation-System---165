package com.moviebooking.controller;

import com.moviebooking.model.Screen;
import com.moviebooking.service.ScreenService;
import com.moviebooking.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/screens")
public class ScreenController {

    @Autowired
    private ScreenService screenService;
    @Autowired
    private TheatreService theatreService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("screens", screenService.getAll());
        return "screens/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("screen", new Screen());
        model.addAttribute("theatres", theatreService.getAll()); // Add theatres for dropdown
        return "screens/add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String add(@ModelAttribute Screen s) {
        screenService.add(s);
        return "redirect:/screens";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        Screen found = screenService.getAll().stream().filter(s -> s.getScreenId().equalsIgnoreCase(id)).findFirst().orElse(null);
        model.addAttribute("screen", found);
        return "screens/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String edit(@ModelAttribute Screen s) {
        screenService.update(s.getScreenId(), s);
        return "redirect:/screens";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        screenService.delete(id);
        return "redirect:/screens";
    }
}

