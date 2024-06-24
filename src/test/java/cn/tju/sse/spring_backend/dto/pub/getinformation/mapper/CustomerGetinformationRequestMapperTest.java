package cn.tju.sse.spring_backend.dto.pub.getinformation.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;

import static org.junit.jupiter.api.Assertions.*;

import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationRequestDTO;
import cn.tju.sse.spring_backend.model.CustomerEntity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

class CustomerGetinformationRequestMapperTest {
    // 提供测试数据
    static Stream<Arguments> provideDataForRequestToEntity() {
        return Stream.of(
                Arguments.of("12345", 12345),  // 输入的 cus_ID, 预期的 entity.cusId
                Arguments.of("67890", 67890)
        );
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = CustomerGetinformationRequestMapperTest.class.getName();
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
//    @MethodSource("provideDataForRequestToEntity")
    @MethodSource("readExcelForRequestToEntity")
    void testRequestToEntityMapping(String inputCusId, int expectedCusId) {
        // 创建DTO对象
        CustomerGetinformationRequestDTO dto = new CustomerGetinformationRequestDTO();
        dto.setCus_ID(inputCusId);

        // 使用映射器转换
        CustomerEntity entity = CustomerGetinformationRequestMapper.INSTANCE.requestToEntity(dto);

        // 断言确保映射正确
        assertNotNull(entity);
        assertEquals(expectedCusId, entity.getCusId());

        // 如果所有断言都通过，返回true
        assertTrue(true);
    }
}
