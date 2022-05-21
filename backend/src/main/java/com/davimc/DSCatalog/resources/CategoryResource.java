package com.davimc.DSCatalog.resources;

import com.davimc.DSCatalog.entities.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("Informática"));
        list.add(new Category("Cama e Banho"));
        list.add(new Category("Escritório"));
        list.add(new Category("Música"));

        return ResponseEntity.ok().body(list);
    }
}
