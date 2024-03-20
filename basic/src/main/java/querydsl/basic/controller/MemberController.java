package querydsl.basic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import querydsl.basic.dto.MemberSearchCond;
import querydsl.basic.dto.MemberTeamDto;
import querydsl.basic.repository.MemberJpaRepository;
import querydsl.basic.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCond condition) {
        return memberRepository.search(condition);
    }
    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCond condition, Pageable pageable) {
        System.out.println("=====v2=======");
        return memberRepository.searchPageSimple(condition, pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCond condition, Pageable pageable) {
        System.out.println("=====v3=======");
        return memberRepository.searchPageComplex(condition, pageable);
    }
}