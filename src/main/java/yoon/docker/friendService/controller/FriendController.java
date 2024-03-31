package yoon.docker.friendService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoon.docker.friendService.dto.request.FriendDto;
import yoon.docker.friendService.dto.response.FriendResponse;
import yoon.docker.friendService.service.FriendService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;


    //친구 정보 GET /{memberIdx} -> 친구 여부 확인 후 정보 반환
    @GetMapping("/{memberIdx}")
    public ResponseEntity<?> getFriendInfo(@PathVariable long memberIdx){
        return null;
    }

    //친구 목록 GET /list
    @GetMapping("/list")
    public ResponseEntity<?> getFriendsList(){
        return null;
    }

    //친구 요청 반환 GET /requests  -> Method WebSocket or Http 선택
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendRequest(){
        return null;
    }


    //친구 여부 POST /is-friend
    @PostMapping("/is-friend")
    public ResponseEntity<Boolean> checkFriend(@RequestBody long memberIdx){

        boolean result = friendService.isFriend(memberIdx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //친구 요청 POST /request
    @PostMapping("/request")
    public ResponseEntity<FriendResponse> requestFriend(@RequestBody FriendDto dto){

        FriendResponse result = friendService.request(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //친구 수락 POST /{friendIdx}/accept -> fromUser와 toUser를 맞바꾼 Friend 엔티티 추가로 저장
    @PostMapping("/accept/{friendIdx}")
    public ResponseEntity<FriendResponse> acceptFriend(@PathVariable long friendIdx){

        FriendResponse result = friendService.accept(friendIdx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //친구 거절 POST /{friendIdx}/decline -> Friend 엔티티 삭제
    @PostMapping("/decline/{friendIdx}")
    public ResponseEntity<?> declineFriend(@PathVariable long friendIdx){

        friendService.decline(friendIdx);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    //친구 삭제 DELETE /{idx}
    @DeleteMapping("/{memberIdx}")
    public ResponseEntity<?> deleteFriend(@PathVariable long memberIdx){

        friendService.delete(memberIdx);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
