package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<CategoryDTO> obj = repository.findAll().stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
        return obj;
    }
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);

        return new CategoryDTO(obj.orElseThrow(() -> new EntityNotFoundException(id, Category.class)));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);

        return new CategoryDTO(entity);
    }
}
