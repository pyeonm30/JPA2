package querydsl.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.entity.Member;
import querydsl.basic.entity.QMember;
import querydsl.basic.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static querydsl.basic.entity.QMember.*;

@SpringBootTest
@Transactional

public class QueryDSLTest1 {

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

        System.out.println("=========================");
    }

    @Test
    public void startJPQL() {

        // 쿼리문을 문자로 작성
        Member findMember = em.createQuery("select m from Member m where m.username= :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertEquals(findMember.getUsername(), ("member1"));
    }


    @Test
    public void startQuerydsl() {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m"); //별칭 붙여놓고 사용하기
        // QMember m = QMember.member; // 기본적으로 만든 인스턴스 활용
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl2() {
        // static 변수로 만들어서 사용하기

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl3() {
        // static 변수로 만들어서 사용하기

        // 같은 테이블을 조인할때 이름이 같으면 안되서 별칭 붙여줌
        QMember m1 = new QMember("m1");
        Member findMember = queryFactory
                .select(m1)
                .from(m1)
                .where(m1.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchEx() {
        /*
        member.username.eq("member1") // username = 'member1'
        member.username.ne("member1") //username != 'member1'
        member.username.eq("member1").not() // username != 'member1'
        member.username.isNotNull() //이름이 is not null
        member.age.in(10, 20) // age in (10,20)
        member.age.notIn(10, 20) // age not in (10, 20)
        member.age.between(10,30) //between 10, 30
        member.age.goe(30) // age >= 30
        member.age.gt(30) // age > 30
        member.age.loe(30) // age <= 30
        member.age.lt(30) // age < 30
        member.username.like("member%") //like 검색
        member.username.contains("member") // like ‘%member%’ 검색
        member.username.startsWith("member") //like ‘member%’ 검색

         */
    }


    @Test
    public void searchAndParam() {
        List<Member> result1 = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10), member.age.loe(30))
                .fetch();
        assertThat(result1.size()).isEqualTo(1);
    }
    @Test
    public void fetchTest () {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
        
        fetch.stream().forEach(m -> System.out.println("m = " + m));
        
        //한건 조회 ==> 결과 둘 이면 에러 발생  com.querydsl.core.NonUniqueResultException
//        Member findMember1 = queryFactory
//                .selectFrom(member)
//                .fetchOne();
//
        Member findMember1 = queryFactory
        .selectFrom(member)
        .limit(1).fetchOne();
        System.out.println("findMember1 = " + findMember1);
        //처음 한 건 조회
        Member findMember2 = queryFactory
                .selectFrom(member)
                .fetchFirst();

        System.out.println("findMember2 = " + findMember2);


        //count 쿼리로 변경
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();

        System.out.println("count = " + count);
        
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 60));
        em.persist(new Member("member5", 60));
        em.persist(new Member("member6", 60));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(60))
                .orderBy(member.age.desc(), member.username.desc().nullsLast())
                .fetch();


        result.stream().forEach(m -> System.out.println("m = " + m));

    }

    // 페이징

    //조회 건수 제한
    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 기본은 0부터 시작 :
                .limit(2) //최대 2건 조회
                .fetch();
        result.stream().forEach(m -> System.out.println("m = " + m));
        assertThat(result.size()).isEqualTo(2);


    }

}
