package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import com.davimc.DSCatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;
    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryRepository categoryRepository;


    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Category product;
    private CategoryDTO productDTO;
    private PageImpl page;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createCategory();
        page = new PageImpl<>(List.of(product));
        productDTO = Factory.createCategoryDTO();

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(ObjectNotFoundException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);

    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        CategoryDTO result = service.findById(existingId);

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
        Assertions.assertNotNull(result);
    }
    @Test
    public void findByIdShouldThrowObjectNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.findById(nonExistingId);

        });
    }

    @Test
    public void updateShouldReturnCategoryDTOWhenIdExists() {

        CategoryDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
    }
    @Test
    public void updateShouldThrowObjectNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);

        });
    }
    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,10);

        Page<CategoryDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }


    @Test
    public void deleteByIdShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            repository.deleteById(dependentId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }
    @Test
    public void deleteByIdShouldThrowObjectNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            repository.deleteById(nonExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }
    @Test
    public void deleteByIdShouldDeleteObjectWhenIdExist() {
        repository.deleteById(existingId);
        Mockito.verify(repository,Mockito.times(1)).deleteById(existingId);
    }
}
