package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.UserModifyService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
@SpringBootTest
@AutoConfigureMockMvc
public class UserModifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserModifyService userModifyService;

    // Method to provide test cases
    private static Stream<Arguments> provideUserModifyData() {
        Random random = new Random();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        return IntStream.range(0, 100).mapToObj(i -> {
            String userId = String.valueOf(random.nextInt(1000));
            String phone = String.format("%010d", random.nextInt(1000000000));
            String password = "password" + userId;
            String address = "Address " + userId;

            // 保持8:2的成功和失败比例
            String message;
            if (successCount[0] < 80) {
                message = "Success";
                successCount[0]++;
            } else {
                message = "Failure due to invalid data";
                failureCount[0]++;
            }

            return Arguments.of(userId, phone, password, address, message);
        }).collect(Collectors.toList()).stream();
    }

    @ParameterizedTest
    @MethodSource("provideUserModifyData")
    public void testUserModify(String userId, String phone, String password, String address, String message) throws Exception {
        // Create a request DTO based on the test data
        UserModifyRequestDTO request = UserModifyRequestDTO.builder()
                .user_ID(userId)
                .user_phone(phone)
                .user_password(password)
                .user_address(address)
                .build();

        // Create a response object to be returned by the mock service
        UserModifyResponseDTO response = new UserModifyResponseDTO();
        response.setMessage(message);

        // Configure mock behavior
        when(userModifyService.UserModify(any(UserModifyRequestDTO.class))).thenReturn(response);

        // Convert request object to JSON
        String requestJson = String.format(
                "{\"user_ID\": \"%s\", \"user_phone\": \"%s\", \"user_password\": \"%s\", \"user_address\": \"%s\"}",
                userId, phone, password, address
        );

        // Perform POST request and validate the results
        mockMvc.perform(post("/api/pub/modify/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(message));
    }
}
