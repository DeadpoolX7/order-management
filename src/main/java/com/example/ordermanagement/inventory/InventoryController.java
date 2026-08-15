package com.example.ordermanagement.inventory;

import com.example.ordermanagement.inventory.dto.InventoryForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String listInventory(Model model) {

        model.addAttribute(
                "inventories",
                inventoryService.findAll()
        );

        return "inventory/list";
    }

    @GetMapping("/{productId}/edit")
    public String showEditForm(
            @PathVariable Long productId,
            Model model
    ) {

        model.addAttribute(
                "inventoryForm",
                inventoryService.getForm(productId)
        );

        return "inventory/form";
    }

    @PostMapping("/save")
    public String updateInventory(
            @Valid @ModelAttribute("inventoryForm") InventoryForm inventoryForm,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return "inventory/form";
        }

        try {
            inventoryService.updateStock(inventoryForm);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject(
                    "inventory.invalid",
                    exception.getMessage()
            );

            return "inventory/form";
        }

        return "redirect:/inventory";
    }
}