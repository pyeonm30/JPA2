package querydsl.basic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import querydsl.basic.dto.MemberSearchCond;
import querydsl.basic.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCond condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCond condition,
                                         Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCond condition,
                                          Pageable pageable);
}
