package yoon.docker.friendService.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yoon.docker.friendService.enums.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<String> UnauthorizedError(UnauthorizedException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler({FriendException.class})
    public ResponseEntity<String> FriendError(FriendException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler({InternalException.class})
    public ResponseEntity<String> InternalException(InternalException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }


}
