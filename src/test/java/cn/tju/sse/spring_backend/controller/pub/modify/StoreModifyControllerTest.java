package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.StoreModifyService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreModifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreModifyService storeModifyService;


    private static Stream<TestData> provideTestData() {
        Random random = new Random();
        String[] categoriesPool = {"electronics", "books", "clothing", "food", "toys", "sports"};

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        return IntStream.range(0, 100).mapToObj(i -> {
            String stoId = String.valueOf(random.nextInt(1000));
            String introduction = "Introduction " + stoId;
            String storeName = "Store Name " + stoId;
            String[] categories = getRandomCategories(categoriesPool, random);
            String licenseFileName = "license" + stoId + ".jpg";
            String[] pictureFileNames = getRandomPictureFileNames(stoId, random);

            // 保持8:2的成功和失败比例
            String expectedMessage;
            if (successCount.get() < 80) {
                expectedMessage = "Success";
                successCount.getAndIncrement();
            } else {
                expectedMessage = "Failure";
                failureCount.getAndIncrement();
            }

            return new TestData(stoId, introduction, storeName, categories, licenseFileName, pictureFileNames, expectedMessage);
        }).collect(Collectors.toList()).stream();
    }

    private static String[] getRandomCategories(String[] categoriesPool, Random random) {
        int numCategories = random.nextInt(categoriesPool.length) + 1;
        String[] categories = new String[numCategories];
        for (int i = 0; i < numCategories; i++) {
            categories[i] = categoriesPool[random.nextInt(categoriesPool.length)];
        }
        return categories;
    }

    private static String[] getRandomPictureFileNames(String stoId, Random random) {
        int numPictures = random.nextInt(3) + 1;
        String[] pictureFileNames = new String[numPictures];
        for (int i = 0; i < numPictures; i++) {
            pictureFileNames[i] = "picture" + stoId + "_" + i + ".jpg";
        }
        return pictureFileNames;
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
