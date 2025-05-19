package com.moviebooking.controller;

import com.moviebooking.model.*;
import com.moviebooking.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ShowtimeService showtimeService;
    @Autowired
    private TicketPdfService ticketPdfService;
    @Autowired
    private TheatreService theatreService;

    @GetMapping("/new/{bookingId}")
    public String showPaymentForm(@PathVariable String bookingId, Model model) {
        Optional<Booking> bookingOpt = bookingService.getById(bookingId);
        if (!bookingOpt.isPresent()) {
            return "redirect:/bookings?error=booking_not_found";
        }

        Booking booking = bookingOpt.get();
        Optional<Screen> screenOpt = screenService.getById(booking.getScreenId());
        if (!screenOpt.isPresent()) {
            return "redirect:/bookings?error=screen_not_found";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("screen", screenOpt.get());
        return "payments/payment-form";
    }

    @PostMapping("/process")
    public String processPayment(
            @RequestParam String bookingId,
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam String phone,
            @RequestParam double amount,
            @RequestParam String cardNumber,
            @RequestParam String cardType,
            @RequestParam String cardExpiry,
            @RequestParam String cardCvc,
            Model model) {

        Booking booking = bookingService.getById(bookingId).orElseThrow();

        Payment payment = paymentService.processPayment(
                userName, userEmail, phone,
                bookingId, amount,
                cardNumber, cardType,
                cardExpiry, cardCvc
        );

        // Confirm booking after successful payment
        bookingService.confirmBooking(booking);

        // Get additional booking details for the view
        Optional<Movie> movie = movieService.getById(booking.getMovieId());
        Optional<Screen> screen = screenService.getById(booking.getScreenId());
        Optional<Showtime> showtime = showtimeService.getById(booking.getShowtimeId());
        Optional<Theatre> theatre = theatreService.getById(booking.getTheaterId());

        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        model.addAttribute("movie", movie.orElse(null));
        model.addAttribute("screen", screen.orElse(null));
        model.addAttribute("showtime", showtime.orElse(null));
        model.addAttribute("theatre", theatre.orElse(null));

        return "payments/confirmation";
    }

    @GetMapping("/download-ticket/{bookingId}")
    public void downloadTicket(@PathVariable String bookingId, HttpServletResponse response) {
        try {
            // Get booking and payment info
            Booking booking = bookingService.getById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            Payment payment = paymentService.getPaymentByBookingId(bookingId)
                    .orElseThrow(() -> new RuntimeException("Payment not found for this booking"));

            // Get all required data
            Movie movie = movieService.getById(booking.getMovieId())
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            Screen screen = screenService.getById(booking.getScreenId())
                    .orElseThrow(() -> new RuntimeException("Screen not found"));

            Showtime showtime = showtimeService.getById(booking.getShowtimeId())
                    .orElseThrow(() -> new RuntimeException("Showtime not found"));

            Theatre theatre = theatreService.getById(screen.getTheatreId())
                    .orElseThrow(() -> new RuntimeException("Theatre not found"));

            // Generate PDF with all parameters
            byte[] pdfBytes = ticketPdfService.generateTicketPdf(
                    booking,
                    payment,
                    movie,
                    screen,
                    showtime,
                    theatre
            );

            // Set response headers
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ticket_" + booking.getBookingId() + ".pdf");
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to generate ticket: " + e.getMessage());
            } catch (IOException ioException) {
                logger.error("Error sending error response", ioException);
            }
            logger.error("Failed to generate ticket for booking {}", bookingId, e);
        }
    }

}

