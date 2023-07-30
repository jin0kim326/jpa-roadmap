package jpabook.jpashop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class JpashopApplication {
	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
		log.info("✅ server started...");
	}
	/**
	 * 💡 엔티티 설계시 주의점 💡
	 *
	 * 1. 엔티티에는 가급적 Setter를 사용하지 말자.
	 * 2. 모든 연관관계는 지연로딩으로 설정
	 *  - 즉시로딩(EAGER)는 예측이 어려움, 어떤 sql이 실행될지 추적어려움 (1+N문제)
	 *  - 실무에서 모든 연관관계는 지연로딩으로 설정해야함
	 *  - xxxToOne은 default가 EAGER이기 때문에 LAZY로 바꿔야함
	 *  3. 컬렉션은 필드에서 바로 초기화 하는 것이 안전
	 *
	 */
}
