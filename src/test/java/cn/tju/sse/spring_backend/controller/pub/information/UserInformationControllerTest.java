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
    @ValueSource(strings = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59", "60",
            "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
            "71", "72", "73", "74", "75", "76", "77", "78", "79", "80",
            "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
            "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"
    })
    public void testUserInformation(String userId) throws Exception {
        UserInformationResponseDTO mockResponse = UserInformationResponseDTO.builder()
                .user_ID(userId)
                .user_phone("123456789" + userId)
                .user_password("password" + userId)
                .user_address("Address " + userId)
                .user_balance(String.valueOf(1000 + Integer.parseInt(userId)))
                .user_regTime("2024-01-" + (Integer.parseInt(userId) % 30 + 1))
                .user_type(userId.equals("50") ? "premium" : "standard")
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
                .andExpect(jsonPath("$.user_phone").value("123456789" + userId))
                .andExpect(jsonPath("$.user_password").value("password" + userId))
                .andExpect(jsonPath("$.user_address").value("Address " + userId))
                .andExpect(jsonPath("$.user_balance").value(String.valueOf(1000 + Integer.parseInt(userId))))
                .andExpect(jsonPath("$.user_regTime").value("2024-01-" + (Integer.parseInt(userId) % 30 + 1)))
                .andExpect(jsonPath("$.user_type").value(userId.equals("50") ? "premium" : "standard"));
    }
}
