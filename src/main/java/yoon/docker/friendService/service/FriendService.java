package yoon.docker.friendService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import yoon.docker.friendService.dto.request.FriendDto;
import yoon.docker.friendService.dto.response.FriendRequestResponse;
import yoon.docker.friendService.dto.response.FriendResponse;
import yoon.docker.friendService.dto.response.MemberResponse;
import yoon.docker.friendService.entity.Friend;
import yoon.docker.friendService.entity.Members;
import yoon.docker.friendService.enums.ExceptionCode;
import yoon.docker.friendService.exception.FriendException;
import yoon.docker.friendService.exception.InternalException;
import yoon.docker.friendService.exception.UnauthorizedException;
import yoon.docker.friendService.repository.FriendRepository;
import yoon.docker.friendService.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    @Value("${SVC_MEMBER}")
    private String memberUrl;
    @Value("${PORT_MEMBER}")
    private String memberPort;

    private final RestTemplate restTemplate;

    private final FriendRepository friendRepository;

    private final MemberRepository memberRepository;

    private FriendResponse toResponse(Friend friend){
        return new FriendResponse(friend.getFromUser().getMemberIdx(), friend.getToUser(), friend.isFriend(), friend.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public boolean isFriend(FriendDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        if(dto.getToUser() == currentMember.getMemberIdx())
            throw new FriendException(ExceptionCode.SELF_REQUEST.getMessage(), ExceptionCode.SELF_REQUEST.getStatus());//본인에게 요청

        Friend friend = friendRepository.findFriendsByFromUserAndToUser(currentMember, dto.getToUser());
        return friend != null && friend.isFriend();
    }

    @Transactional(readOnly = true)
    public MemberResponse getInfo(long idx){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        if(idx == currentMember.getMemberIdx())
            throw new FriendException(ExceptionCode.SELF_REQUEST.getMessage(), ExceptionCode.SELF_REQUEST.getStatus()); // 본인에게 요청

        Friend friend = friendRepository.findFriendsByFromUserAndToUser(currentMember, idx);
        if(friend == null)
            throw new FriendException(ExceptionCode.NOT_A_FRIEND.getMessage(), ExceptionCode.NOT_A_FRIEND.getStatus()); //친구가 아님
        if(!friend.isFriend())
            throw new FriendException(ExceptionCode.FRIEND_REQUEST_NOT_ACCEPTED.getMessage(), ExceptionCode.FRIEND_REQUEST_NOT_ACCEPTED.getStatus()); //친구 요청이 아직 수락되지 않음

        ResponseEntity<MemberResponse> response = restTemplate.getForEntity("http://"+memberUrl+":"+memberPort+"/api/v1/members/"+idx, MemberResponse.class);
        if(response.getStatusCode() == HttpStatus.OK)
            return response.getBody();
        else
            throw new InternalException(ExceptionCode.INTERNAL_SERVER_ERROR.getMessage(), ExceptionCode.INTERNAL_SERVER_ERROR.getStatus());  // 서버 에러
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getFriendsList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        List<Friend> list = friendRepository.findFriendsByFromUserAndIsFriend(currentMember, true);
        List<Long> idxList = new ArrayList<>();
        for(Friend f : list){
            idxList.add(f.getToUser());
        }
        HttpEntity<List<Long>> requestBody = new HttpEntity<>(idxList);
        ResponseEntity<List<MemberResponse>> response = restTemplate.exchange(
                "http://"+memberUrl+":"+memberPort+"/api/v1/members/lists",
                HttpMethod.POST,
                requestBody,
                new ParameterizedTypeReference<List<MemberResponse>>() {});

        if(response.getStatusCode() == HttpStatus.OK)
            return response.getBody();
        else
            throw new InternalException(ExceptionCode.INTERNAL_SERVER_ERROR.getMessage(), ExceptionCode.INTERNAL_SERVER_ERROR.getStatus()); // 서버 에러
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getFriendRequests(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        List<Friend> list = friendRepository.findFriendsByToUser(currentMember.getMemberIdx());
        List<FriendRequestResponse> result = new ArrayList<>();

        for(Friend f: list){
            Members fromUser = f.getFromUser();
            result.add(new FriendRequestResponse(f.getFriendIdx(), fromUser.getEmail()
                    , fromUser.getUsername(), fromUser.getProfile(),f.getCreatedAt()));
        }

        return result;
    }

    @Transactional
    public FriendResponse request(FriendDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();
        long toUser = dto.getToUser();

        if(toUser == currentMember.getMemberIdx())
            throw new FriendException(ExceptionCode.SELF_REQUEST.getMessage(), ExceptionCode.SELF_REQUEST.getStatus()); // 본인에게 요청
        if(!memberRepository.existsMembersByMemberIdx(toUser))
            throw new FriendException(ExceptionCode.FRIEND_NOT_FOUND.getMessage(), ExceptionCode.FRIEND_NOT_FOUND.getStatus()); //해당 유저가 없음
        if(friendRepository.existsByFromUserAndToUser(currentMember, toUser) ||
                friendRepository.existsByFromUserAndToUser(memberRepository.findMembersByMemberIdx(toUser), currentMember.getMemberIdx()))
            throw new FriendException(ExceptionCode.FRIEND_REQUEST_ALREADY_SENT.getMessage(), ExceptionCode.FRIEND_REQUEST_ALREADY_SENT.getStatus());//이미 친구이거나 요청을 보냄

        Friend friend = Friend.builder()
                .fromUser(currentMember)
                .toUser(toUser)
                .build();

        return toResponse(friendRepository.save(friend));
    }

    @Transactional
    public FriendResponse accept(long friendIdx){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();
        Friend friend1 = friendRepository.findFriendsByFriendIdx(friendIdx);

        if(friend1.getToUser() != currentMember.getMemberIdx()) {     //toUser가 아님
            throw new FriendException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus());
        }

        Friend friend2 = Friend.builder()
                .fromUser(memberRepository.findMembersByMemberIdx(friend1.getToUser()))
                .toUser(friend1.getFromUser().getMemberIdx())
                .build();

        friend1.setFriend(true);
        friend2.setFriend(true);

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        return toResponse(friend1);
    }

    @Transactional
    public void decline(long friendIdx){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();
        Friend friend = friendRepository.findFriendsByFriendIdx(friendIdx);

        if(friend.getToUser() != currentMember.getMemberIdx()) {     //toUser가 아님
            throw new FriendException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus());
        }

        friendRepository.delete(friend);
    }

    @Transactional
    public void delete(long memberIdx){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS.getMessage(), ExceptionCode.UNAUTHORIZED_ACCESS.getStatus()); //로그인 되지 않았거나 만료됨

        Members currentMember = (Members) authentication.getPrincipal();

        if(memberIdx == currentMember.getMemberIdx())
            throw new FriendException(ExceptionCode.SELF_REQUEST.getMessage(), ExceptionCode.SELF_REQUEST.getStatus()); // 본인에게 요청

        Members to = memberRepository.findMembersByMemberIdx(memberIdx);

        Friend friend1 = friendRepository.findFriendsByFromUserAndToUser(currentMember, to.getMemberIdx());
        Friend friend2 = friendRepository.findFriendsByFromUserAndToUser(to, currentMember.getMemberIdx());

        friendRepository.delete(friend1);
        friendRepository.delete(friend2);

    }

}
