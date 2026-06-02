package com.campus.secondhand.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 0);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getTitle, keyword);
        }
        if ("price".equals(sortBy)) {
            if ("asc".equals(sortOrder)) {
                wrapper.orderByAsc(Product::getPrice);
            } else {
                wrapper.orderByDesc(Product::getPrice);
            }
        } else {
            if ("asc".equals(sortOrder)) {
                wrapper.orderByAsc(Product::getCreateTime);
            } else {
                wrapper.orderByDesc(Product::getCreateTime);
            }
        }
        
        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> resultPage = productMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        data.put("pageNum", resultPage.getCurrent());
        data.put("pageSize", resultPage.getSize());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        return Result.success(product);
    }

    @PostMapping
    public Result<Product> publish(@RequestBody Product product) {
        Long sellerId = StpUtil.getLoginIdAsLong();
        product.setSellerId(sellerId);
        product.setStatus(0);
        productMapper.insert(product);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        Long userId = StpUtil.getLoginIdAsLong();
        Product existing = productMapper.selectById(id);
        if (existing == null || !existing.getSellerId().equals(userId)) {
            return Result.error("无权操作");
        }
        product.setProductId(id);
        productMapper.updateById(product);
        return Result.success();
    }

    @PutMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Product product = productMapper.selectById(id);
        if (product == null || !product.getSellerId().equals(userId)) {
            return Result.error("无权操作");
        }
        product.setStatus(2);
        productMapper.updateById(product);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<Product>> myProducts() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, userId);
        wrapper.orderByDesc(Product::getCreateTime);
        List<Product> list = productMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/categories")
    public Result<List<String>> categories() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Product::getCategory).groupBy(Product::getCategory);
        List<Product> products = productMapper.selectList(wrapper);
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .toList();
        return Result.success(categories);
    }
}
