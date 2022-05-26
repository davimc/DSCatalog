package com.davimc.DSCatalog.services.exceptions;

import com.davimc.DSCatalog.entities.Category;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(Long id, Class<Category> categoryClass) {
        super("Object not found. id: " + id + " to: " + categoryClass.getSimpleName());
    }
}
