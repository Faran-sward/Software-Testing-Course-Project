package cn.tju.sse.spring_backend.dto.pub.information.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationRequestDTO;
import cn.tju.sse.spring_backend.model.UsersEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserInformationRequestMapperTest {

    private UserInformationRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserInformationRequestMapper.class);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = UserInformationRequestMapperTest.class.getName();
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

    @ParameterizedTest
    @MethodSource("readExcelForRequestToEntity")
    void requestToEntity(String user_ID, int expected_userId) {
        // Arrange
        UserInformationRequestDTO request = UserInformationRequestDTO.builder()
                .user_ID(user_ID)
                .build();

        // Act
        UsersEntity entity = mapper.requestToEntity(request);

        // Assert
        assertThat(entity.getUserId()).isEqualTo(expected_userId);
    }
}
