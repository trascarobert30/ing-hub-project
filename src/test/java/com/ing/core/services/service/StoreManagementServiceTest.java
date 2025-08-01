package com.ing.core.services.service;

import com.ing.core.services.data.Cart;
import com.ing.core.services.data.Product;
import com.ing.core.services.data.User;
import com.ing.core.services.dto.CartResponse;
import com.ing.core.services.dto.ProductRequest;
import com.ing.core.services.dto.ProductResponse;
import com.ing.core.services.exceptions.StoreNotFoundException;
import com.ing.core.services.exceptions.UserNotFoundException;
import com.ing.core.services.mapper.CartMapper;
import com.ing.core.services.mapper.ProductMapper;
import com.ing.core.services.repository.CartRepository;
import com.ing.core.services.repository.ProductRepository;
import com.ing.core.services.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreManagementServiceTest {
    @InjectMocks
    private StoreManagementService storeManagementService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CartMapper cartMapper;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void testAddProductSuccess() {
        ProductRequest productRequest = getProductRequest();
        Product product = getProduct();
        User user = getUser();
        Cart cart = getCart(user);
        CartResponse cartResponse = new CartResponse();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.save(any())).thenReturn(cart);
        lenient().when(userRepository.save(any())).thenReturn(user);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse response = storeManagementService.addProduct(productRequest);

        assertNotNull(response);
        verify(productRepository, times(1)).save(product);
        verify(cartRepository, times(1)).save(cart);
        verify(kafkaProducer, times(1)).sendMessage(eq("AUDIT-IN"), any());
    }

    @Test
    void testUserNotFound() {
        ProductRequest productRequest = getProductRequest();
        Product product = getProduct();
        User user = getUser();
        Cart cart = getCart(user);

        when(userRepository.findByUsername(anyString())).thenThrow(UserNotFoundException.class);
        when(productMapper.toEntity(productRequest)).thenReturn(product);

        assertThrows(UserNotFoundException.class, () -> storeManagementService.addProduct(productRequest));

        verify(productRepository, times(0)).save(product);
        verify(cartRepository, times(0)).save(cart);
    }

    @Test
    void testDeleteProduct() {
        Product product = getProduct();
        User user = getUser();
        Cart cart = getCart(user);
        CartResponse cartResponse = new CartResponse();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.save(any())).thenReturn(cart);
        lenient().when(userRepository.save(any())).thenReturn(user);
        doNothing().when(productRepository).delete(any());
        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

        CartResponse response = storeManagementService.deleteProduct(1L);

        assertNotNull(response);
        verify(productRepository, times(1)).delete(product);
        verify(cartRepository, times(1)).save(cart);
        verify(kafkaProducer, times(1)).sendMessage(eq("AUDIT-IN"), any());
    }

    @Test
    void testProductNotFound() {
        Product product = getProduct();
        User user = getUser();
        Cart cart = getCart(user);

        when(productRepository.findById(product.getId())).thenThrow(StoreNotFoundException.class);

        assertThrows(StoreNotFoundException.class, () -> storeManagementService.deleteProduct(1L));

        verify(productRepository, times(0)).delete(product);
        verify(cartRepository, times(0)).save(cart);
        verify(kafkaProducer, times(0)).sendMessage(eq("AUDIT-IN"), any());
    }

    @Test
    void testFindCart() {
        User user = getUser();
        Cart cart = getCart(user);
        CartResponse cartResponse = new CartResponse();

        when(cartMapper.toResponse(cart)).thenReturn(cartResponse);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        storeManagementService.findCart();

        assertNotNull(cartResponse);
    }

    @Test
    void testFindProductInCart() {
        User user = getUser();
        Cart cart = getCart(user);
        Product product = getProduct();
        cart.setProducts(List.of(product));
        ProductResponse productResponse = new ProductResponse();

        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        storeManagementService.findProductInCart(product.getId());

        assertNotNull(productResponse);
    }

    private ProductRequest getProductRequest() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setPrice(10.0);
        productRequest.setQuantity(2);

        return productRequest;
    }

    private Product getProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(10.0);
        product.setQuantity(2);

        return product;
    }

    private User getUser() {
        User user = new User();
        user.setUsername("testUser");

        return user;
    }

    private Cart getCart(User user) {
        Cart cart = new Cart();
        cart.setProducts(new ArrayList<>());
        user.setCart(cart);

        return cart;
    }
}