package com.moviebooking.controller;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private TheatreService theatreService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String adminPage() {
        return "admin/dashboard";
    }

    @GetMapping("/manage-movies")
    public String moviePage(Model model) {
        List<Movie> movies = movieService.getAll();
        model.addAttribute("movies", movies);
        return "admin/movies";
    }

    @GetMapping("/manage-bookings")
    public String viewAllBookings(Model model) {
        List<Booking> bookings = bookingService.getAll();
        model.addAttribute("bookings", bookings);
        Map<String, Movie> movieMap = new HashMap<>();
        Map<String, Theatre> theatreMap = new HashMap<>();
        Map<String, Screen> screenMap = new HashMap<>();
        Map<String, Showtime> showtimeMap = new HashMap<>();

        for (Booking booking : bookings) {
            movieService.getById(booking.getMovieId()).ifPresent(movie -> movieMap.put(booking.getMovieId(), movie));
            theatreService.getById(booking.getTheaterId()).ifPresent(theatre -> theatreMap.put(booking.getTheaterId(), theatre));
            screenService.getById(booking.getScreenId()).ifPresent(screen -> screenMap.put(booking.getScreenId(), screen));
            showtimeService.getById(booking.getShowtimeId()).ifPresent(showtime -> showtimeMap.put(booking.getShowtimeId(), showtime));
        }

        model.addAttribute("movieMap", movieMap);
        model.addAttribute("theatreMap", theatreMap);
        model.addAttribute("screenMap", screenMap);
        model.addAttribute("showtimeMap", showtimeMap);
        return "admin/bookings";
    }

    @GetMapping("/manage-payments")
    public String viewAllPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "admin/payments";
    }
}
