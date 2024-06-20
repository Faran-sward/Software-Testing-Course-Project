package cn.tju.sse.spring_backend.service.pub.getinformation;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.mapper.StoreGetinformationRequestMapper;
import cn.tju.sse.spring_backend.dto.pub.getinformation.mapper.StoreGetinformationResponseMapper;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import cn.tju.sse.spring_backend.repository.pub.getinformation.StoreCategoriesGetinformationRepository;
import cn.tju.sse.spring_backend.repository.pub.getinformation.StoreGetinformationRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreGetinformationServiceTest {

    @Mock
    private StoreCategoriesGetinformationRepository storeCategoriesGetinformationRepository;

    @Mock
    private StoreGetinformationRepository storeGetinformationRepository;

    @InjectMocks
    private StoreGetinformationService storeGetinformationService;

    private final StoreGetinformationResponseMapper storeGetinformationResponseMapper
            = StoreGetinformationResponseMapper.INSTANCE;

    private final StoreGetinformationRequestMapper storeGetinformationRequestMapper
            = StoreGetinformationRequestMapper.INSTANCE;

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = StoreGetinformationServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "storeGetinformationTest";

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
    void storeGetinformationTest(String stoId, boolean storeExists, boolean dbError, String expectedMessage, String expectedStoId) {
        // Arrange
        StoreGetinformationRequestDTO requestDTO = new StoreGetinformationRequestDTO();
        requestDTO.setSto_ID(stoId);

        when(storeGetinformationRepository.existsByStoId(Integer.parseInt(stoId))).thenReturn(storeExists);

        if (storeExists) {
            Optional<StoreEntity> storeEntity = Optional.of(new StoreEntity());

            if (!dbError) {
                storeEntity = createStoreEntity(stoId);
            }
            when(storeGetinformationRepository.findById(Integer.parseInt(stoId))).thenReturn(storeEntity);

            if (expectedMessage.equals("success")) {
                List<StoreCategoriesEntity> categoriesEntities = createStoreCategoriesEntities(stoId);
                when(storeCategoriesGetinformationRepository.findAllByStoreId(Integer.parseInt(stoId))).thenReturn(categoriesEntities);
            }
        }

        // Act
        StoreGetinformationResponseDTO responseDTO = storeGetinformationService.StoreGetinformation(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            assertEquals(expectedStoId, responseDTO.getSto_ID().toString());
        }
    }

    private Optional<StoreEntity> createStoreEntity(String stoId) {
        Random random = new Random();
        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setStoId(Integer.parseInt(stoId));
        storeEntity.setStoName("StoreName" + random.nextInt(1000));
        storeEntity.setStoLicenseimg("img" + random.nextInt(1000));
        storeEntity.setStoIntroduction("intro" + random.nextInt(1000));
        storeEntity.setStoState(random.nextInt(2));
        return Optional.of(storeEntity);
    }

    private List<StoreCategoriesEntity> createStoreCategoriesEntities(String stoId) {
        Random random = new Random();
        int length = random.nextInt(10) + 1;  // 生成随机长度，范围为1到10
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = "Category" + random.nextInt(1000);
        }

        return IntStream.range(0, array.length)
                .mapToObj(i -> {
                    StoreCategoriesEntity categoriesEntity = new StoreCategoriesEntity();
                    categoriesEntity.setStoreId(Integer.parseInt(stoId));
                    categoriesEntity.setComCategory(array[i]);
                    return categoriesEntity;
                }).toList();
    }
}
