package com.nhnacademy.bookstore.purchase.repository;


import com.nhnacademy.bookstore.entity.cart.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByMemberId(Long userId);
}
