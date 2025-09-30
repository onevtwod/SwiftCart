package com.swiftcart.inventory.repo;

import com.swiftcart.inventory.domain.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovementEntity, Long> {
}
