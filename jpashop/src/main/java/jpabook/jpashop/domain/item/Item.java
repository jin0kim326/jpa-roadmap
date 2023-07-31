package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jakarta.persistence.*;
import jpabook.jpashop.exception.NotEnoughStockException;
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

    //==비즈니스 로직==//

    /**
     * 재고 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if ( restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
