package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * xxxToOne 관계에 대한 조회
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /**
     * <엔티티를 직접 노출하면 안됨!!>
     * 지연로딩을 피하기 위해 EAGER로 설정하지말자..
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        for (Order order : orders) {
            order.getMember().getName();      //Lazy 강제 초기화
            order.getDelivery().getAddress();//Lazy 강제 초기화
        }

        return orders;
    }
}
