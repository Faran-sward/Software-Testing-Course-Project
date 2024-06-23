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
    @ValueSource(strings = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59", "60",
            "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
            "71", "72", "73", "74", "75", "76", "77", "78", "79", "80"
    })
    public void testCustomerGetinformationActive(String cusId) throws Exception {
        CustomerGetinformationResponseDTO mockResponse = CustomerGetinformationResponseDTO.builder()
                .cus_ID(cusId)
                .cus_nickname("Nickname " + cusId)
                .cus_notes("Some notes for customer " + cusId)
                .cus_payPassword("securePassword" + cusId)
                .cus_state("active")
                .cus_loves(new String[]{"traveling " + cusId, "shopping " + cusId})
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
                .andExpect(jsonPath("$.cus_nickname").value("Nickname " + cusId))
                .andExpect(jsonPath("$.cus_notes").value("Some notes for customer " + cusId))
                .andExpect(jsonPath("$.cus_payPassword").value("securePassword" + cusId))
                .andExpect(jsonPath("$.cus_state").value("active"))
                .andExpect(jsonPath("$.cus_loves").isArray())
                .andExpect(jsonPath("$.cus_loves[0]").value("traveling " + cusId))
                .andExpect(jsonPath("$.cus_loves[1]").value("shopping " + cusId));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
            "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"
    })
    public void testCustomerGetinformationBanned(String cusId) throws Exception {
        CustomerGetinformationResponseDTO mockResponse = CustomerGetinformationResponseDTO.builder()
                .cus_ID(cusId)
                .cus_nickname("Nickname " + cusId)
                .cus_notes("Some notes for customer " + cusId)
                .cus_payPassword("securePassword" + cusId)
                .cus_state("banned")
                .cus_loves(new String[]{"gaming " + cusId, "reading " + cusId})
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
                .andExpect(jsonPath("$.cus_nickname").value("Nickname " + cusId))
                .andExpect(jsonPath("$.cus_notes").value("Some notes for customer " + cusId))
                .andExpect(jsonPath("$.cus_payPassword").value("securePassword" + cusId))
                .andExpect(jsonPath("$.cus_state").value("banned"))
                .andExpect(jsonPath("$.cus_loves").isArray())
                .andExpect(jsonPath("$.cus_loves[0]").value("gaming " + cusId))
                .andExpect(jsonPath("$.cus_loves[1]").value("reading " + cusId));
    }
}
