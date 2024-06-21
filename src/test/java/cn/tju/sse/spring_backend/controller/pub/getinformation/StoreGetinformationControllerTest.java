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
    @ValueSource(strings = {"1", "2", "3"})  // 示例商家ID
    public void testStoreGetinformation(String stoId) throws Exception {
        StoreGetinformationResponseDTO mockResponse = new StoreGetinformationResponseDTO();
        mockResponse.setMessage("success");
        mockResponse.setSto_ID(Integer.parseInt(stoId));
        mockResponse.setSto_name("Example Store");
        mockResponse.setSto_introduction("This is an example store.");
        mockResponse.setSto_licenseImg("licenseImg.jpg");
        mockResponse.setSto_state("1");
        mockResponse.setCategories(new String[]{"electronics", "books"});

        when(storeGetinformationService.StoreGetinformation(any(StoreGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/pub/getinformation/store")
                        .param("sto_ID", stoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.sto_ID").value(stoId))
                .andExpect(jsonPath("$.sto_name").value("Example Store"))
                .andExpect(jsonPath("$.sto_introduction").value("This is an example store."))
                .andExpect(jsonPath("$.sto_licenseImg").value("licenseImg.jpg"))
                .andExpect(jsonPath("$.sto_state").value("1"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0]").value("electronics"))
                .andExpect(jsonPath("$.categories[1]").value("books"));
    }
}
