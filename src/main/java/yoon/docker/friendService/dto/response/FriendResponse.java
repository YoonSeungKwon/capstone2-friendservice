package yoon.docker.friendService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FriendResponse {

    private long fromUser;

    private long toUser;

    private boolean isFriend;

    private LocalDateTime created_at;

}
