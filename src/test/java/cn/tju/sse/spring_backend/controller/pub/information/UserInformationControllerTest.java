package cn.tju.sse.spring_backend.controller.pub.information;

import cn.tju.sse.spring_backend.dto.pub.information.UserInformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.information.UserInformationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserInformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInformationService userInformationService;  // Mock the service layer

    @ParameterizedTest
    @ValueSource(strings = {"123", "456", "789"})
    public void testUserInformation(String userId) throws Exception {
        UserInformationResponseDTO mockResponse = UserInformationResponseDTO.builder()
                .user_ID(userId)
                .user_phone("1234567890")
                .user_password("password")
                .user_address("1234 Main St")
                .user_balance("1000")
                .user_regTime("2024-01-01")
                .user_type("standard")
                .message("Success")
                .build();

        when(userInformationService.UserInformation(any(UserInformationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/pub/information/user")
                        .param("user_ID", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.user_ID").value(userId))
                .andExpect(jsonPath("$.user_phone").value("1234567890"))
                .andExpect(jsonPath("$.user_password").value("password"))
                .andExpect(jsonPath("$.user_address").value("1234 Main St"))
                .andExpect(jsonPath("$.user_balance").value("1000"))
                .andExpect(jsonPath("$.user_regTime").value("2024-01-01"))
                .andExpect(jsonPath("$.user_type").value("standard"));
    }
}
