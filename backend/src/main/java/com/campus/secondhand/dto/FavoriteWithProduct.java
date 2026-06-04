package com.campus.secondhand.dto;

import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import lombok.Data;

@Data
public class FavoriteWithProduct {
    private Favorite favorite;
    private Product product;

    public FavoriteWithProduct(Favorite favorite, Product product) {
        this.favorite = favorite;
        this.product = product;
    }
}
