package cn.tju.sse.spring_backend.dto.pub.modify.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyRequestDTO;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class CustomerModifyRequestMapperTest {

    static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = CustomerModifyRequestMapperTest.class.getName();
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
    void testRequestToEntityMapping(String cus_ID, String cus_nickname, String cus_notes, String cus_payPassword,
                                    String[] cus_category, int expectedCusId, String expectedCus_nickname,
                                    String expectedCus_notes, String expectedCus_payPassword) {
        // 创建DTO对象
        CustomerModifyRequestDTO dto = new CustomerModifyRequestDTO();
        dto.setCus_ID(cus_ID);
        dto.setCus_nickname(cus_nickname);
        dto.setCus_notes(cus_notes);
        dto.setCus_payPassword(cus_payPassword);
        dto.setCus_category(cus_category);

        // 使用映射器转换
        CustomerEntity entity = CustomerModifyRequestMapper.INSTANCE.requestToEntity(dto);

        // 断言确保映射正确
        assertNotNull(entity);
        assertEquals(expectedCusId, entity.getCusId());
        assertEquals(expectedCus_nickname, entity.getCusNickname());
        assertEquals(expectedCus_notes, entity.getCusNotes());
        assertEquals(expectedCus_payPassword, entity.getCusPaypassword());

        // 如果所有断言都通过，返回true
        assertTrue(true);
    }

    static Stream<Arguments> readExcelForRequestToEntities() throws IOException {
        String testClass = CustomerModifyRequestMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);
        String testMethodName = "testRequestToEntitiesMapping";
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
    @MethodSource("readExcelForRequestToEntities")
    void testRequestToEntitiesMapping(String cus_ID, String cus_nickname, String cus_notes, String cus_payPassword,
                                    String[] cus_category, int expectedCusId, String[] expectedCus_categories) {
        // 创建DTO对象
        CustomerModifyRequestDTO dto = new CustomerModifyRequestDTO();
        dto.setCus_ID(cus_ID);
        dto.setCus_nickname(cus_nickname);
        dto.setCus_notes(cus_notes);
        dto.setCus_payPassword(cus_payPassword);
        dto.setCus_category(cus_category);

        // 使用映射器转换
        CustomerLoveEntity[] entities = CustomerModifyRequestMapper.INSTANCE.requestToLoveEntities(dto);

        // 断言确保映射正确
        for (int i = 0; i < cus_category.length; i++) {
            assertNotNull(entities[i]);
            assertEquals(expectedCusId, entities[i].getCusId());
            assertEquals(expectedCus_categories[i], entities[i].getComCategory());
        }

    }
}
