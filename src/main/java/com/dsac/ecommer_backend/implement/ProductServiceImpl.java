package com.dsac.ecommer_backend.implement;

import com.dsac.ecommer_backend.exception.ResourceFoundException;
import com.dsac.ecommer_backend.model.Product;
import com.dsac.ecommer_backend.repository.ProductRepository;
import com.dsac.ecommer_backend.service.ProductService;
import com.dsac.ecommer_backend.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @Override
    public Product addProduct(Product product, MultipartFile image) throws ResourceFoundException, IOException {
        Product prod = productRepository.findProductByName(product.getNameProduct());

        if (prod != null) {
            throw new ResourceFoundException("ERROR: Product already exists");
        } else {
            if (image != null && !image.isEmpty()) {
                String imagePath = uploadFileService.copy(image, "product");
                product.setImageProduct(imagePath);
            }
            return productRepository.save(product);
        }
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findProductByName(name);
    }

    @Override
    public List<Product> searchProductsByName(String name, int page, int size) {
        return productRepository.searchProductsByName(name, page, size);
    }

    @Override
    public List<Product> getEnabledProducts(int page, int size) {
        return productRepository.findEnabledProducts(page, size);
    }

    @Override
    public List<Product> getDisabledProducts(int page, int size) {
        return productRepository.findDisabledProducts(page, size);
    }

    @Override
    public List<Product> getProductsByCategory(String category, int page, int size) {
        return productRepository.findProductsByCategory(category, page, size);
    }

    @Override
    public List<Product> getEnabledProductsByCategory(String category, int page, int size) {
        return productRepository.findEnabledProductsByCategory(category, page, size);
    }

    @Override
    public Product getProductById(UUID id) throws ResourceFoundException {
        return productRepository.findById(id).orElseThrow(
                () -> new ResourceFoundException("Product not found"));
    }

    @Override
    public List<Product> getTopSellingProducts(int page, int size) {
        return productRepository.findTopSellingProducts(page, size);
    }

    @Override
    public List<Product> getTopBuyingProducts(int page, int size) {
        return productRepository.findTopBuyingProducts(page, size);
    }

    @Override
    public Product getTopBuyerProduct(UUID idUser) {
        return productRepository.findTopBuyerProduct(idUser);
    }

    @Override
    public Product updateProduct(UUID id, Product product, MultipartFile image) throws ResourceFoundException, IOException {
        Product existingProduct = productRepository.findById(id).
                orElseThrow(() -> new ResourceFoundException("Product not found"));
        Product productWhitSameName = productRepository.findProductByName(product.getNameProduct());

        if (existingProduct == null) {
            throw new ResourceFoundException("Product not found");
        } else if (productWhitSameName != null && !productWhitSameName.getIdProduct().equals(id)) {
            throw new ResourceFoundException("Product already exists");
        } else {
            existingProduct.setNameProduct(product.getNameProduct());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setEnabled(product.isEnabled());
            existingProduct.setCategory(product.getCategory());

            if (image != null && !image.isEmpty()) {
                String imagePath = uploadFileService.copy(image, "product");
                existingProduct.setImageProduct(imagePath);

            }

            return productRepository.save(existingProduct);
        }
    }

    @Override
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
