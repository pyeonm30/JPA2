package querydsl.basic.repository;

import querydsl.basic.dto.MemberSearchCond;
import querydsl.basic.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCond condition);
}
