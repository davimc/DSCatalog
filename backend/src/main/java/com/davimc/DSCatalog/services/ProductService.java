package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.DTO.ProductDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.entities.Product;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.repositories.ProductRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<ProductDTO> obj = repository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
        return obj;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Long categoryId, String name, PageRequest pageRequest) {
        List<Category> categories = (categoryId == 0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));
        Page<Product> list = repository.find(categories, name, pageRequest);
        repository.findProductsWithCategories(list.getContent());
        return list.map(x -> new ProductDTO(x, x.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product obj = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Product.class));
        return new ProductDTO(obj,obj.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product obj = new Product();
        copyDtoToEntity(dto, obj);
        obj = repository.save(obj);

        return new ProductDTO(obj);
    }


    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product obj = repository.getOne(id);
            copyDtoToEntity(dto,obj);
            obj = repository.save(obj);
            return new ProductDTO(obj);
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException(id, Product.class);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(id, Product.class);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product obj) {
        obj.setName(dto.getName());
        obj.setDate(dto.getDate());
        obj.setDescription(dto.getDescription());
        obj.setPrice(dto.getPrice());
        obj.setImgUrl(dto.getImgUrl());

        obj.getCategories().clear();
        for(CategoryDTO catDTO: dto.getCategories()) {
            Category cat = categoryRepository.getOne(catDTO.getId());
            obj.getCategories().add(cat);
        }
    }
}
