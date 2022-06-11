package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.entities.Product;
import com.davimc.DSCatalog.repositories.ProductRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<ProductDTO> obj = repository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
        return obj;
    }
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(ProductDTO::new);
    }
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);

        return new ProductDTO(obj.orElseThrow(() -> new ObjectNotFoundException(id, Product.class)));
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product obj = new Product();
        obj.setName(dto.getName());
        obj = repository.save(obj);

        return new ProductDTO(obj);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product obj = repository.getReferenceById(id);
            obj.setName(dto.getName());
            obj = repository.save(obj);
            return new ProductDTO(obj);
        }catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException(id, Product.class);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(id, Product.class);
        }catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }


}