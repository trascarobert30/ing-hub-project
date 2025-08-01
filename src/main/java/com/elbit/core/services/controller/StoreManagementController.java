package com.elbit.core.services.controller;

import com.elbit.core.services.dto.CartResponse;
import com.elbit.core.services.dto.ProductRequest;
import com.elbit.core.services.service.StoreManagementService;
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
}
