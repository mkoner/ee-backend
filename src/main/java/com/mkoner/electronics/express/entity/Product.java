package com.mkoner.electronics.express.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(nullable = false)
    private Date createdAt = new Date();
    private Date modifiedAt;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Double productPrice;
    private String productDescription;

    @JsonIgnoreProperties({"products", "file"})
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_category")
    private Category category;

    @OneToMany(mappedBy = "")
    private List<File> files;

    public void addFile(List<File> filesToAdd){
        files.add(0, filesToAdd.get(0));
    }

    public void removeFile(File file){
        files.remove(file);
    }
}
