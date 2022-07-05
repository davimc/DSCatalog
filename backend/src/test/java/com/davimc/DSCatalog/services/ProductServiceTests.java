package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.entities.Product;
import com.davimc.DSCatalog.repositories.ProductRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import com.davimc.DSCatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Product product;
    private PageImpl page;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(ObjectNotFoundException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,10);

        Page<ProductDTO> result = service.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenExistId() {
        ProductDTO pdct = service.findById(existingId);

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
        Assertions.assertTrue(pdct!=null);
    }
    @Test
    public void findByIdShouldThrowObjectNotFoundExceptionWhenDoesNotExistsId() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.findById(nonExistingId);

        });
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
