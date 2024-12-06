package com.vinesh.SpringRest.payload.product;

import java.util.ArrayList;
import java.util.Map;

import com.vinesh.SpringRest.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProductResponseDTO {
    private ArrayList<CategoryMapping> category;
    private Map<Long, ArrayList<SubCategoryMapping>> subcategory;
    private Map<Long, Map<Integer, ArrayList<ProductMapping>>> product;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryMapping {
        private long id;
        private String cname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCategoryMapping {
        private long id;
        private String sname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductMapping {
        private long productid;
        private String productName;
        private int prodcutPrice;
        private Double productWeight;
        private int productStock;
        private Account account;
    }
}
