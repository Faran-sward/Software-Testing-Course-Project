package cn.tju.sse.spring_backend.dto.pub.getinformation.mapper;

import static org.junit.jupiter.api.Assertions.*;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationRequestDTO;
import cn.tju.sse.spring_backend.model.StoreEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

public class StoreGetinformationRequestMapperTest {

    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = StoreGetinformationRequestMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);
        String testMethodName = "testRequestToEntityMapping";

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
    void testRequestToEntityMapping(String inputStoId, int expectedStoId) {
        // 创建DTO对象
        StoreGetinformationRequestDTO dto = new StoreGetinformationRequestDTO();
        dto.setSto_ID(inputStoId);

        // 使用映射器转换
        StoreEntity entity = StoreGetinformationRequestMapper.INSTANCE.requestToEntity(dto);

        // 断言确保映射正确
        assertNotNull(entity);
        assertEquals(expectedStoId, entity.getStoId());

        // 如果所有断言都通过，返回true
        assertTrue(true);
    }
}
