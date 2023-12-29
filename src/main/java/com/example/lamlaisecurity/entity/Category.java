package com.example.lamlaisecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    @Column(nullable = false, unique = true)
    private String categoryName;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean status = true;
    @Column(nullable = false)
    private Date timeStamp = new Date();
    @Column(nullable = false)
    @JsonIgnore
    private Boolean isDelete = false;

    @ManyToOne
    @JoinColumn(name= "parentId", referencedColumnName = "categoryId")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Category> children = new ArrayList<>();
}
