package yoon.docker.friendService.exception;

import org.springframework.http.HttpStatus;

public class FriendException extends RuntimeException{

    private String message;

    private HttpStatus status;

    public String getMessage(){
        return this.message;
    }

    public HttpStatus getStatus(){
        return this.status;
    }

    public FriendException(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }

}
