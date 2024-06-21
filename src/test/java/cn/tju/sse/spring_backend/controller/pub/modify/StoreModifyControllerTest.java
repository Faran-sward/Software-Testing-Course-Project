package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.StoreModifyService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreModifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreModifyService storeModifyService;

    private static Stream<TestData> provideTestData() {
        return Stream.of(
                new TestData("123", "New Introduction", "New Store Name", new String[]{"electronics"}, "license1.jpg", new String[]{"picture1.jpg"}, "Success"),
                new TestData("456", "Old Introduction", "Old Store Name", new String[]{"books"}, "license2.jpg", new String[]{"picture2.jpg", "picture3.jpg"}, "Failure")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void testStoreModify(TestData data) throws Exception {
        MockMultipartFile stoLicenseImg = new MockMultipartFile("sto_licenseImg", data.licenseFileName, MediaType.IMAGE_JPEG_VALUE, "license content".getBytes());
        MockMultipartFile[] stoPicture = Stream.of(data.pictureFileNames)
                .map(name -> new MockMultipartFile("sto_picture", name, MediaType.IMAGE_JPEG_VALUE, "picture content".getBytes()))
                .toArray(MockMultipartFile[]::new);

        StoreModifyResponseDTO response = new StoreModifyResponseDTO();
        response.setMessage(data.expectedMessage);

        when(storeModifyService.storeModify(any())).thenReturn(response);

        mockMvc.perform(multipart("/api/pub/modify/store")
                        .file(stoLicenseImg)
                        .file(stoPicture[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("sto_ID", data.stoId)
                        .param("sto_introduction", data.introduction)
                        .param("sto_name", data.storeName)
                        .param("categories", data.categories))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(data.expectedMessage));
    }

    private static class TestData {
        String stoId;
        String introduction;
        String storeName;
        String[] categories;
        String licenseFileName;
        String[] pictureFileNames;
        String expectedMessage;

        TestData(String stoId, String introduction, String storeName, String[] categories, String licenseFileName, String[] pictureFileNames, String expectedMessage) {
            this.stoId = stoId;
            this.introduction = introduction;
            this.storeName = storeName;
            this.categories = categories;
            this.licenseFileName = licenseFileName;
            this.pictureFileNames = pictureFileNames;
            this.expectedMessage = expectedMessage;
        }
    }
}
