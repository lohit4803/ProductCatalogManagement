package com.example.demo.Controllers;
import com.example.demo.Models.productModel;
import com.example.demo.Models.categoryModel;
import com.example.demo.Repository.categoryRepository;
import com.example.demo.Repository.productRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class productController {
    private final productRepository productRepository;
    private final categoryRepository categoryRepository;

    public productController(productRepository productRepository, categoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/list")
    public String listProducts(Model model) {
        List<productModel> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categoriesLink", "/categories");

        return "products-list";
    }


    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Optional<productModel> product = productRepository.findById(id);
        product.ifPresent(p -> model.addAttribute("product", p));
        return "product";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new productModel());
        model.addAttribute("categories", categoryRepository.findAll());
        return "add-product";
    }


    @PostMapping("/add")
    public String addProduct(@ModelAttribute productModel product) {
        productModel savedProduct = productRepository.save(product);
        Long categoryId = product.getCategory().getId();
        Optional<categoryModel> optionalCategory = categoryRepository.findById(categoryId);
        optionalCategory.ifPresent(category -> {
            List<productModel> products = category.getProducts();
            products.add(savedProduct);
            category.setProducts(products);
            categoryRepository.save(category);
        });
        return "redirect:/categories";
    }


    @GetMapping("/{id}/edit")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        Optional<productModel> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryRepository.findAll());
            return "edit-product";
        }
        return "redirect:/categories";
    }

    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable("id") Long id, @ModelAttribute productModel updatedProduct) {
        Optional<productModel> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            productModel product = existingProduct.get();
            product.setName(updatedProduct.getName());

            categoryModel currentCategory = product.getCategory();
            categoryModel newCategory = updatedProduct.getCategory();
            if (newCategory != null && !newCategory.equals(currentCategory)) {
                // Remove product from the current category
                if (currentCategory != null) {
                    currentCategory.getProducts().remove(product);
                    categoryRepository.save(currentCategory);
                }

                // Add product to the new category
                newCategory.getProducts().add(product);
                categoryRepository.save(newCategory);

                product.setCategory(newCategory);
            }
            productRepository.save(product);
        }
        return "redirect:/categories";
    }


    @GetMapping("/{id}/delete")
    public String deleteProduct(@PathVariable("id") Long id) {
        Optional<productModel> product = productRepository.findById(id);
        if (product.isPresent()) {
            productModel productToDelete = product.get();
            categoryModel category = productToDelete.getCategory();
            List<productModel> products = category.getProducts();
            products.remove(productToDelete);
            category.setProducts(products);
            categoryRepository.save(category);
            productRepository.delete(productToDelete);
            Long categoryId = category.getId();
            return "redirect:/categories/" + categoryId + "/products";
        }
        return "redirect:/categories";
    }
}
