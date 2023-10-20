package com.mkoner.electronics.express.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "line_items")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LineItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lineItemId;
    @Column(nullable = false)
    private Date createdAt = new Date();
    private Date modifiedAt;
    @Column(nullable = false)
    private int lineItemQuantity;
    @Column(nullable = false)
    private Double lineItemPrice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pk_product")
    private Product product;

    private Double calculateLineItemPrice(){
        if(Objects.nonNull(this.product))
        return this.product.getProductPrice() * this.lineItemQuantity;
        return null;
    }
}