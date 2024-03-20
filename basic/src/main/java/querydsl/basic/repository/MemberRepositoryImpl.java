package querydsl.basic.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import querydsl.basic.dto.MemberSearchCond;
import querydsl.basic.dto.MemberTeamDto;
import querydsl.basic.dto.QMemberTeamDto;
import querydsl.basic.entity.Member;

import java.util.List;

import static querydsl.basic.entity.QMember.member;
import static querydsl.basic.entity.QTeam.team;

//  MemberRepositoryImpl 개발자끼리 이름 정의 함 + 인터페이스 구현체로 사용한다 이런 의미
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<MemberTeamDto> search(MemberSearchCond condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.isEmpty(username) ? null : member.username.eq(username);
    }
    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.isEmpty(teamName) ? null : team.name.eq(teamName);
    }
    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe == null ? null : member.age.goe(ageGoe);
    }
    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe == null ? null : member.age.loe(ageLoe);
    }


    //단순한 페이징, fetchResults() 사용
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCond condition,
                                                Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())  // 몇번째부터 조회할꺼야
                .limit(pageable.getPageSize()) // 몇개까지 조회할꺼야
                .fetchResults();

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    // 복잡한 페이징 => 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
    // 데이터가 많지 않으면 굳이 분리 해서 안해도 됨 데이터가 천개가 넘으면 고민하기
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCond condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // 내가 직접 토탈카운트를 날림
        // join 을 하지 않아도 토탈 카운트에 영향을 미치치않으면
        // fetchResult 가 아니라 각각 날려서 join를 최소화해서
        // 성능 최적화 할 수 있다.

        JPAQuery<Member> countQuery  = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe()));
        countQuery.fetch();

        // return new PageImpl<>(content, pageable, total);
        return PageableExecutionUtils.getPage(content, pageable,
                ()->countQuery.fetchCount());

    }

}
