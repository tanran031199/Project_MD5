package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Check(constraints = "stock >= 0", name = "stock_check")
    private Integer stock;
    @Check(constraints = "import_price > 0", name = "import_price_check")
    private Double importPrice;
    @Check(constraints = "interest_percent > 0", name = "interest_percent_check")
    private Double interestPercent;
    @Check(constraints = "export_price > 0", name = "export_price_check")
    private Double exportPrice;
    @Column(nullable = false)
    private Boolean status = true;
    @Column(nullable = false)
    private Boolean isDelete = false;
    @Column(nullable = false)
    private Date timeStamp = new Date();

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductImage> productImages;
}
