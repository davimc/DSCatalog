package com.davimc.DSCatalog.services;

import com.davimc.DSCatalog.DTO.CategoryDTO;
import com.davimc.DSCatalog.entities.Category;
import com.davimc.DSCatalog.repositories.CategoryRepository;
import com.davimc.DSCatalog.services.exceptions.DatabaseException;
import com.davimc.DSCatalog.services.exceptions.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

        return new CategoryDTO(obj.orElseThrow(() -> new ObjectNotFoundException(id, Category.class)));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category obj = new Category();
        obj.setName(dto.getName());
        obj = repository.save(obj);

        return new CategoryDTO(obj);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category obj = repository.getReferenceById(id);
            obj.setName(dto.getName());
            obj = repository.save(obj);
            return new CategoryDTO(obj);
        }catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException(id, Category.class);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException(id, Category.class);
        }catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
}
