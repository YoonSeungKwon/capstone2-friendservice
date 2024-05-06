package yoon.docker.friendService.sliceTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import yoon.docker.friendService.controller.FriendController;
import yoon.docker.friendService.repository.FriendRepository;
import yoon.docker.friendService.service.FriendService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser("tester")
public class FriendApiTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FriendController friendController;

    @MockBean
    FriendService friendService;

    @MockBean
    FriendRepository friendRepository;

    String url = "/api/v1/friends/1";

//        String requestBody = "{\"toUser\":\"1\"}";


    @Test
    void Friend_Info() throws Exception {


        mockMvc.perform(get(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
