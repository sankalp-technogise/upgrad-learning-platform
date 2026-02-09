package com.technogise.upgrad.backend.dto;

/**
 * DTO representing an interest option.
 *
 * @param id Unique identifier (constant name like "PYTHON_PROGRAMMING")
 * @param name Display name shown to users
 * @param description Detailed description of the interest
 * @param iconName Icon identifier for UI rendering
 */
public record InterestDTO(String id, String name, String description, String iconName) {}
