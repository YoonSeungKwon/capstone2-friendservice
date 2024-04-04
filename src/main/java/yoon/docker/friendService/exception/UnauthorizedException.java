package yoon.docker.friendService.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException{
    private final String message;

    private final HttpStatus status;

    public String getMessage(){
        return this.message;
    }

    public HttpStatus getStatus(){
        return this.status;
    }

    public UnauthorizedException(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
