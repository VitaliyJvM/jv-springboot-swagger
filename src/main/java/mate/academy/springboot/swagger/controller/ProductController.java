package mate.academy.springboot.swagger.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import mate.academy.springboot.swagger.dto.ProductRequestDto;
import mate.academy.springboot.swagger.dto.ProductResponseDto;
import mate.academy.springboot.swagger.dto.mapper.ProductMapper;
import mate.academy.springboot.swagger.model.Product;
import mate.academy.springboot.swagger.service.ProductService;
import mate.academy.springboot.swagger.util.ParseUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping
    @ApiOperation(value = "Create a new product")
    public ProductResponseDto create(@RequestBody ProductRequestDto requestDto) {
        return productMapper.toDto(productService.save(productMapper.toModel(requestDto)));
    }

    @GetMapping("/{productId}")
    @ApiOperation(value = "Get product by id")
    public ProductResponseDto getById(@PathVariable Long productId) {
        return productMapper.toDto(productService.get(productId));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update product by id")
    public void update(@PathVariable Long id,
            @RequestBody ProductRequestDto requestDto) {
        Product product = productMapper.toModel(requestDto);
        product.setId(id);
        productService.update(product);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Remove product by id")
    public void delete(@PathVariable Long id) {
        productService.remove(id);
    }

    @GetMapping
    @ApiOperation(value = "Get products split in pages")
    public List<ProductResponseDto> findAll(@RequestParam (defaultValue = "10")
            @ApiParam(value = "default value is '10'") Integer count,
            @RequestParam (defaultValue = "0")
            @ApiParam(value = "default value is '0'") Integer page,
            @RequestParam (defaultValue = "id") @ApiParam(value = "default value is 'id'")
                  String sortBy) {
        return productService.findAll(ParseUtil.getPageRequest(count, page, sortBy))
              .stream()
              .map(productMapper::toDto)
              .collect(Collectors.toList());
    }

    @GetMapping("/by-price")
    @ApiOperation(value = "Get filtered products in a defined price range")
    public List<ProductResponseDto> findAllByPriceBetween(@RequestParam
            @ApiParam(value = "Price value 'from'") BigDecimal from,
            @RequestParam @ApiParam(value = "Price value 'to'") BigDecimal to,
            @RequestParam (defaultValue = "10") @ApiParam(value = "Split in count products on a "
                + "page. Default value is '10'") Integer count,
            @RequestParam (defaultValue = "0") @ApiParam(value = "Page number. "
                  + "Default value is '0'") Integer page,
            @RequestParam (defaultValue = "id") @ApiParam(value = "Sorting order. "
                + "Default value is 'id'") String sortBy) {
        return productService.findAllByPriceBetween(from, to,
                    ParseUtil.getPageRequest(count, page, sortBy))
              .stream()
              .map(productMapper::toDto)
              .collect(Collectors.toList());
    }
}
