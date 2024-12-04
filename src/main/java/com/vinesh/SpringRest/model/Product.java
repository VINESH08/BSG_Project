package com.vinesh.SpringRest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String p_name;

    private Integer p_price;

    private Double p_weight;
    private Integer p_stock;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = true)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "subCategory_id", referencedColumnName = "id", nullable = true)
    private SubCategory subCategory;
}
