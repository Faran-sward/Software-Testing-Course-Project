package cn.tju.sse.spring_backend.dto.pub.modify.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyRequestDTO;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StoreModifyRequestMapperTest {

    private StoreModifyRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(StoreModifyRequestMapper.class);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = StoreModifyRequestMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "requestToEntity";

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

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToCategoryEntities() throws IOException {
        String testClass = StoreModifyRequestMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "requestToCategoryEntities";

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
    void requestToStoreEntity(String sto_ID, String sto_name, String sto_introduction, String[] categories, MultipartFile stoLicenseImg, MultipartFile[] stoPicture, int expected_stoId, String expected_stoName, String expected_stoIntroduction) {
        // Arrange
        StoreModifyRequestDTO request = StoreModifyRequestDTO.builder()
                .sto_ID(sto_ID)
                .sto_name(sto_name)
                .sto_introduction(sto_introduction)
                .categories(categories)
                .stoLicenseImg(stoLicenseImg)
                .stoPicture(stoPicture)
                .build();

        // Act
        StoreEntity storeEntity = mapper.requestToStoreEntity(request);

        // Assert
        assertThat(storeEntity.getStoId()).isEqualTo(expected_stoId);
        assertThat(storeEntity.getStoName()).isEqualTo(expected_stoName);
        assertThat(storeEntity.getStoIntroduction()).isEqualTo(expected_stoIntroduction);
    }

    @ParameterizedTest
    @MethodSource("readExcelForRequestToCategoryEntities")
    void requestToCategoryEntities(String sto_ID, String sto_name, String sto_introduction, String[] categories, MultipartFile stoLicenseImg, MultipartFile[] stoPicture, int expected_storeId, String[] expected_categories) {
        // Arrange
        StoreModifyRequestDTO request = StoreModifyRequestDTO.builder()
                .sto_ID(sto_ID)
                .sto_name(sto_name)
                .sto_introduction(sto_introduction)
                .categories(categories)
                .stoLicenseImg(stoLicenseImg)
                .stoPicture(stoPicture)
                .build();

        // Act
        StoreCategoriesEntity[] categoryEntities = mapper.requestToCategoryEntities(request);

        // Assert
        assertThat(categoryEntities.length).isEqualTo(expected_categories.length);
        for (int i = 0; i < categoryEntities.length; i++) {
            assertThat(categoryEntities[i].getStoreId()).isEqualTo(expected_storeId);
            assertThat(categoryEntities[i].getComCategory()).isEqualTo(expected_categories[i]);
        }
    }
}
