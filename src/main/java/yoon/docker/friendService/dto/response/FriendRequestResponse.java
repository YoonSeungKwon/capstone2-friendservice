package yoon.docker.friendService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FriendRequestResponse {

    private long friendIdx;

    private String email;

    private String username;

    private String profile;

    private LocalDateTime created_At;


}
