package yoon.docker.friendService.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //친구 관련 에러코드

    NOT_A_FRIEND("친구가 아닙니다.", HttpStatus.BAD_REQUEST),

    ALREADY_FRIENDS("이미 친구입니다.", HttpStatus.BAD_REQUEST),

    FRIEND_REQUEST_NOT_ACCEPTED("친구 요청이 수락되지 않았습니다.", HttpStatus.BAD_REQUEST),

    FRIEND_REQUEST_ALREADY_SENT("이미 친구 요청을 보냈습니다.", HttpStatus.BAD_REQUEST),

    FRIEND_NOT_FOUND("친구를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    SELF_REQUEST("자기 자신에게 요청할 수 없습니다.", HttpStatus.FORBIDDEN),

    //인증 에러

    UNAUTHORIZED_ACCESS("인증되지 않은 접근입니다.", HttpStatus.UNAUTHORIZED),

    //서비스 호출 에러

    INTERNAL_SERVER_ERROR("알 수 없는 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    ;


    private final String message;

    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }


}
