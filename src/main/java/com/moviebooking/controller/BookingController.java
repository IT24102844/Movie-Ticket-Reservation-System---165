package com.moviebooking.controller;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import com.moviebooking.util.SeatFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private TheatreService theatreService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private SeatFileUtil seatFileUtil;

    @GetMapping("/select-showtime")
    public String showShowtimeSelection(
            @RequestParam String movieId,
            @RequestParam(required = false) String theatreId,
            @RequestParam(required = false) String screenId,
            Model model) {

        Optional<Movie> movie = movieService.getById(movieId);
        if (!movie.isPresent()) {
            return "redirect:/movies";
        }

        model.addAttribute("movie", movie.get());
        model.addAttribute("theatres", theatreService.getAll());

        if (theatreId != null) {
            model.addAttribute("selectedTheatreId", theatreId);
            model.addAttribute("screens", screenService.getAll());

            if (screenId != null) {
                model.addAttribute("selectedScreenId", screenId);
                model.addAttribute("showtimes", showtimeService.getByMovieId(movieId));
            }
        }

        return "bookings/select-showtime";
    }

    @GetMapping("/select-seats")
    public String showSeatSelection(
            @RequestParam String movieId,
            @RequestParam String theatreId,
            @RequestParam String screenId,
            @RequestParam String showtimeId,
            Model model) {

        Optional<Movie> movie = movieService.getById(movieId);
        Optional<Theatre> theatre = theatreService.getById(theatreId);
        Optional<Screen> screen = screenService.getById(screenId);
        Optional<Showtime> showtime = showtimeService.getById(showtimeId);

        if (!movie.isPresent() || !theatre.isPresent() || !screen.isPresent() || !showtime.isPresent()) {
            return "redirect:/movies?error=invalid_selection";
        }

        seatFileUtil.initializeSeats(showtimeId, screen.get().getTotalSeats());

        model.addAttribute("movie", movie.get());
        model.addAttribute("theater", theatre.get());
        model.addAttribute("screen", screen.get());
        model.addAttribute("showtime", showtime.get());
        model.addAttribute("allSeats", seatFileUtil.getAllSeats(showtimeId));
        model.addAttribute("bookedSeats", bookingService.getBookedSeats(showtimeId));

        return "bookings/select-seats";
    }

    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam String movieId,
            @RequestParam String theaterId,
            @RequestParam String screenId,
            @RequestParam String showtimeId,
            @RequestParam String seatNumber,
            Model model) {

        try {
            // First check if seats are still available
            if (!bookingService.areSeatsAvailable(showtimeId, seatNumber)) {
                return buildRedirectUrl(movieId, theaterId, screenId, showtimeId, "seat_taken");
            }

            Booking booking = bookingService.reserveSeats(
                    showtimeId, movieId, theaterId, screenId,
                    seatNumber
            );

            return "redirect:/payments/new/" + booking.getBookingId();
        } catch (Exception e) {
            logger.error("Error creating booking", e);
            return buildRedirectUrl(movieId, theaterId, screenId, showtimeId, "booking_failed");
        }
    }

    private String buildRedirectUrl(String movieId, String theaterId, String screenId,
                                    String showtimeId, String error) {
        return "redirect:/bookings/select-seats?" +
                "movieId=" + movieId +
                "&theaterId=" + theaterId +
                "&screenId=" + screenId +
                "&showtimeId=" + showtimeId +
                (error != null ? "&error=" + error : "");
    }
}