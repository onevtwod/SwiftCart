package com.swiftcart.inventory.repo;

import com.swiftcart.inventory.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
}
