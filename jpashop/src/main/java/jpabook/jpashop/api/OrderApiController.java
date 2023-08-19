package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * 권장방식
 * 1. 엔티티 조회 방식으로 우선 접근
 *  -> 페치조인으로 쿼리 수를 최적화
 *  -> 컬렉션 최적화
 *   (페이징필요한경우 : hibernate.default_batch_fetch_size, @BatchSize 최적화)
 *   (페이징필요없는경우 : 페치 조인 사용)
 * 2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식
 * 3. DTO 조회 방식으로 해결이 안되면 NativeSQL / 스프링 JdbcTemplate
 *
 */

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return collect;
    }

    /**
     * 페치 조인으로 SQL이 한번만 실행됨
     *
     * distinct
     * 1대다 조인에서 데이터 row증가 -> order엔티티수도 증가
     * distinct 를 사용하면 중복조회 되는것을 막아줌 (스프링 3버전 이상부터는 Hibernate6 버전채용 -> distinct 자동적용됨)
     *
     * 🔥 단점 🔥
     * 1대다 페치조인은 "페이징" 불가
     * @return
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return collect;
    }

    /**
     * V3.1 페이징과 한계 돌파
     * 컬렉션을 페치 조인하면 페이징이 불가능하다.
     * Order를 기준으로 페이징 하고 싶은데 다(N)인 OrderItem을 조인하면 OrderItem이 기준이 되어버림.
     *
     * hibernate.default_batch_fatch_size, @BatchSize 옵션사용
     * -> 컬렉션을 갯수만큼 쿼리를 호출하는것이아니라, 위 설정값의 수만큼 in절로 한번에 조회한다.
     * -> 1 + N -> 1 + 1 로 최적화 된다.
     *
     * * 옵션값은 100 ~ 1000 사이로, WAS와 DB가 순간부하를 버틸수있는 갯수로 상황에 맞게 설정하는것이 좋음
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_Page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return collect;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    /**
     * v6은 order데이터가 중복되어 나옴.
     * => OrderFlatDto를 OrderQueryDto로 메모리에서 발라내면됨
     *
     * 장점: 쿼리 한방
     * 단점 : 오더 기준으로 페이징 불가
     *       코드 복잡
     * */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> allByDtoFlat = orderQueryRepository.findAllByDto_flat();

        List<OrderQueryDto> result = allByDtoFlat.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
        return result;
    }


    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }

}
