package yoon.docker.friendService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoon.docker.friendService.dto.request.FriendDto;
import yoon.docker.friendService.dto.response.FriendResponse;
import yoon.docker.friendService.entity.Friend;
import yoon.docker.friendService.entity.Members;
import yoon.docker.friendService.exception.FriendException;
import yoon.docker.friendService.repository.FriendRepository;
import yoon.docker.friendService.repository.MemberRepository;

import java.lang.reflect.Member;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    private final MemberRepository memberRepository;

    private FriendResponse toResponse(Friend friend){
        return new FriendResponse(friend.getFromUser().getMemberIdx(), friend.getToUser(), friend.isFriend(), friend.getCreatedAt());
    }

    public boolean isFriend(long memberIdx){
        Members member = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Friend friend = friendRepository.findFriendsByFromUserAndToUser(member, memberIdx);
        return friend != null && friend.isFriend();
    }

    @Transactional
    public FriendResponse request(FriendDto dto){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long toUser = dto.getToUser();

        if(!memberRepository.existsMembersByMemberIdx(toUser))
            throw new FriendException(null,null); //해당 유저가 없음
        if(friendRepository.existsByFromUserAndToUser(members, toUser) ||
                friendRepository.existsByFromUserAndToUser(memberRepository.findMembersByMemberIdx(toUser), members.getMemberIdx()))
            throw new FriendException(null, null);//이미 친구이거나 요청을 보냄

        Friend friend = Friend.builder()
                .toUser(toUser)
                .build();

        return toResponse(friendRepository.save(friend));
    }

    @Transactional
    public FriendResponse accept(long friendIdx){
        Friend friend1 = friendRepository.findFriendsByFriendIdx(friendIdx);
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
        Friend friend = friendRepository.findFriendsByFriendIdx(friendIdx);
        friendRepository.delete(friend);
    }

    @Transactional
    public void delete(long memberIdx){
        Members from = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members to = memberRepository.findMembersByMemberIdx(memberIdx);

        Friend friend1 = friendRepository.findFriendsByFromUserAndToUser(from, to.getMemberIdx());
        Friend friend2 = friendRepository.findFriendsByFromUserAndToUser(to, from.getMemberIdx());

        friendRepository.delete(friend1);
        friendRepository.delete(friend2);

    }

}
