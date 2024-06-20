package cn.tju.sse.spring_backend.dto.pub.getinformation.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationResponseDTO;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import setest.utils.testCase.TestCase;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.stream.Stream;

class CustomerGetinformationResponseMapperTest {

    public static Stream<Arguments> readExcelForEntityToResponse() throws IOException {
        String testClass = CustomerGetinformationResponseMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "testEntityToResponse";

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
    @MethodSource("readExcelForEntityToResponse")
    void testEntityToResponse(int cusId, String cusNickname, String cusNotes, String cusPaypassword, int cusState,
                              String[] categories, int[] weights,
                              String message, String dtoCusId, String dtoCusNickname, String dtoCusNotes, String dtoCusPaypassword, String dtoCusState, String[] dtoCusLoves) {
        // 构建 CustomerEntity
        CustomerEntity customer = new CustomerEntity(cusId, cusNickname, cusNotes, cusPaypassword, cusState);

        // 构建 CustomerLoveEntity[]
        CustomerLoveEntity[] loves = CustomerLoveEntity.createArray(cusId, categories, weights);

        // 构建 CustomerGetinformationResponseDTO
        CustomerGetinformationResponseDTO expected = new CustomerGetinformationResponseDTO(message, dtoCusId, dtoCusNickname, dtoCusNotes, dtoCusPaypassword, dtoCusState, dtoCusLoves);

        // 执行映射
        CustomerGetinformationResponseDTO actual = CustomerGetinformationResponseMapper.INSTANCE.entityToResponse(customer, loves);

        // 验证结果
        assertNotNull(actual);
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getCus_ID(), actual.getCus_ID());
        assertEquals(expected.getCus_nickname(), actual.getCus_nickname());
        assertEquals(expected.getCus_notes(), actual.getCus_notes());
        assertEquals(expected.getCus_payPassword(), actual.getCus_payPassword());
        assertEquals(expected.getCus_state(), actual.getCus_state());
        assertArrayEquals(expected.getCus_loves(), actual.getCus_loves());

        // 如果所有断言都通过，返回true
        assertTrue(true);
    }
}
