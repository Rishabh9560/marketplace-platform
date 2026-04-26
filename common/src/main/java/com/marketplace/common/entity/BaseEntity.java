package com.marketplace.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class with common fields for all entities
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    
    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID")
    protected UUID id;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    protected LocalDateTime updatedAt;
}
