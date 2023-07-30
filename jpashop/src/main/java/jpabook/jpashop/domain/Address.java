package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 값타입은 변경 불가능하게 설계해야함
 * JPA 스펙상 엔티티,임베디드 타입은 자바 기본생성자를 public or protected로 설정해야함
 * pulic 보다는 protected가 안전함
 *
 * -> JPA가 이런 제약을 두는 이유는 JPA구현 라이브러리가 객체를 생성할때 리플렉션 같은 기술을 사용할 수 있도록 지원해야하기때문
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;
 }
