package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.AuthUtil;
import com.campus.secondhand.entity.Cart;
import com.campus.secondhand.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @GetMapping
    public Result<List<Cart>> list(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.orderByDesc(Cart::getCreateTime);
        List<Cart> list = cartMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping
    public Result<Cart> add(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Map<String, Object> params) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.getOrDefault("quantity", 1).toString());

        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.eq(Cart::getProductId, productId);
        Cart existing = cartMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            cartMapper.updateById(existing);
            return Result.success(existing);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cartMapper.insert(cart);
        return Result.success(cart);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id, @RequestBody Cart cart) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        Cart existing = cartMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        cart.setCartId(id);
        cartMapper.updateById(cart);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        Cart existing = cartMapper.selectById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error("无权操作");
        }
        cartMapper.deleteById(id);
        return Result.success();
    }
}
