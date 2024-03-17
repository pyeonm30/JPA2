package querydsl.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.entity.Member;
import querydsl.basic.entity.QMember;
import querydsl.basic.entity.Team;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional

public class QueryDSLTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    // 각각 개별 테스트 실행전에 실행함
    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        System.out.println("=========================");
        // 쿼리문을 문자로 작성
        Member findMember = em.createQuery("select m from Member m where m.username= :username", Member.class)
                .setParameter("username","member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertEquals(findMember.getUsername(),("member1"));
    }



    @Test
    public void startQuerydsl() {
        System.out.println("=========================");
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m"); //별칭 한개 줘야함
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl2() {
        System.out.println("=========================");

        QMember m = new QMember("m"); //별칭 한개 줘야함
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


}
