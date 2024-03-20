package querydsl.basic.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

// dto 만들면 다시 빌드 해서 Q클래스 만들기
@Data
public class MemberTeamDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;
    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}