package cn.tju.sse.spring_backend.dto.pub.getinformation.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationResponseDTO;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

class StoreGetinformationResponseMapperTest {

    static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = StoreGetinformationResponseMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "testEntityToResponseMapping";

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
    void testEntityToResponseMapping(int sto_ID, String sto_name, String sto_introduction, String sto_licenseImg,
                                     int sto_state, String[] categories, String message, Integer dtoSto_ID,
                                     String dtoSto_name, String dtoSto_introduction,
                                     String dtoSto_licenseImg, String dtoSto_state, String[] dtoCategories) {
        // 创建 StoreEntity 对象
        StoreEntity store = new StoreEntity(sto_ID, sto_name, sto_introduction, sto_licenseImg, sto_state);

        // 创建 StoreCategoriesEntity 数组
        StoreCategoriesEntity[] storeCategoriesEntities = Arrays.stream(categories)
                .map(category -> new StoreCategoriesEntity(sto_ID, category))
                .toArray(StoreCategoriesEntity[]::new);

        // 使用映射器转换
        StoreGetinformationResponseDTO actual = StoreGetinformationResponseMapper.INSTANCE.entityToResponse(store, storeCategoriesEntities);

        // 断言确保映射正确
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(message, actual.getMessage());
        Assertions.assertEquals(dtoSto_ID, actual.getSto_ID());
        Assertions.assertEquals(dtoSto_name, actual.getSto_name());
        Assertions.assertEquals(dtoSto_introduction, actual.getSto_introduction());
        Assertions.assertEquals(dtoSto_licenseImg, actual.getSto_licenseImg());
        Assertions.assertEquals(dtoSto_state, actual.getSto_state());
        Assertions.assertArrayEquals(dtoCategories, actual.getCategories());
    }
}
