package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.CustomerModifyService;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerModifyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CustomerModifyService customerModifyService;

    private static Stream<Arguments> provideCustomerModifyData() {
        return Stream.of(
                Arguments.of("123", "Nickname1", "Some notes", "password1", new String[]{"books"}, "Success"),
                Arguments.of("456", "Nickname2", "", "password2", new String[]{"electronics", "furniture"}, "Failure"),
                Arguments.of("789", "Nickname3", "More notes", "password3", new String[]{"sports"}, "Update successful")
        );
    }

    @ParameterizedTest
    @MethodSource("provideCustomerModifyData")
    public void testCustomerModify(String cusId, String nickname, String notes, String payPassword, String[] categories, String expectedMessage) throws Exception {
        CustomerModifyRequestDTO request = new CustomerModifyRequestDTO();
        request.setCus_ID(cusId);
        request.setCus_nickname(nickname);
        request.setCus_notes(notes);
        request.setCus_payPassword(payPassword);
        request.setCus_category(categories);

        CustomerModifyResponseDTO response = new CustomerModifyResponseDTO();
        response.setMessage(expectedMessage);

        when(customerModifyService.customerModify(any(CustomerModifyRequestDTO.class))).thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/pub/modify/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}
