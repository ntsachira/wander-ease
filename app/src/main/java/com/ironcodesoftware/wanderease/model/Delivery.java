package com.ironcodesoftware.wanderease.model;

public interface Delivery {
    String F_COLLECTION = "delivery";
    String F_STATUS = "delivery_status";
    String F_ASSIGNED_ON = "assigned_datetime";
    String F_DELIVERED_ON = "delivered_datetime";
    String F_COURIER = "courier";
    String F_ORDER_ID = "order";

    enum State {
        Active,
        Delivered
    }
}
