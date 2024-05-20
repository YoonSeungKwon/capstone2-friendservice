package yoon.docker.friendService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import yoon.docker.friendService.controller.FriendController;
import yoon.docker.friendService.repository.FriendRepository;
import yoon.docker.friendService.repository.MemberRepository;
import yoon.docker.friendService.security.jwt.JwtProvider;
import yoon.docker.friendService.service.FriendService;

import javax.management.DescriptorKey;

@SpringBootTest
@AutoConfigureMockMvc
class FriendServiceApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FriendController friendController;

    @Autowired
    FriendService friendService;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtProvider jwtProvider;





}
