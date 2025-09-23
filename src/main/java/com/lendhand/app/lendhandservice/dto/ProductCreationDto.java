package com.lendhand.app.lendhandservice.dto;

import com.lendhand.app.lendhandservice.entity.enums.ProductCategory;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreationDto {
    @NotBlank(message = "Пожалуйста, укажите название товара или услуги.")
    private String title;

    @NotNull (message = "Пожалуйста, укажите цену товара или услуги.")
    @PositiveOrZero
    @Digits(integer = 7, fraction = 0, message = "Цена товара или услуги должна быть целым числом. ")
    private BigDecimal price;

    @NotNull(message = "Пожалуйста, выберите категорию товара или услуги.")
    private ProductCategory category;

    private String description;
}
