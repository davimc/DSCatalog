package com.davimc.DSCatalog.repositories;

import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*TODO
    * fix problem in category
    * to return all categories without filtering
    * */
    @Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE " +
            "(COALESCE(:categories) IS NULL OR cats IN :categories) AND " +
            "(LOWER(obj.name) LIKE LOWER(CONCAT('%',:name, '%')) )")
    public Page<Product> find(List<Category> categories, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories IN :products")
    public List<Product> findProductsWithCategories(List<Product> products);
}