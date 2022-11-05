package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.repositories.ProductRepository;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;

    private Long existingid;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception{
        existingid = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingid);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowObjectNotFoundExceptionWhenidDoesNotExist() {
        Assertions.assertThrows(ObjectNotFoundException.class, () ->{
            service.delete(nonExistingId);
        });
    }
    @Test
    public void findAllPageShouldReturnPageWhenPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAllPaged(0L,"",pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }
    @Test
    public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50,10);
        Page<ProductDTO> result = service.findAllPaged(0L, "",pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPageShouldReturnPageSortedWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
        Page<ProductDTO> result = service.findAllPaged(0L, "", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }
}
