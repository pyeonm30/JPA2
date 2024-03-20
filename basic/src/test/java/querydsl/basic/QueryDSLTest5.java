package querydsl.basic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import querydsl.basic.dto.MemberDto;
import querydsl.basic.entity.Member;
import querydsl.basic.entity.QMember;
import querydsl.basic.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static querydsl.basic.entity.QMember.member;

@SpringBootTest
@Transactional

public class QueryDSLTest5 {

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
    public void simpleProjection(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();
        for(String s : result){
            System.out.println("s = " + s);
        }
    }
    @Test
    public void tuplueProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username=" + username);
            System.out.println("age=" + age);
        }
    }

    @Test
    public void findDtoinJPQL(){

        // 결과를 만들때 생성자 호출에서 넘겨줌
        // 프로퍼티 접근 안됨, 무조건 생성자방식만 지원함
        List<MemberDto> result = em.createQuery(
                        "select new querydsl.basic.dto.MemberDto(m.username, m.age) " +
                                "from Member m", MemberDto.class)
                .getResultList();
        for(MemberDto m : result){
            System.out.println("m = " + m);
        }
    }

    // QueryDsl 은 결과 DTO 반환할때 3가지를 지원한다
    // 기본 생성자 필요함

    //프로퍼티 사용
    @Test
    public void findDtoBySetter(){

        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for(MemberDto m : result){
            System.out.println("m = " + m);
        }
    }
    //필드 사용 , getter setter 필요없음
    @Test
    public void findDtoByField(){

        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for(MemberDto m : result){
            System.out.println("m = " + m);
        }
    }
    @Test
    public void findDtoByConstructor(){

        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for(MemberDto m : result){
            System.out.println("m = " + m);
        }

    }

    @Test
    public void distinct(){

        Member member1 = new Member("member1", 50);
        Member member2 = new Member("member2", 60);
        em.persist(member1);
        em.persist(member2);

        List<String> result = queryFactory
                .select(member.username).distinct()
                .from(member)
                .fetch();

        for(String s : result){
            System.out.println("s = " + s);
        }

    }

    @Test
    public void 동적쿼리_BooleanBuilder() throws Exception {
        String usernameParam = "member1";
        Integer ageParam = null;

        // select * from member where username='member1' and age=10;
        List<Member> result1 = searchMember1(usernameParam, ageParam);
        Assertions.assertThat(result1.size()).isEqualTo(1);

        List<Member> result2 = searchMember2(usernameParam, ageParam);
        Assertions.assertThat(result2.size()).isEqualTo(1);


    }
    // 검색 조건 쿼리 검색조건에 null 이 뜨면 동적으로 where 절에서 빼주는 것이 builder
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder(); // new BooleanBuilder(필수값 넣어줄 수도 있음 );
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    // where 절에 직접 넣을 수도 있음 
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond))
                .fetch();
    }
    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }
    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }
}
