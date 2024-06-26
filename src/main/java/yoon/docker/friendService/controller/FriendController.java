package yoon.docker.friendService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yoon.docker.friendService.dto.request.FriendDto;
import yoon.docker.friendService.dto.response.FriendRequestResponse;
import yoon.docker.friendService.dto.response.FriendResponse;
import yoon.docker.friendService.dto.response.MemberResponse;
import yoon.docker.friendService.entity.Members;
import yoon.docker.friendService.enums.ExceptionCode;
import yoon.docker.friendService.exception.UnauthorizedException;
import yoon.docker.friendService.service.FriendService;

import java.util.List;

@Tag(name="친구관련 API", description = "version1")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService friendService;


    //친구 정보 GET /{memberIdx} -> 친구 여부 확인 후 정보 반환
    @Operation(summary = "친구의 정보를 반환", description = "idx에 해당하는 친구의 정보를 MemberResponse 형태로 반환 친구가 아니거나 본인일 경우 에러 응답")
    @GetMapping("/{memberIdx}")
    public ResponseEntity<MemberResponse> getFriendInfo(@PathVariable long memberIdx){

        MemberResponse result = friendService.getInfo(memberIdx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //친구 목록 GET /list
    @Operation(summary = "친구 목록 반환", description = "친구 목록을 MemberResponse의 List형태로 응답.")
    @GetMapping("/list")
    public ResponseEntity<List<MemberResponse>> getFriendsList(){

        List<MemberResponse> result = friendService.getFriendsList(getUserIndex());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //친구 요청 반환 GET /requests  -> Method WebSocket or Http 선택
    @Operation(summary = "친구 요청 목록 반환", description = "본인에게 친구 요청을 한 회원들을 List 형태로 응답")
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getFriendRequest(){

        List<FriendRequestResponse> result = friendService.getFriendRequests();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //친구 여부 POST /is-friend
    @Operation(summary = "친구 여부 확인", description = "idx에 해당하는 회원이 친구인지 boolean값으로 응답")
    @PostMapping("/is-friend")
    public ResponseEntity<Boolean> checkFriend(@RequestBody FriendDto dto){

        boolean result = friendService.isFriend(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //친구 요청 POST /request 본인한태 안되게 추가
    @Operation(summary = "친구 요청", description = "idx에 해당하는 회원에게 친구요청 전송, 만약 본인이거나 존재하지 않을 경우 에러 응답")
    @PostMapping("/request")
    public ResponseEntity<FriendResponse> requestFriend(@RequestBody FriendDto dto){

        FriendResponse result = friendService.request(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //친구 수락 POST /{friendIdx}/accept -> fromUser와 toUser를 맞바꾼 Friend 엔티티 추가로 저장
    @Operation(summary = "친구 요청 수락", description = "idx에 해당하는 회원이 보낸 친구 요청을 수락, 데이터베이스에 송수신자가 바뀐 데이터 추가 저장, FriendResponse 응답")
    @PostMapping("/accept/{friendIdx}")
    public ResponseEntity<FriendResponse> acceptFriend(@PathVariable long friendIdx){

        FriendResponse result = friendService.accept(friendIdx, getUserIndex());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //친구 거절 POST /{friendIdx}/decline -> Friend 엔티티 삭제
    @Operation(summary = "친구 요청 거절", description = "idx에 해당하는 회원이 보낸 친구 요청 거절, Friend 데이터 삭제 후, NO_CONTENT 응답")
    @PostMapping("/decline/{friendIdx}")
    public ResponseEntity<?> declineFriend(@PathVariable long friendIdx){

        friendService.decline(friendIdx);

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    //친구 삭제 DELETE /{idx}
    @Operation(summary = "친구 삭제", description = "idx에 해당하는 회원을 친구에서 삭제, NO_CONTENT 응답")
    @DeleteMapping("/{memberIdx}")
    public ResponseEntity<?> deleteFriend(@PathVariable long memberIdx){

        friendService.delete(memberIdx, getUserIndex());

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    private long getUserIndex(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus());

        Members members = (Members) authentication.getPrincipal();

        return members.getMemberIdx();
    }

}
