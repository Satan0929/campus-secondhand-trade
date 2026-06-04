package com.campus.secondhand.dto;

import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;

public class FavoriteWithProduct {
    private Favorite favorite;
    private Product product;

    public FavoriteWithProduct() {
    }

    public FavoriteWithProduct(Favorite favorite, Product product) {
        this.favorite = favorite;
        this.product = product;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
