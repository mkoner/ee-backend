package com.mkoner.electronics.express.entity;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Address {
    private String addressCity;
    private  String addressLine1;
    private String addressLine2;
}
