package com.campus.secondhand.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping
    @Transactional
    public Result<Order> create(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.getOrDefault("quantity", 1).toString());
        
        Long buyerId = StpUtil.getLoginIdAsLong();
        Product product = productMapper.selectById(productId);
        
        if (product == null || product.getStatus() != 0) {
            return Result.error("商品不可购买");
        }
        if (product.getStock() < quantity) {
            return Result.error("库存不足");
        }
        
        product.setStock(product.getStock() - quantity);
        if (product.getStock() == 0) {
            product.setStatus(1);
        }
        productMapper.updateById(product);
        
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setUnitPrice(product.getPrice());
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus(0);
        orderMapper.insert(order);
        
        return Result.success(order);
    }

    @PutMapping("/{id}/pay")
    @Transactional
    public Result<Void> pay(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getBuyerId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getBuyerId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @Transactional
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getBuyerId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            return Result.error("订单状态不正确");
        }
        
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStock(product.getStock() + order.getQuantity());
            if (product.getStatus() == 1) {
                product.setStatus(0);
            }
            productMapper.updateById(product);
        }
        
        order.setStatus(3);
        orderMapper.updateById(order);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> myOrders(@RequestParam(defaultValue = "1") Integer pageNum) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, userId);
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> page = new Page<>(pageNum, 10);
        Page<Order> resultPage = orderMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", resultPage.getRecords());
        data.put("total", resultPage.getTotal());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        return Result.success(order);
    }
}
