package com.example.crud.dto.booking;

public class BestCustomer {
    private String name;
    private Long totalBookings;

    public BestCustomer(String name, Long totalBookings) {
        this.name = name;
        this.totalBookings = totalBookings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }
}

