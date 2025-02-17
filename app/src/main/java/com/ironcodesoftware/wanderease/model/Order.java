package com.ironcodesoftware.wanderease.model;

public interface Order {
    enum State{
        Pending,
        Processing,
        Completed
    }
    String F_PRICE = "total_price";
    String F_STATE = "order_status";
    String F_DATE = "order_date";
    String F_BUYER = "buyer";
    String F_SELLER = "seller";
    String F_ITEMS = "items";

}
