package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   //상속관계설정 : 싱글테이블전략 사용
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Item {    // TODO: 2023/07/30 abstract 를 사용하는 이유 ?
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    
}
