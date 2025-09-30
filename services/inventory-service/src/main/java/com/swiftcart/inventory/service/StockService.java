package com.swiftcart.inventory.service;

import com.swiftcart.inventory.domain.InventoryMovementEntity;
import com.swiftcart.inventory.domain.ProductEntity;
import com.swiftcart.inventory.repo.InventoryMovementRepository;
import com.swiftcart.inventory.repo.ProductRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final StringRedisTemplate redis;

    public StockService(ProductRepository productRepository,
            InventoryMovementRepository movementRepository,
            StringRedisTemplate redis) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.redis = redis;
    }

    @Transactional
    public void decrement(String sku, int qty) {
        ProductEntity product = productRepository.findById(sku)
                .orElseGet(() -> {
                    ProductEntity p = new ProductEntity();
                    p.setSku(sku);
                    p.setName(sku);
                    p.setAvailableStock(0);
                    return p;
                });
        int newStock = Math.max(0, product.getAvailableStock() - qty);
        product.setAvailableStock(newStock);
        productRepository.save(product);

        InventoryMovementEntity m = new InventoryMovementEntity();
        m.setSku(sku);
        m.setDelta(-qty);
        m.setReason("order_confirmed");
        movementRepository.save(m);

        redis.opsForValue().set("available_stock:" + sku, String.valueOf(newStock));
    }
}
