package com.kelaryon.store_management_tool.error_handling;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(Class<?> entityType,String fieldName,Object value) {
        super(entityType.getSimpleName()
                + " with "
                + fieldName
                + " '" + value + "' already exists.");
    }
}
