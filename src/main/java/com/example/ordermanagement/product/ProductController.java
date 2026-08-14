package com.example.ordermanagement.product;

import com.example.ordermanagement.product.dto.ProductForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "products/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute(
                "productForm",
                productService.getForm(id)
        );

        return "products/form";
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("productForm") ProductForm productForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "products/form";
        }

        productService.save(productForm);

        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {

        productService.delete(id);

        return "redirect:/products";
    }
}