package com.davimc.DSCatalog.services.exceptions;

import com.davimc.DSCatalog.entities.Category;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Long id, Class<Category> categoryClass) {
        super("Entity not found. id: " + id + " to: " + categoryClass.getSimpleName());
    }
}
