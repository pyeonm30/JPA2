package querydsl.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.entity.Hello;
import querydsl.basic.entity.QHello;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BasicApplicationTest {
    @Autowired
    EntityManager em;

    @Test
    void contextLoads(){
        //given
        Hello hello = new Hello();
        em.persist(hello);

        //when
        // 쿼리랑 관련된것은 queryDSL 쓸때는 다 Q타입을 넣어야한다
        QHello qHello = new QHello("h");
        JPAQueryFactory query = new JPAQueryFactory(em);
        Hello result = query
                .selectFrom(qHello)
                .fetchOne();
        //then
        assertThat(result).isEqualTo(hello);


    }
}