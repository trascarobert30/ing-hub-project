package com.elbit.core.services.service;

import com.elbit.core.services.data.Audit;
import com.elbit.core.services.data.Cart;
import com.elbit.core.services.data.Product;
import com.elbit.core.services.data.User;
import com.elbit.core.services.dto.CartResponse;
import com.elbit.core.services.dto.ProductRequest;
import com.elbit.core.services.exceptions.StoreNotFoundException;
import com.elbit.core.services.exceptions.UserNotFoundException;
import com.elbit.core.services.mapper.CartMapper;
import com.elbit.core.services.mapper.ProductMapper;
import com.elbit.core.services.repository.CartRepository;
import com.elbit.core.services.repository.ProductRepository;
import com.elbit.core.services.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreManagementService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;
    private final KafkaProducer kafkaProducer;

    public CartResponse addProduct(ProductRequest productRequest) {
        Product product = productMapper.toEntity(productRequest);
        log.info("Adding product: {}", productRequest);
        User user = getAuthenticatedUser();
        Cart cart = saveCart(user);
        product.setCart(cart);
        productRepository.save(product);
        addProductAndRecalculateTotalPrice(product, cart);
        log.info("Product added successfully");
        sendKafkaMessage("PRODUCT_ADDED", "Product added to cart: " + product.getName() + " for user: " + user.getUsername());
        return cartMapper.toResponse(cart);
    }

    public CartResponse deleteProduct(Long productId) {
        log.info("Deleting product: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new StoreNotFoundException("Product not found with ID: " + productId));
        User user = getAuthenticatedUser();
        Cart cart = user.getCart();
        if (cart == null || cart.getProducts() == null) {
            log.warn("Cart is empty or does not exist for user: {}", user.getUsername());
            throw new StoreNotFoundException("Cart not found for user: " + user.getUsername());
        }
        removeProductAndRecalculateTotalPrice(cart, product);
        cartRepository.save(cart);
        productRepository.delete(product);
        sendKafkaMessage("PRODUCT_DELETED", "Product deleted: " + product.getName() + " for user: " + user.getUsername());
        return cartMapper.toResponse(cart);
    }

    private Cart saveCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart = cartRepository.save(cart);
            user.setCart(cart);
            userRepository.save(user);
        }
        return cart;
    }

    private void removeProductAndRecalculateTotalPrice(Cart cart, Product product) {
        if (cart.getProducts().size() == 1) {
            cart.setProducts(null);
            cart.setTotalPrice(0.0);
        } else {
            List<Product> products = cart.getProducts();
            products.removeIf(prod -> prod.getId().equals(product.getId()));
            cart.setProducts(products);
            double totalPrice = products.stream()
                    .mapToDouble(prod -> prod.getPrice() * prod.getQuantity())
                    .sum();
            cart.setTotalPrice(totalPrice);
        }
    }

    private void addProductAndRecalculateTotalPrice(Product product, Cart cart) {
        List<Product> products = cart.getProducts();
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
        cart.setProducts(products);
        double totalPrice = products.stream()
                .mapToDouble(prod -> prod.getPrice() * prod.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found: " + userDetails.getUsername());
        }
        return user.get();
    }

    private void sendKafkaMessage(String event, String info) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        kafkaProducer.sendMessage("AUDIT-IN", Audit.builder()
                .event(event)
                .date(now.format(formatter))
                .info(info)
                .build());
    }
}
