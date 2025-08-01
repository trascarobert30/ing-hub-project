package com.ing.core.services.controller;

import com.ing.core.services.dto.CartResponse;
import com.ing.core.services.dto.ProductRequest;
import com.ing.core.services.dto.ProductResponse;
import com.ing.core.services.service.StoreManagementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreManagementController {
    private final StoreManagementService storeManagementService;

    @PostMapping("/addProduct")
    @Operation(summary = "Add a product to the cart")
    public ResponseEntity<CartResponse> addProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(storeManagementService.addProduct(productRequest));
    }

    @DeleteMapping("/deleteProduct")
    @Operation(summary = "Delete a product from the cart")
    public ResponseEntity<CartResponse> deleteProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(storeManagementService.deleteProduct(productId));
    }

    @GetMapping("/findCart")
    @Operation(summary = "Find the cart for the authenticated user")
    public ResponseEntity<CartResponse> findCart() {
        return ResponseEntity.ok(storeManagementService.findCart());
    }

    @GetMapping("/findProductInCart")
    @Operation(summary = "Find a product in the cart by  ID")
    public ResponseEntity<ProductResponse> findProductInCart(@RequestParam Long productId) {
        return ResponseEntity.ok(storeManagementService.findProductInCart(productId));
    }


    @PutMapping("/changePrice")
    @Operation(summary = "Change the price of a product in the cart")
    public ResponseEntity<CartResponse> changePrice(@RequestParam Long productId, @RequestParam double newPrice) {
        return ResponseEntity.ok(storeManagementService.changePrice(productId, newPrice));
    }
}
