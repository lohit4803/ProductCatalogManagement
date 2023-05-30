package com.example.demo.Controllers;

import com.example.demo.Models.productModel;
import com.example.demo.Repository.categoryRepository;
import com.example.demo.Models.categoryModel;
import com.example.demo.Repository.productRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class categoryController {
    private final categoryRepository categoryRepository;
    private final productRepository productRepository;

    public categoryController(categoryRepository categoryRepository, productRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new categoryModel());
        return "add-category";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute categoryModel category) {
        categoryRepository.save(category);
        return "redirect:/categories";
    }



    @GetMapping
    public String getAllCategories(Model model) {
        List<categoryModel> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/{id}/edit")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Optional<categoryModel> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "edit-category";
        }
        else return "";
    }

    @PostMapping("/{id}/edit")
    public String editCategory(@PathVariable("id") Long id, @ModelAttribute categoryModel category) {
        Optional<categoryModel> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isPresent()) {
            category.setId(id);
            categoryRepository.save(category);
        }
        return "redirect:/categories";
    }

    @GetMapping("/{id}/delete")
    public String deleteCategory(@PathVariable("id") Long id) {
        Optional<categoryModel> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryModel categoryToDelete = category.get();
            List<productModel> productsToDelete = categoryToDelete.getProducts();

            // Deleting products
            for (productModel product : productsToDelete) {
                productRepository.delete(product);
            }

            // Deleting category
            categoryRepository.delete(categoryToDelete);
        }

        return "redirect:/categories";
    }

    @GetMapping("/{id}/products")
    public String viewCategoryProducts(@PathVariable("id") Long id, Model model) {
        Optional<categoryModel> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryModel categoryModel = category.get();
            List<productModel> products = categoryModel.getProducts();
            model.addAttribute("category", categoryModel);
            model.addAttribute("products", products);
            return "products";
        } else {
            return "error";
        }
    }









}




