package com.moviebooking.repository;

import com.moviebooking.controller.BookingController;
import com.moviebooking.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final String filePath = "data/bookings.txt";
    private final Path bookingFile = Paths.get(filePath);
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final BookingQueue bookingQueue = new BookingQueue();
    private volatile boolean isWriterRunning = false;

    public BookingRepository() {
        ensureFileExists();
        startQueueProcessor();
    }

    private static class BookingNode {
        Booking booking;
        BookingNode next;

        BookingNode(Booking booking) {
            this.booking = booking;
            this.next = null;
        }
    }

    private static class BookingQueue {
        private BookingNode head;
        private BookingNode tail;
        private int size;

        public BookingQueue() {
            head = null;
            tail = null;
            size = 0;
        }

        public synchronized void enqueue(Booking booking) {
            BookingNode newNode = new BookingNode(booking);
            if (tail == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                tail = newNode;
            }
            size++;
            notifyAll();
        }

        public synchronized Booking dequeue() throws InterruptedException {
            while (isEmpty()) {
                wait();
            }
            BookingNode temp = head;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return temp.booking;
        }

        public synchronized boolean isEmpty() {
            return head == null;
        }

        public synchronized int size() {
            return size;
        }
    }


    private void startQueueProcessor() {
        Thread writerThread = new Thread(() -> {
            isWriterRunning = true;
            while (isWriterRunning || !bookingQueue.isEmpty()) {
                try {
                    Booking booking = bookingQueue.dequeue();
                    writeBookingToFile(booking);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Queue processor thread interrupted", e);
                } catch (Exception e) {
                    logger.error("Error processing booking from queue", e);
                }
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();
    }


    private synchronized void writeBookingToFile(Booking booking) {
        try (BufferedWriter writer = Files.newBufferedWriter(bookingFile, StandardOpenOption.APPEND)) {
            writer.write(format(booking));
            writer.newLine();
        } catch (IOException e) {
            logger.error("Error writing booking to file", e);
        }
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(bookingFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|");
                try {
                    LocalDateTime bookingTime;
                    try {
                        bookingTime = LocalDateTime.parse(p[6], dtFormatter);
                    } catch (DateTimeParseException e) {
                        if (p[6].length() == 13) {
                            bookingTime = LocalDateTime.parse(p[6] + ":00:00", dtFormatter);
                        } else {
                            bookingTime = LocalDateTime.now();
                        }
                    }

                    list.add(new Booking(
                            p[0], p[1], p[2], p[3], p[4], p[5], bookingTime
                    ));
                } catch (Exception e) {
                    logger.error("Error parsing booking line: " + line, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading bookings file", e);
        }
        return list;
    }

    public Optional<Booking> findById(String bookingId) {
        return findAll().stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst();
    }

    public void save(Booking booking) {
        bookingQueue.enqueue(booking);
    }

    public void delete(String bookingId) {
        List<Booking> all = findAll();
        try (BufferedWriter writer = Files.newBufferedWriter(bookingFile)) {
            for (Booking b : all) {
                if (!b.getBookingId().equalsIgnoreCase(bookingId)) {
                    writer.write(format(b));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Booking> findByShowtime(String showtimeId) {
        return findAll().stream()
                .filter(b -> b.getShowtimeId().equals(showtimeId))
                .toList();
    }

    private String format(Booking b) {
        return String.join("|",
                b.getBookingId(),
                b.getMovieId(),
                b.getTheaterId(),
                b.getScreenId(),
                b.getShowtimeId(),
                b.getSeatNumber(),
                b.getBookingTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    public void shutdown() {
        isWriterRunning = false;
        synchronized (bookingQueue) {
            bookingQueue.notifyAll();
        }
    }
}