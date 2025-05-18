package com.moviebooking.service;

import com.moviebooking.controller.BookingController;
import com.moviebooking.model.Booking;
import com.moviebooking.repository.BookingRepository;
import com.moviebooking.util.SeatFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatFileUtil seatUtil;


    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    private final Object seatLock = new Object();

    public void cancelBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(booking -> {
            Arrays.stream(booking.getSeatNumber().split(","))
                    .forEach(seat -> markSeatAsAvailable(booking.getShowtimeId(), seat));

            bookingRepository.delete(bookingId);
        });
    }

    public List<String> getAvailableSeats(String showtimeId) {
        return seatUtil.getAvailableSeats(showtimeId);
    }

    public List<String> getAllSeats(String showtimeId) {
        return seatUtil.getAllSeats(showtimeId);
    }

    public boolean areSeatsAvailable(String showtimeId, String seatNumbers) {
        List<String> availableSeats = getAvailableSeats(showtimeId);
        Set<String> availableSet = new HashSet<>(availableSeats);

        return Arrays.stream(seatNumbers.split(","))
                .allMatch(seat -> availableSet.contains(seat.trim()));
    }

    private void markSeatAsAvailable(String showtimeId, String seat) {
        String seatFilePath = "data/seats_" + showtimeId + ".txt";
        List<String> updated = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(seatFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(seat)) {
                    updated.add(parts[0] + "|available");
                } else {
                    updated.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(seatFilePath))) {
            for (String s : updated) {
                writer.write(s);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getBookedSeats(String showtimeId) {
        List<String> allSeats = seatUtil.getAllSeats(showtimeId);
        List<String> bookedSeats = new ArrayList<>();

        try {
            Path seatFile = Paths.get("data/seats_" + showtimeId + ".txt");
            if (Files.exists(seatFile)) {
                for (String line : Files.readAllLines(seatFile)) {
                    String[] parts = line.split("\\|");
                    if (parts.length > 1 && "booked".equalsIgnoreCase(parts[1])) {
                        bookedSeats.add(parts[0]);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading booked seats for showtime: " + showtimeId, e);
        }
        return bookedSeats;
    }

    public Booking reserveSeats(String showtimeId, String movieId, String theaterId,
                                String screenId, String seatNumbers) {
        synchronized (seatLock) {
            if (!areSeatsAvailable(showtimeId, seatNumbers)) {
                throw new IllegalStateException("One or more selected seats are no longer available");
            }

            String bookingId = UUID.randomUUID().toString().substring(0, 6);
            LocalDateTime now = LocalDateTime.now();

            Booking booking = new Booking(
                    bookingId, movieId, theaterId, screenId, showtimeId,
                    seatNumbers, now
            );

            Arrays.stream(seatNumbers.split(","))
                    .forEach(seat -> seatUtil.markSeatAsReserved(showtimeId, seat));

            bookingRepository.save(booking);
            return booking;
        }
    }

    public void confirmBooking(Booking booking) {
        synchronized (seatLock) {
            Arrays.stream(booking.getSeatNumber().split(","))
                    .forEach(seat -> seatUtil.markSeatAsBooked(booking.getShowtimeId(), seat));

            bookingRepository.save(booking);
        }
    }
}