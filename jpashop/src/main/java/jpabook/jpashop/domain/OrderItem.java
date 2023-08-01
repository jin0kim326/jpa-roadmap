package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //createOrderItem 이라는 별도 메소드를 제공하기때문에 막는다.
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 당시 가격
    private int count;

    protected void setItem(Item item) {
        this.item = item;
    }

    protected void setOrder(Order order) {
        this.order = order;
    }

    protected void setOrderPrice(int orderPrice) { this.orderPrice = orderPrice; }

    protected void setCount(int count) {
        this.count = count;
    }

    //== 생성 메서드 ==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //== 비즈니스 로직 ==//
    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
