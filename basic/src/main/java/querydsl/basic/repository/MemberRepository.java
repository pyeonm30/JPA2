package querydsl.basic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import querydsl.basic.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom{
    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);

}
