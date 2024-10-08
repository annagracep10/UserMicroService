package com.techphantomexample.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String productType;


}
