package com.lendhand.app.lendhandservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank (message = "Пожалуйста, укажите имя пользователя.")
    @Size(min = 3, max = 32, message = "Длина имени пользователя — от 3 до 32 символов.")
    private String username;

    @NotBlank (message = "Укажите адрес вашей электронной почты.")
    @Email (message = "Проверьте правильность адреса электронной почты.")
    private String email;

    @NotBlank(message = "Пожалуйста, придумайте пароль.")
    @Size(min = 8, max = 32, message = "Пароль должен содержать от 8 до 32 символов")
    private String password;

}
