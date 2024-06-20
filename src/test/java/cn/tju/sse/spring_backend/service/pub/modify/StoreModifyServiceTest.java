package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.mapper.StoreModifyRequestMapper;
import cn.tju.sse.spring_backend.model.CommoditiesCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import cn.tju.sse.spring_backend.model.StoreimageEntity;
import cn.tju.sse.spring_backend.repository.obs.ObsOperationTool;
import cn.tju.sse.spring_backend.repository.pub.modify.CommoditiesCategoriesRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreCategoriesModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreImageModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.StoreModifyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreModifyServiceTest {

    @Mock
    private StoreCategoriesModifyRepository storeCategoriesModifyRepository;

    @Mock
    private StoreModifyRepository storeModifyRepository;

    @Mock
    private StoreImageModifyRepository storeImageModifyRepository;

    @Mock
    private CommoditiesCategoriesRepository commoditiesCategoriesRepository;

    @InjectMocks
    private StoreModifyService storeModifyService;

    @Mock
    private StoreModifyRequestMapper storeModifyRequestMapper;

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = StoreModifyServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "storeModifyTest";

        String filePath = TestFileConfig.fileRoot + testClassName + "/" + testMethodName + TestFileConfig.ext;

        TestCase cases = new TestCase(filePath);
        return cases.getDataList().stream()
                .map(data -> {
                    Object[] allParamsWithResult = new Object[data.getParameters().length + 1];
                    System.arraycopy(data.getParameters(), 0, allParamsWithResult, 0, data.getParameters().length);
                    allParamsWithResult[allParamsWithResult.length - 1] = data.getResult();
                    return Arguments.of(allParamsWithResult);
                });
    }

    @ParameterizedTest
    @MethodSource("readExcelForRequestToEntity")
    void storeModifyTest(String stoId, boolean stoExists, String stoName, String stoIntroduction,
                         String[] categories, MultipartFile stoLicenseImg, MultipartFile[] stoPicture,
                         String expectedMessage, String[] expectedCategories) {
        // Arrange
        StoreModifyRequestDTO requestDTO = new StoreModifyRequestDTO();
        requestDTO.setSto_ID(stoId);
        requestDTO.setSto_name(stoName);
        requestDTO.setSto_introduction(stoIntroduction);
        requestDTO.setCategories(categories);
        requestDTO.setStoLicenseImg(stoLicenseImg);
        requestDTO.setStoPicture(stoPicture);

        when(storeModifyRepository.existsById(Integer.parseInt(stoId))).thenReturn(stoExists);

        if (stoExists) {
            StoreEntity storeEntity = createStoreEntity(stoId, stoName, stoIntroduction);
            lenient().when(storeModifyRequestMapper.requestToStoreEntity(requestDTO)).thenReturn(storeEntity);

            StoreCategoriesEntity[] storeCategoriesEntities = createStoreCategoriesEntities(categories);
            lenient().when(storeModifyRequestMapper.requestToCategoryEntities(requestDTO)).thenReturn(storeCategoriesEntities);

            List<CommoditiesCategoriesEntity> categoryEntities = createCommoditiesCategoriesEntities(expectedCategories);
            lenient().when(commoditiesCategoriesRepository.findAll()).thenReturn(categoryEntities);

            lenient().when(storeModifyRepository.findById(Integer.parseInt(stoId))).thenReturn(Optional.of(storeEntity));

            if ("success".equals(expectedMessage)) {
                when(storeModifyRepository.save(any(StoreEntity.class))).thenReturn(storeEntity);
                when(storeCategoriesModifyRepository.saveAll(anyIterable())).thenReturn(Arrays.asList(storeCategoriesEntities));
            }
        }

        // Act
        StoreModifyResponseDTO responseDTO = storeModifyService.storeModify(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            assertNotNull(responseDTO);

            // 验证保存的StoreEntity
            ArgumentCaptor<StoreEntity> storeCaptor = ArgumentCaptor.forClass(StoreEntity.class);
            verify(storeModifyRepository).save(storeCaptor.capture());
            StoreEntity savedStore = storeCaptor.getValue();
            assertEquals(stoId, String.valueOf(savedStore.getStoId()));
            assertEquals(stoName, savedStore.getStoName());

            // 验证保存的StoreCategoriesEntity
            ArgumentCaptor<Iterable<StoreCategoriesEntity>> categoriesCaptor = ArgumentCaptor.forClass(Iterable.class);
            verify(storeCategoriesModifyRepository).saveAll(categoriesCaptor.capture());
            Iterable<StoreCategoriesEntity> savedCategories = categoriesCaptor.getValue();
            for (StoreCategoriesEntity categoryEntity : savedCategories) {
                assertTrue(Arrays.asList(expectedCategories).contains(categoryEntity.getComCategory()));
            }

            // 验证保存的StoreimageEntity
            ArgumentCaptor<Iterable<StoreimageEntity>> imageCaptor = ArgumentCaptor.forClass(Iterable.class);
            verify(storeImageModifyRepository).saveAll(imageCaptor.capture());
            Iterable<StoreimageEntity> savedImages = imageCaptor.getValue();
            for (StoreimageEntity imageEntity : savedImages) {
                assertEquals(stoId, String.valueOf(imageEntity.getStoId()));
            }
        }
    }

    private StoreEntity createStoreEntity(String stoId, String stoName, String stoIntroduction) {
        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setStoId(Integer.parseInt(stoId));
        storeEntity.setStoName(stoName);
        storeEntity.setStoIntroduction(stoIntroduction);
        // 生成随机长度的Licenseimg字符串
        String licenseImg = generateRandomString(new Random().nextInt(20) + 5); // 长度在5到24之间
        storeEntity.setStoLicenseimg(licenseImg);
        return storeEntity;
    }

    private StoreCategoriesEntity[] createStoreCategoriesEntities(String[] categories) {
        return Arrays.stream(categories)
                .map(category -> {
                    StoreCategoriesEntity storeCategoriesEntity = new StoreCategoriesEntity();
                    storeCategoriesEntity.setComCategory(category);
                    return storeCategoriesEntity;
                }).toArray(StoreCategoriesEntity[]::new);
    }

    private List<CommoditiesCategoriesEntity> createCommoditiesCategoriesEntities(String[] categories) {
        return Arrays.stream(categories)
                .map(CommoditiesCategoriesEntity::new)
                .toList();
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }
}
