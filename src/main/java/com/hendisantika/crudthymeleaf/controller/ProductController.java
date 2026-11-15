package com.hendisantika.crudthymeleaf.controller;

import com.hendisantika.crudthymeleaf.model.Product;
import com.hendisantika.crudthymeleaf.repository.ProductRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by IntelliJ IDEA.
 * Project : crud-thymeleaf
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 02/08/18
 * Time: 07.23
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ProductController {
    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RequestMapping(path = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(path = "/2")
    public String index2() {
        return "index2";
    }

    @RolesAllowed("admin")
    @GetMapping("/products/add")
    public String createProduct(Model model) {
        model.addAttribute("product", new Product());
        return "edit";
    }

    @RolesAllowed("admin")
    @PostMapping("products")
    public String saveProduct(Product product) {
        productRepository.save(product);
        return "redirect:/";
    }

    @RolesAllowed({"admin", "user"})
    @GetMapping("/products")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products";
    }

    @RolesAllowed("admin")
    @GetMapping("/products/edit/{id}")
    public String editProduct(Model model, @PathVariable(value = "id") String id) {
        model.addAttribute("product", productRepository.findById(id).orElseThrow());
        return "edit";
    }

    @RolesAllowed("admin")
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") String id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}
