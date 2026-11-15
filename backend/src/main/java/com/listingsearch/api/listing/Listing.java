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

    @Column(name = "bathrooms", nullable = false, precision = 4, scale = 1)
    private BigDecimal bathrooms;

    private int sqft;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "listed_date", nullable = false)
    private LocalDate listedDate;

    @Column(nullable = false)
    private String status;

    private String description;  // nullable on purpose
}