package com.mkoner.electronics.express.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column(nullable = false)
    private Date createdAt = new Date();
    private Date modifiedAt;
    @Column(nullable = false, unique = true)
    private String categoryName;
    private int categoryOrder;

    @OneToOne()
    private File file;

    /*
    @JsonIgnoreProperties("category")
    @OneToMany(mappedBy = "category")
    private List<Product> products;
     */


}
