/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ironcodesoftware.wanderease.model;


public interface Product {

    String F_TITLE = "title";
    String F_PRICE = "price";
    String F_QTY = "quantity";
    String F_DESC = "spec";
    String F_COLOR = "color";
    String F_CATEGORY ="category";
    String F_STATE ="active_state";

    enum state{
        Active,
        Inactive
    }
    
}
