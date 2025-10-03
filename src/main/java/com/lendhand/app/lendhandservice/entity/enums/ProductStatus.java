package com.lendhand.app.lendhandservice.entity.enums;
public enum ProductStatus {
    AVAILABLE("Доступно"), RESERVED("Зарезервировано"), UNAVAILABLE("Недоступно");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
