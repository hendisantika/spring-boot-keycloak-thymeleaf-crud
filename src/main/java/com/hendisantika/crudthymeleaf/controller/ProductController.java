package com.hendisantika.crudthymeleaf.controller;

import com.hendisantika.crudthymeleaf.model.Product;
import com.hendisantika.crudthymeleaf.repository.ProductRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;

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
    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${is.keycloak.admin.user}")
    private String keycloakAdminUser;

    @Value("${is.keycloak.admin.password}")
    private String keycloakAdminPassword;

    private Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(
                keycloakUrl,
                "master",
                keycloakAdminUser,
                keycloakAdminPassword,
                "admin-cli");
    }

    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RolesAllowed({"admin", "user"})
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
        model.addAttribute("product", productRepository.findById(id));
        return "edit";
    }

    @RolesAllowed("admin")
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") String id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }

//    @GetMapping("logout")
//    public String logout() {
//        Keycloak keycloak = getKeycloakInstance();
//
//        Response result = keycloak.realm(keycloakRealm).deleteSession(jwt.getClaimAsString("session_state"));
//    }
}
