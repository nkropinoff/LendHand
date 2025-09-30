package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.dto.ProductCreationDto;
import com.lendhand.app.lendhandservice.entity.Product;
import com.lendhand.app.lendhandservice.entity.User;
import com.lendhand.app.lendhandservice.exception.UserNotFoundException;
import com.lendhand.app.lendhandservice.repository.ProductRepository;
import com.lendhand.app.lendhandservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Product createProduct(Long ownerId, ProductCreationDto productCreationDto) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Пользователь с таким id не существует!"));
        Product product = new Product(
                productCreationDto.getTitle(),
                productCreationDto.getDescription(),
                productCreationDto.getPrice(),
                productCreationDto.getCategory(),
                owner
        );
        return productRepository.save(product);
    }
}
