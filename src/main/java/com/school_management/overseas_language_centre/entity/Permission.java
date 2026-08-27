package com.school_management.overseas_language_centre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permission")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = true)
    private String name;
    private String description;
}
