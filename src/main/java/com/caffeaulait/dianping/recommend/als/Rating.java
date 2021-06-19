package com.caffeaulait.dianping.recommend.als;

import java.io.Serializable;

public class Rating implements Serializable {

    private int userId;

    private int shopId;

    private int rating;

    public Rating(int userId, int shopId, int rating) {
        this.userId = userId;
        this.shopId = shopId;
        this.rating = rating;
    }

    public static Rating parseRating(String str) {
        str = str.replace("\"", "");
        String[] strArr = str.split(",");
        int userId = Integer.parseInt(strArr[0]);
        int shopId = Integer.parseInt(strArr[1]);
        int rating = Integer.parseInt(strArr[2]);
        return new Rating(userId, shopId, rating);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}