package com.ironcodesoftware.wanderease.model;

public interface Order {
    enum State{
        Pending,
        Processing,
        Delivery_Assigned,
        Out_for_Delivery,
        Delivered;
        public String getName(){
            return this.name().replace("_"," ");
        }
    }
    String F_PRICE = "total_price";
    String F_STATE = "order_status";
    String F_DATE = "order_date";
    String F_BUYER = "buyer";
    String F_SELLER = "seller";
    String F_ITEMS = "items";
    String F_ID = "orderId";
    String F_LOCATION = "location";
    String F_COLLECTION = "Order";

}
