package com.example.ordermanagement.inventory;

import com.example.ordermanagement.inventory.dto.InventoryForm;
import com.example.ordermanagement.product.Product;
import com.example.ordermanagement.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Inventory not found for product: " + productId
                        )
                );
    }

    public Inventory createForProduct(Product product) {

        if (inventoryRepository.existsByProductId(product.getId())) {
            return findByProductId(product.getId());
        }

        Inventory inventory = new Inventory();

        inventory.setProduct(product);
        inventory.setQuantity(0);
        inventory.setReservedQuantity(0);

        return inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public InventoryForm getForm(Long productId) {

        Inventory inventory = findByProductId(productId);

        InventoryForm form = new InventoryForm();

        form.setProductId(inventory.getProduct().getId());
        form.setProductName(inventory.getProduct().getName());
        form.setSku(inventory.getProduct().getSku());
        form.setQuantity(inventory.getQuantity());
        form.setReservedQuantity(inventory.getReservedQuantity());

        return form;
    }

    public void updateStock(InventoryForm form) {

        Inventory inventory = findByProductId(form.getProductId());

        if (form.getReservedQuantity() > form.getQuantity()) {
            throw new IllegalArgumentException(
                    "Reserved quantity cannot exceed total quantity"
            );
        }

        inventory.setQuantity(form.getQuantity());
        inventory.setReservedQuantity(form.getReservedQuantity());

        inventoryRepository.save(inventory);
    }

    public void deleteByProductId(Long productId) {

        Inventory inventory = findByProductId(productId);

        inventoryRepository.delete(inventory);
    }
}