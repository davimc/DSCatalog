package com.davimc.DSCatalog.tests;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product obj =  new Product("Phone", "Good phone", 2800.75, "./img/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        obj.getCategories().add(createCategory(1L));
        return obj;
    }

    public static ProductDTO createProductDTO() {
        Product obj = createProduct();
        ProductDTO dto = new ProductDTO(obj,obj.getCategories());
        dto.setId(1L);

        return dto;
    }

    public static Category createCategory() {
        return new Category(null,"Eletronics");
    }

    public static Category createCategory(Long id) {
        return new Category(id,"Eletronics");
    }
    public static CategoryDTO createCategoryDTO() {
        Category obj = createCategory(1L);
        CategoryDTO dto = new CategoryDTO(obj);

        return dto;
    }
}
