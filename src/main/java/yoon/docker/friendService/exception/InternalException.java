package yoon.docker.friendService.exception;

import org.springframework.http.HttpStatus;

public class InternalException extends RuntimeException{

    private final String message;

    private final HttpStatus status;

    public String getMessage(){
        return this.message;
    }

    public HttpStatus getStatus(){
        return this.status;
    }

    public InternalException(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
