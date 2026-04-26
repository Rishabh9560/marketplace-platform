package com.marketplace.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {
    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(unique = true, nullable = false)
    private String businessEmail;

    private String businessPhone;
    private String registrationNumber;
    private String taxId;
    private String bankAccountNumber;
    private String bankName;

    private Double commissionRate = 5.00;

    @Enumerated(EnumType.STRING)
    private VendorStatus status = VendorStatus.PENDING;

    private LocalDateTime approvalDate;
    private String rejectionReason;

    private String storeUrl;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private Double rating = 0.0;
    private Integer totalReviews = 0;
    private Integer totalProducts = 0;
    private Integer totalOrders = 0;
    private Double totalRevenue = 0.0;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @OneToOne(mappedBy = "vendor", cascade = CascadeType.ALL)
    private VendorKyc kyc;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<VendorPayout> payouts = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum VendorStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }
}
