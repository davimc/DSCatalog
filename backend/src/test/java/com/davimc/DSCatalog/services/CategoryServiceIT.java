package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CategoryServiceIT {

    @Autowired
    private CategoryService service;
    @Autowired
    private CategoryRepository repository;

    private Long independentId;
    private Long dependentId;
    private Long nonExistingId;
    private Long countTotalCategorys;

    @BeforeEach
    void setUp() throws Exception{
        dependentId = 1L;
        independentId = 4L;
        nonExistingId = 1000L;
        countTotalCategorys = 4L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIndependentId() {
        service.delete(independentId);

        Assertions.assertEquals(countTotalCategorys - 1, repository.count());
    }


    /*
     * TODO: por algum motivo ele está permitindo passar ids dependentes
     *  mas retorna DataIntegrityViolationException quando faz a contagem(repository.count())
     */
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () ->{
            service.delete(dependentId);
        });
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
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }
    @Test
    public void findAllPageShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50,10);
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPageShouldReturnPageSortedWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("name"));
        Page<CategoryDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Computadores", result.getContent().get(0).getName());
        Assertions.assertEquals("Eletrônicos", result.getContent().get(1).getName());
        Assertions.assertEquals("Futuristas", result.getContent().get(2).getName());
    }
}
