package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {
    @Autowired EntityManager em;

    @Test
    void updateTest() throws Exception {
        //given
        Book book = em.find(Book.class, 1L);

        //변경감지 == Dirty checking
        book.setName("asdasd");

        //then
    }

    /**
     * 준영속 엔티티?
     * 영속성 컨텍스트가 더이상 관리하지 않는 엔티티
     * (DB에 한번 저장되어서 식별자가 존재, 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티로 볼수있음)
     *
     * 준영속 엔티티를 수정하는 방법
     * 1. 변경 감지 기능
     * 2. 병합(merge) 사용 - 준영속 엔티티를 영속 엔티티로 변경
     *
     * 🔥 병합(merge)는 객체의 모든필드를 업데이트하기 때문에 필드에 값이 없는경우 디비에 null로 업데이트해버리는 문제있음!!
     * 🔥 엔티티를 변경할 때는 항상 변경감지 사용!!
     *
     *  + 컨트롤러에서 어설프게 엔티티 생성하지 말자.
     *
     *
     */
}
