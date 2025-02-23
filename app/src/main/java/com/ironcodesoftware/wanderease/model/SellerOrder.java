package com.ironcodesoftware.wanderease.model;

public interface SellerOrder {

    enum State{
        Pending,Processing,Completed
    }

    String F_STATE = "status";
    String F_SELLER = "seller_email";
    String F_ITEMS = "items";
    String F_ID = "order_id";
    String F_COLLECTION = "SellerOrder";
}
