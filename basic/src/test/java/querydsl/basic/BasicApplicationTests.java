package querydsl.basic;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.entity.Hello;

@SpringBootTest
@Transactional
class BasicApplicationTests {
    @Autowired
    EntityManager em;
    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);
//
//        JPAQueryFactory query = new JPAQueryFactory(em);
//        QHello qHello = QHello.hello;
//
//        Hello result = query
//                .selectFrom(qHello)
//                .fetchOne();

//        assertThat(result).isEqualTo(hello);
//        assertThat(result.getId()).isEqualTo(hello.getId());

    }

}
