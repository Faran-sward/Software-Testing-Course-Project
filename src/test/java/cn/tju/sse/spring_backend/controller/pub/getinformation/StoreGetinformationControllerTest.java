package cn.tju.sse.spring_backend.controller.pub.getinformation;

import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.getinformation.StoreGetinformationService;
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
public class StoreGetinformationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreGetinformationService storeGetinformationService;  // Mock the service layer

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
    public void testStoreGetinformationActive(String stoId) throws Exception {
        StoreGetinformationResponseDTO mockResponse = new StoreGetinformationResponseDTO();
        mockResponse.setMessage("success");
        mockResponse.setSto_ID(Integer.parseInt(stoId));
        mockResponse.setSto_name("Example Store " + stoId);
        mockResponse.setSto_introduction("This is an example store " + stoId + ".");
        mockResponse.setSto_licenseImg("licenseImg" + stoId + ".jpg");
        mockResponse.setSto_state("1");
        mockResponse.setCategories(new String[]{"electronics " + stoId, "books " + stoId});

        when(storeGetinformationService.StoreGetinformation(any(StoreGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/pub/getinformation/store")
                        .param("sto_ID", stoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.sto_ID").value(stoId))
                .andExpect(jsonPath("$.sto_name").value("Example Store " + stoId))
                .andExpect(jsonPath("$.sto_introduction").value("This is an example store " + stoId + "."))
                .andExpect(jsonPath("$.sto_licenseImg").value("licenseImg" + stoId + ".jpg"))
                .andExpect(jsonPath("$.sto_state").value("1"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0]").value("electronics " + stoId))
                .andExpect(jsonPath("$.categories[1]").value("books " + stoId));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
            "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"
    })
    public void testStoreGetinformationBanned(String stoId) throws Exception {
        StoreGetinformationResponseDTO mockResponse = new StoreGetinformationResponseDTO();
        mockResponse.setMessage("success");
        mockResponse.setSto_ID(Integer.parseInt(stoId));
        mockResponse.setSto_name("Example Store " + stoId);
        mockResponse.setSto_introduction("This is a banned store " + stoId + ".");
        mockResponse.setSto_licenseImg("bannedLicenseImg" + stoId + ".jpg");
        mockResponse.setSto_state("0");
        mockResponse.setCategories(new String[]{"clothing " + stoId, "accessories " + stoId});

        when(storeGetinformationService.StoreGetinformation(any(StoreGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/pub/getinformation/store")
                        .param("sto_ID", stoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.sto_ID").value(stoId))
                .andExpect(jsonPath("$.sto_name").value("Example Store " + stoId))
                .andExpect(jsonPath("$.sto_introduction").value("This is a banned store " + stoId + "."))
                .andExpect(jsonPath("$.sto_licenseImg").value("bannedLicenseImg" + stoId + ".jpg"))
                .andExpect(jsonPath("$.sto_state").value("0"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0]").value("clothing " + stoId))
                .andExpect(jsonPath("$.categories[1]").value("accessories " + stoId));
    }
}
