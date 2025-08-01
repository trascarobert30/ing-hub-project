package com.elbit.core.services.repository;

import com.elbit.core.services.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
