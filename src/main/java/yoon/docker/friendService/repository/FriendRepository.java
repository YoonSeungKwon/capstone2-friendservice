package yoon.docker.friendService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.docker.friendService.entity.Friend;
import yoon.docker.friendService.entity.Members;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByFromUserAndToUser(Members fromUser, long toUser);

    Friend findFriendsByFromUserAndToUser(Members fromUser, long toUser);

    Friend findFriendsByFriendIdx(long idx);

    List<Friend> findFriendsByToUser(long idx);
    List<Friend> findFriendsByFromUserAndIsFriend(Members members, boolean isFriend);
}
