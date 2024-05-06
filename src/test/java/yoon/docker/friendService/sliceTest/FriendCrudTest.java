package yoon.docker.friendService.sliceTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.parameters.P;
import yoon.docker.friendService.entity.Friend;
import yoon.docker.friendService.entity.Members;
import yoon.docker.friendService.repository.FriendRepository;
import yoon.docker.friendService.repository.MemberRepository;

import java.util.List;

@DataJpaTest
public class FriendCrudTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FriendRepository friendRepository;

    @Test
    void Create_Friend(){

        int executeNum = 20;
        int successCount = 0;
        int count = 0;

        for(int i=1; i<=executeNum; i++) {
            Members member = Members.builder().email("test"+i+"@test.com").password("12345678")
                    .name("member"+i).phone("010-1234-5678").build();
            memberRepository.save(member);
        }

        for(int i=1; i<executeNum; i++) {
            for(int j=i+1; j<=executeNum; j++) {
                count++;
                try {
                    Friend friend = Friend.builder()
                            .fromUser(memberRepository.findMembersByMemberIdx(i))
                            .toUser(j)
                            .build();
                    friendRepository.save(friend);
                    successCount++;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        Assertions.assertThat(successCount).isEqualTo(count);
    }

    @Test
    void Update_Test(){

        int executeNum = 20;
        int count = 0;

        for(int i=1; i<=executeNum; i++) {
            Members member = Members.builder().email("test"+i+"@test.com").password("12345678")
                    .name("member"+i).phone("010-1234-5678").build();
            memberRepository.save(member);
        }

        for(int i=1; i<executeNum; i++) {
            for(int j=i+1; j<=executeNum; j++) {
                count++;
                try {
                    Friend friend = Friend.builder()
                            .fromUser(memberRepository.findMembersByMemberIdx(i))
                            .toUser(j)
                            .build();
                    friend.setFriend(true);
                    friendRepository.save(friend);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        for(int i=1; i<=count; i++){
            friendRepository.findFriendsByFriendIdx(i).setFriend(true);
        }

        List<Friend> list = friendRepository.findAll();

        for(int i=1; i<list.size(); i++){
            Assertions.assertThat(list.get(i).isFriend()).isTrue();
        }


    }

    @Test
    void Delete_Friend(){

        int executeNum = 20;
        int successCount = 0;
        int count = 0;

        for(int i=1; i<=executeNum; i++) {
            Members member = Members.builder().email("test"+i+"@test.com").password("12345678")
                    .name("member"+i).phone("010-1234-5678").build();
            memberRepository.save(member);
        }

        for(int i=1; i<executeNum; i++) {
            for(int j=i+1; j<=executeNum; j++) {
                try {
                    Friend friend = Friend.builder()
                            .fromUser(memberRepository.findMembersByMemberIdx(i))
                            .toUser(j)
                            .build();
                    friend.setFriend(true);
                    friendRepository.save(friend);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        Assertions.assertThat(successCount).isEqualTo(count);

        for(int i=1; i<executeNum;i++){
            for(int j=i+1; j<=executeNum ;j++){
                Friend friend = friendRepository.findFriendsByFromUserAndToUser(memberRepository.findMembersByMemberIdx(i),j);
                friendRepository.delete(friend);
            }
        }

        Assertions.assertThat(friendRepository.findAll()).isNullOrEmpty();

    }


}
