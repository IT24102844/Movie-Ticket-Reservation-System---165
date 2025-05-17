package com.moviebooking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(SeatFileUtil.class);
    private static final String DATA_DIR = "data/";
    private static final long RESERVATION_TIMEOUT_SECONDS = 15 * 60; // 15 minutes in seconds

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            logger.error("Failed to create data directory", e);
        }
    }

    public void initializeSeats(String showtimeId, int totalSeats) {
        Path seatFile = Paths.get(DATA_DIR + "seats_" + showtimeId + ".txt");
        try {
            if (!Files.exists(seatFile)) {
                List<String> seats = new ArrayList<>();
                for (int i = 1; i <= totalSeats; i++) {
                    seats.add("S" + i + "|available");
                }
                Files.write(seatFile, seats);
                logger.info("Created seat file for showtime: {}", showtimeId);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize seats for showtime: " + showtimeId, e);
        }
    }

    public List<String> getAvailableSeats(String showtimeId) {
        Path seatFile = Paths.get(DATA_DIR + "seats_" + showtimeId + ".txt");
        List<String> available = new ArrayList<>();

        try {
            if (!Files.exists(seatFile)) {
                logger.warn("Seat file not found for showtime: {}", showtimeId);
                return available;
            }

            List<String> lines = Files.readAllLines(seatFile);
            List<String> updatedLines = new ArrayList<>();
            boolean needsUpdate = false;

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length > 1) {
                    if ("available".equalsIgnoreCase(parts[1])) {
                        available.add(parts[0]);
                        updatedLines.add(line);
                    } else if (parts[1].startsWith("reserved:")) {
                        try {
                            long reservedTime = Long.parseLong(parts[1].split(":")[1]);
                            long currentTime = Instant.now().getEpochSecond();

                            if (currentTime - reservedTime > RESERVATION_TIMEOUT_SECONDS) {
                                available.add(parts[0]);
                                updatedLines.add(parts[0] + "|available");
                                needsUpdate = true;
                            } else {
                                updatedLines.add(line);
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid reservation timestamp for seat {}: {}", parts[0], parts[1]);
                            updatedLines.add(parts[0] + "|available");
                            needsUpdate = true;
                        }
                    } else {
                        updatedLines.add(line);
                    }
                }
            }

            if (needsUpdate) {
                Files.write(seatFile, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
            }

        } catch (IOException e) {
            logger.error("Error reading seat file for showtime: " + showtimeId, e);
        }
        return available;
    }

    public synchronized void markSeatAsBooked(String showtimeId, String seat) {
        updateSeatStatus(showtimeId, seat, "booked");
    }

    public synchronized void markSeatAsReserved(String showtimeId, String seat) {
        updateSeatStatus(showtimeId, seat, "reserved:" + Instant.now().getEpochSecond());
    }

    private void updateSeatStatus(String showtimeId, String seat, String status) {
        Path seatFile = Paths.get(DATA_DIR + "seats_" + showtimeId + ".txt");
        try {
            if (!Files.exists(seatFile)) {
                logger.warn("Seat file not found for showtime: {}", showtimeId);
                return;
            }

            List<String> updated = Files.readAllLines(seatFile).stream()
                    .map(line -> {
                        String[] parts = line.split("\\|");
                        return parts[0].equals(seat) ? seat + "|" + status : line;
                    })
                    .collect(Collectors.toList());

            Files.write(seatFile, updated, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Error updating seat status for showtime: {}", showtimeId, e);
        }
    }

    public List<String> getAllSeats(String showtimeId) {
        Path seatFile = Paths.get(DATA_DIR + "seats_" + showtimeId + ".txt");
        List<String> allSeats = new ArrayList<>();

        try {
            if (!Files.exists(seatFile)) {
                logger.warn("Seat file not found for showtime: {}", showtimeId);
                return allSeats;
            }

            for (String line : Files.readAllLines(seatFile)) {
                allSeats.add(line.split("\\|")[0]);
            }
        } catch (IOException e) {
            logger.error("Error reading all seats for showtime: " + showtimeId, e);
        }
        return allSeats;
    }

    private List<String> getSeatsByStatus(String showtimeId, String statusPrefix) {
        Path seatFile = Paths.get(DATA_DIR + "seats_" + showtimeId + ".txt");
        List<String> seats = new ArrayList<>();

        try {
            if (!Files.exists(seatFile)) {
                logger.warn("Seat file not found for showtime: {}", showtimeId);
                return seats;
            }

            for (String line : Files.readAllLines(seatFile)) {
                String[] parts = line.split("\\|");
                if (parts.length > 1 && parts[1].startsWith(statusPrefix)) {
                    seats.add(parts[0]);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading {} seats for showtime: {}", statusPrefix, showtimeId, e);
        }
        return seats;
    }
}