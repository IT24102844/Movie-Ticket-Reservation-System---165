package com.moviebooking.model;

import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String userName;
    private String userEmail;
    private String phone;
    private String bookingId;
    private double amount;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvc;
    private String cardType;
    private LocalDateTime paymentTime;
    private String status;

    public Payment() {}

    public Payment(String paymentId, String userName, String userEmail, String phone, String bookingId, double amount, String cardNumber, String cardExpiry, String cardCvc, String cardType, LocalDateTime paymentTime, String status) {
        this.paymentId = paymentId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.phone = phone;
        this.bookingId = bookingId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCvc = cardCvc;
        this.cardType = cardType;
        this.paymentTime = paymentTime;
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvc() {
        return cardCvc;
    }

    public void setCardCvc(String cardCvc) {
        this.cardCvc = cardCvc;
    }
}