package com.elbit.core.services.repository;

import com.elbit.core.services.data.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
