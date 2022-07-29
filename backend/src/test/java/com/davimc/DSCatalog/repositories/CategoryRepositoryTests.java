package com.davimc.DSCatalog.repositories;

import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repository;
    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private long countTotalIds;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        countTotalIds = repository.count();


    }



    @Test
    public void findByIdShouldReturnNonEmptyOptionalCategoryWhenIdExist() {

        Assertions.assertDoesNotThrow(()-> {
            Optional<Category> obj = repository.findById(existingId);
            Assertions.assertTrue(obj.isPresent());
            Assertions.assertEquals(obj.get().getId(),existingId);
        });
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalCategoryWhenIdDoesNotExist() {
        Assertions.assertDoesNotThrow(()-> {
            Optional<Category> obj = repository.findById(nonExistingId);
            Assertions.assertFalse(obj.isPresent());
        });
    }

    @Test
    public void saveShouldCreateAndAutoincrementIdWhenIdIsNull() {
        Category obj = Factory.createCategory();
        obj = repository.save(obj);

        Assertions.assertNotNull(obj.getId());
        Assertions.assertEquals(countTotalIds+1, obj.getId());
    }

    @Test
    public void deleteShouldDeleteCategoryWhenIdExists() {


        repository.deleteById(existingId);

        Optional<Category> obj = repository.findById(existingId);
        Assertions.assertFalse(obj.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {


        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            repository.deleteById(nonExistingId);
        });
    }
}
