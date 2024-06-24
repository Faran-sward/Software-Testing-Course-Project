package cn.tju.sse.spring_backend.dto.pub.modify.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyRequestDTO;
import cn.tju.sse.spring_backend.model.UsersEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserModifyRequestMapperTest {

    private UserModifyRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserModifyRequestMapper.class);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = UserModifyRequestMapperTest.class.getName();
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
    void requestToEntity(String user_ID, String user_phone, String user_password, String user_address, int expected_userId, String expected_userPhone, String expected_userPassword, String expected_userAddress) {
        // Arrange
        UserModifyRequestDTO request = UserModifyRequestDTO.builder()
                .user_ID(user_ID)
                .user_phone(user_phone)
                .user_password(user_password)
                .user_address(user_address)
                .build();

        // Act
        UsersEntity usersEntity = mapper.requestToEntity(request);

        // Assert
        assertThat(usersEntity.getUserId()).isEqualTo(expected_userId);
        assertThat(usersEntity.getUserPhone()).isEqualTo(expected_userPhone);
        assertThat(usersEntity.getUserPassword()).isEqualTo(expected_userPassword);
        assertThat(usersEntity.getUserAddress()).isEqualTo(expected_userAddress);
    }
}
