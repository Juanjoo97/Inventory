package com.litethinking.inventory.domain.exception;

/** Se lanza al intentar crear un recurso que viola una restriccion de unicidad. Mapea a HTTP 409. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
