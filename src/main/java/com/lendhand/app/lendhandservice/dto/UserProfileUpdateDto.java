package com.lendhand.app.lendhandservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDto {
    @Size(max = 100, message = "Информация о местоположении не должна превышать 100 символов.")
    private String location;

    @Size(max = 500, message = "Описание не должно превышать 500 символов.")
    private String about;

    @Size(max = 255, message = "URL аватара не должен превышать 255 символов.")
    private String avatarUrl;
}
