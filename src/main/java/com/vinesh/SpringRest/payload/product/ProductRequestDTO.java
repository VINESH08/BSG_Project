package com.vinesh.SpringRest.payload.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductRequestDTO {

    private CategoryDTO category;

    private SubCategoryDTO subCategory;

    private ProductDTO product;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCategoryDTO {
        private Long id;
        private String name;
        private Long categoryId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDTO {
        private Long id;
        private String product_name;
        private int product_price;
        private Double product_weight;
        private int product_stock;
    }

}
