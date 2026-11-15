package com.listingsearch.api.listing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "listings")
@Getter
@Setter
@NoArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listing_id", nullable = false)
    private String listingId;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zip;

    @Column(nullable = false)
    private BigDecimal price;

    private int bedrooms;
    private double bathrooms;
    private int sqft;

    private Double latitude;     // nullable on purpose
    private Double longitude;    // nullable on purpose

    @Column(name = "listed_date", nullable = false)
    private LocalDate listedDate;

    @Column(nullable = false)
    private String status;

    private String description;  // nullable on purpose
}