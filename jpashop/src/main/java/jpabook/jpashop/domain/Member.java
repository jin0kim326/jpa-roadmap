package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;
    
    @Embedded // TODO: 2023/07/30 내장타입을 쓰는이유?
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") // Order엔티티의 member필드에 의해 매핑되었다.
    private List<Order> orders = new ArrayList<>();

}
