package com.elbit.core.services.dto;

import com.elbit.core.services.data.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class CartResponse {
    private double totalPrice;
    private List<Product> products;
}
