package com.vinesh.SpringRest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    @Column(nullable = false)
    private String p_name;
    @Column(nullable = false)
    private Double p_price;
    @Column(nullable = false)
    private Double p_weight;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = true)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "subCategory_id", referencedColumnName = "id", nullable = true)
    private SubCategory subCategory;
}
