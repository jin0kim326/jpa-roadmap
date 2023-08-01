package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook("JPA 공부 실습을 위한 책", 17000, 15);

        int ORDER_COUNT = 4;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), ORDER_COUNT);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(findOrder.getTotalPrice()).isEqualTo(17000*ORDER_COUNT);
        assertThat(book.getStockQuantity()).isEqualTo(11);
    }

    @Test
    void 상품주문_재문수량초과() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA 공부 실습을 위한 책", 17000, 15);

        int ORDER_COUNT = 16;

        //when
        assertThatThrownBy(()->orderService.order(member.getId(), book.getId(), ORDER_COUNT))
        .isInstanceOf(NotEnoughStockException.class); //then
    }

    @Test
    void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA 공부 실습을 위한 책", 17000, 15);

        int ORDER_COUNT = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), ORDER_COUNT);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(15);
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("진영");
        member.setAddress(new Address("부산시", "수영광일로", "17241-56"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}