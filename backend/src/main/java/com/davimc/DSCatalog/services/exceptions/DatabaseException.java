package com.davimc.DSCatalog.services.exceptions;

import com.davimc.DSCatalog.entities.Category;

public class DatabaseException extends RuntimeException{

    public DatabaseException(String message) {
        super(message);
    }
}
