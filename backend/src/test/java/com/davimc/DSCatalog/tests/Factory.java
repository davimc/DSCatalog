package com.davimc.DSCatalog.tests;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product obj =  new Product("Phone", "Good phone", 2800.75, "./img/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        obj.getCategories().add(createCategory());
        return obj;
    }

    public static ProductDTO createProductDTO() {
        Product obj = createProduct();
        ProductDTO dto = new ProductDTO(obj,obj.getCategories());

        return dto;
    }

    public static Category createCategory() {
        return new Category("Eletronics");
    }
}
