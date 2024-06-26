package yoon.docker.friendService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private long idx;

    private String email;

    private String name;

    private String profile;

    private String createdAt;

    private String updatedAt;

}
