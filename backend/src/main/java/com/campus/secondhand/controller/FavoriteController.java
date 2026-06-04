package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.AuthUtil;
import com.campus.secondhand.dto.FavoriteWithProduct;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public Result<List<FavoriteWithProduct>> list(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        
        List<FavoriteWithProduct> result = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product != null) {
                result.add(new FavoriteWithProduct(favorite, product));
            }
        }
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> add(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Map<String, Object> params) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        Long productId = Long.valueOf(params.get("productId").toString());
        
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getProductId, productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            return Result.success();
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> delete(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long productId) {
        Long userId = AuthUtil.getLoginId(token);
        if (userId == null) {
            return Result.error("未登录");
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
        return Result.success();
    }
}
