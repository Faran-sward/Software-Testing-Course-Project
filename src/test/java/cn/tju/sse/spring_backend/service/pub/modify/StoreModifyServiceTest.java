package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyResponseDTO;
import cn.tju.sse.spring_backend.model.CommoditiesCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import cn.tju.sse.spring_backend.repository.pub.modify.CommoditiesCategoriesRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreCategoriesModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreImageModifyRepository;
import cn.tju.sse.spring_backend.repository.obs.ObsOperationTool;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StoreModifyServiceTest {

    @Mock
    private StoreModifyRepository storeModifyRepository;

    @Mock
    private StoreCategoriesModifyRepository storeCategoriesModifyRepository;

    @Mock
    private StoreImageModifyRepository storeImageModifyRepository;

    @Mock
    private CommoditiesCategoriesRepository commoditiesCategoriesRepository;

    @InjectMocks
    private StoreModifyService service;

    public StoreModifyServiceTest() {
        MockitoAnnotations.openMocks(this);  // Ensure all mocks are initialized
    }

    private static Stream<Arguments> provideStoreModifyData() {
        Random random = new Random();
        return Stream.generate(() -> {
            boolean isSuccess = random.nextInt(10) < 8;
            String stoId = String.valueOf(random.nextInt(1000));
            String name = isSuccess ? "ValidStoreName" : "Store AAAAAAAAAAAAAAAAAAAAAAAAA";
            String intro = isSuccess ? "ValidIntroduction" : "";
            String[] categories = isSuccess ? new String[]{"Books"} : new String[]{"Invalid"};
            boolean exists = isSuccess;
            boolean validCategories = isSuccess;
            String expectedMessage = isSuccess ? "success" : (exists ? (validCategories ? "name is too long" : "categories not right") : "store not found");

            return Arguments.of(stoId, name, intro, categories, expectedMessage, exists, validCategories);
        }).limit(100);
    }

    @ParameterizedTest
    @MethodSource("provideStoreModifyData")
    public void testStoreModify(String stoId, String name, String intro, String[] categories, String expectedMessage, boolean exists, boolean validCategories) {
        StoreModifyRequestDTO request = new StoreModifyRequestDTO();
        request.setSto_ID(stoId);
        request.setSto_name(name);
        request.setSto_introduction(intro);
        request.setCategories(categories);
        request.setStoLicenseImg(new MockMultipartFile("license.jpg", new byte[0]));
        request.setStoPicture(new MockMultipartFile[]{new MockMultipartFile("picture.jpg", new byte[0])});

        if (exists) {
            StoreEntity foundStore = new StoreEntity();
            foundStore.setStoId(Integer.parseInt(stoId));
            foundStore.setStoName("Old Name");
            foundStore.setStoIntroduction("Old Intro");

            when(storeModifyRepository.existsById(Integer.parseInt(stoId))).thenReturn(true);
            when(storeModifyRepository.findById(Integer.parseInt(stoId))).thenReturn(Optional.of(foundStore));
        } else {
            when(storeModifyRepository.existsById(Integer.parseInt(stoId))).thenReturn(false);
        }

        lenient().when(commoditiesCategoriesRepository.findAll()).thenReturn(List.of(new CommoditiesCategoriesEntity("Books"),
                new CommoditiesCategoriesEntity("Electronics")));

        StoreModifyResponseDTO response = service.storeModify(request);

        assertEquals(expectedMessage, response.getMessage());
        if (exists && name.length() < 20 && validCategories) {
            verify(storeModifyRepository).save(any(StoreEntity.class));
        }
    }
}
