package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch()); // -> order2개 조회

        // 첫번째주문의 member, delivery 각각 조회
        // 두번째주문의 memeber, delivery 각각 조회
        // 💡 1 + N(회원) + N(배송) 문제
        List<SimpleOrderDto> result = orders.stream().map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    /* 페치 조인 */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream().map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    /* JPA에서 바로 DTO로 조회*/
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        List<OrderSimpleQueryDto> orders = orderRepository.findOrdersDto(new OrderSearch());
        return orders;
    }

    /**
     * V3 와 V4는 서로 우열을 가리기 어려움 (트레이드오프존재)
     *
     * V3:엔티티의 데이터를 모두 조회했기때문에 재사용성이 높음,
     *
     * V4:원하는 데이터를 핏하게 조회했기때문에 성능은 더 좋을수 있으나, 재사용성은 떨어짐 (성능차이는 생각보다 미비)
     *    -> V4는 리포지토리계층이나, 화면이 변경되면 이 리포지토리코드도 변경되어야함...
     *
     * 🔥쿼리 방식 선택 권장순서🔥
     * 1. 우선 엔티티를 DTO로 변환 (필수)
     * 2. 페치 조인으로 성능 최적화 - 대부분의 성능 이슈해결
     * 3. 그래도 안되면 DTO로 직접 조회
     * 4. 최후의 방법은 네이티브SQL, 스프링 JDBC 템플릿을 사용해서 SQL을 직접 사용
     */

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
