package cn.tju.sse.spring_backend.controller.pub.getinformation;

import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.getinformation.CustomerGetinformationService;
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
public class CustomerGetinformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerGetinformationService customerGetinformationService;  // Mock the service layer

    @ParameterizedTest
    @ValueSource(strings = {"c123", "c456", "c789"})  // 示例顾客ID
    public void testCustomerGetinformation(String cusId) throws Exception {
        CustomerGetinformationResponseDTO mockResponse = CustomerGetinformationResponseDTO.builder()
                .cus_ID(cusId)
                .cus_nickname("Nickname")
                .cus_notes("Some notes")
                .cus_payPassword("securePassword")
                .cus_state("active")
                .cus_loves(new String[]{"traveling", "shopping"})
                .message("Success")
                .build();

        when(customerGetinformationService.CustomerGetinformation(any(CustomerGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/pub/getinformation/customer")
                        .param("cus_ID", cusId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.cus_ID").value(cusId))
                .andExpect(jsonPath("$.cus_nickname").value("Nickname"))
                .andExpect(jsonPath("$.cus_notes").value("Some notes"))
                .andExpect(jsonPath("$.cus_payPassword").value("securePassword"))
                .andExpect(jsonPath("$.cus_state").value("active"))
                .andExpect(jsonPath("$.cus_loves").isArray())
                .andExpect(jsonPath("$.cus_loves[0]").value("traveling"))
                .andExpect(jsonPath("$.cus_loves[1]").value("shopping"));
    }
}
