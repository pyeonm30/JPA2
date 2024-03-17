package querydsl.basic;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.entity.Member;
import querydsl.basic.entity.QMember;
import querydsl.basic.entity.QTeam;
import querydsl.basic.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static querydsl.basic.entity.QMember.member;
import static querydsl.basic.entity.QTeam.team;

@SpringBootTest
@Transactional

public class QueryDSLTest3 {

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


    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() throws Exception {
        QMember member = QMember.member;
        QTeam team = QTeam.team;
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) // join => innerJoin 의미한다
                .where(team.name.eq("teamA"))
                .fetch();

        result.stream().forEach(m -> System.out.println("m = " + m + "t =" + m.getTeam()));

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() throws Exception {
        // 사람 이름이 teamA
        // 연관관겨는 id 만 있으니깐 name 으로도 되는지 test
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));
        List<Member> result = queryFactory
                .select(member)
                .from(member, team) // from 두개를 나열하는 것
                .where(member.username.eq(team.name))
                .fetch();
        result.stream().forEach(m -> System.out.println("m = " + m + "t =" + m.getTeam()));


        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인,
     * 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and
     t.name='teamA'
     */
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        // 이게 결과값이 튜플인 이유는 member 객체 + team 객체가 포함 이라서
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void join_on_filtering1() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .join(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        // 이게 결과값이 튜플인 이유는 member 객체 + team 객체가 포함 이라서
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void join_on_filtering2() throws Exception {

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        // 이게 결과값이 튜플인 이유는 member 객체 + team 객체가 포함 이라서
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }


    /**
     * 2. 연관관계 없는 엔티티 외부 조인
     * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        // 클래스 두개에서 값을 가져와야한다
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("t=" + tuple);
        }
    }


    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchLazyTest() throws Exception {
        // fetch join 테스트할때 안지워주면 결과 보기 어렵다
        // 영속성 컨테스트 초기화 하고 테스트하기
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        System.out.println("findMember = " + findMember);
        // System.out.println("team = " + findMember.getTeam());
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        System.out.println("loaded = " + loaded);
    }

    @Test
    public void fetchJoinUse() throws Exception {
        // fetch join 테스트할때 안지워주면 결과 보기 어렵다
        // 영속성 컨테스트 초기화 하고 테스트하기
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

}
