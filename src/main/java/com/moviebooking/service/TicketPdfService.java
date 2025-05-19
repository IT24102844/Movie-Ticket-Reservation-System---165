package com.moviebooking.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.moviebooking.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class TicketPdfService {

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
    private PaymentService paymentService;

    public byte[] generateTicketPdf(Booking booking, Payment payment,
                                    Movie movie, Screen screen, Showtime showtime, Theatre theatre)
            throws DocumentException {

        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("MOVIE TICKET", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableRow(table, "Movie: ", movie.getTitle());
            addTableRow(table, "Theater: ",theatre.getName()+", " + theatre.getLocation());
            addTableRow(table, "Screen: ", screen.getScreenName());
            addTableRow(table, "Date: ", showtime.getDate().toString());
            addTableRow(table, "Time: ", showtime.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            addTableRow(table, "Seats: ", booking.getSeatNumber());
            addTableRow(table, "Booking ID: ", booking.getBookingId());

            addTableRow(table, "Payment ID: ", payment.getPaymentId());
            addTableRow(table, "Amount: ", String.format("%.2f", payment.getAmount())+ " Lkr");
            addTableRow(table, "Payment Method: ", payment.getCardType() + " ****" + payment.getCardNumber());

            document.add(table);

            Paragraph thanks = new Paragraph("Enjoy the Movie!",
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            thanks.setAlignment(Element.ALIGN_CENTER);
            thanks.setSpacingBefore(20);
            document.add(thanks);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return outputStream.toByteArray();
    }

    private void addTableRow(PdfPTable table, String header, String content) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
        headerCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell contentCell = new PdfPCell(new Phrase(content, contentFont));
        contentCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(headerCell);
        table.addCell(contentCell);
    }
}