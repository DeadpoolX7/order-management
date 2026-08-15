package com.example.ordermanagement.product;

import com.example.ordermanagement.inventory.Inventory;
import com.example.ordermanagement.inventory.InventoryService;
import com.example.ordermanagement.product.dto.ProductForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public ProductService(ProductRepository productRepository, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found: " + id)
                );
    }

    @Transactional(readOnly = true)
    public ProductForm getForm(Long id) {
        Product product = findById(id);

        ProductForm form = new ProductForm();

        form.setId(product.getId());
        form.setName(product.getName());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setSku(product.getSku());
        form.setCategory(product.getCategory());

        return form;
    }

    public void save(ProductForm form) {

        Product product;

        boolean isNew = form.getId() == null;

        if (isNew) {
            product = new Product();
        } else {
            product = findById(form.getId());
        }

        product.setName(form.getName().trim());
        product.setDescription(
                form.getDescription() == null
                        ? null
                        : form.getDescription().trim()
        );
        product.setPrice(form.getPrice());
        product.setSku(form.getSku().trim().toUpperCase());
        product.setCategory(form.getCategory().trim());

        Product savedProduct = productRepository.save(product);

        if (isNew) {
            inventoryService.createForProduct(savedProduct);
        }
    }

    public void delete(Long id) {
        Product product = findById(id);
        inventoryService.deleteByProductId(id);
        productRepository.delete(product);
    }
}