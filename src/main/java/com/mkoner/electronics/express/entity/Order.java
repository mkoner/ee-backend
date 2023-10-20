package com.mkoner.electronics.express.entity;

import com.fasterxml.jackson.annotation.*;
import com.mkoner.electronics.express.enumeration.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "orders")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
/*@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "orderId")*/
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(nullable = false)
    private Date createdAt = new Date();
    private Date modifiedAt;
    private Date orderCancellationDate;
    private OrderStatus orderStatus;
    private Double orderAmount ;

    @Embedded
    private Address shippingAddress;

    @OneToMany()
    @JsonManagedReference
    private List<LineItem> lineItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pk_customer")
    @JsonManagedReference
    private Customer customer;

    public Double calculateOrderAmount(){
        Double amount = 0D;
        if(Objects.nonNull(this.lineItems)) {
            for (LineItem lineItem : this.lineItems) {
                amount += lineItem.getLineItemPrice();
            }
            this.orderAmount = amount;
            return amount;
        }
        return null;
    }

    public void addLineItem(LineItem lineItem){
        lineItems.add(lineItem);
    }

    public void removeLineItem(LineItem lineItem){
        lineItems.remove(lineItem);
    }
}
