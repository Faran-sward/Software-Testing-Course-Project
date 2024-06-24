package cn.tju.sse.spring_backend.dto.pub.information.mapper;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationResponseDTO;
import cn.tju.sse.spring_backend.model.UsersEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UserInformationResponseMapperTest {

    private UserInformationResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserInformationResponseMapper.class);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForEntityToResponse() throws IOException {
        String testClass = UserInformationResponseMapperTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "entityToResponse";

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
    void entityToResponse(int userId, String userPhone, String userPassword, String userAddress, BigDecimal userBalance,
                          Date userRegtime, int userType, String expected_user_ID, String expected_user_phone,
                          String expected_user_password, String expected_user_address, String expected_user_balance,
                          String expected_user_regTime, String expected_user_type) {
        // Arrange
        UsersEntity userEntity = UsersEntity.builder()
                .userId(userId)
                .userPhone(userPhone)
                .userPassword(userPassword)
                .userAddress(userAddress)
                .userBalance(userBalance)
                .userRegtime(userRegtime)
                .userType(userType)
                .build();

        // Act
        UserInformationResponseDTO response = mapper.entityToResponse(userEntity);

        // Assert
        assertThat(response.getUser_ID()).isEqualTo(expected_user_ID);
        assertThat(response.getUser_phone()).isEqualTo(expected_user_phone);
        assertThat(response.getUser_password()).isEqualTo(expected_user_password);
        assertThat(response.getUser_address()).isEqualTo(expected_user_address);
        assertThat(response.getUser_balance()).isEqualTo(expected_user_balance);
        assertThat(response.getUser_regTime()).isEqualTo(expected_user_regTime);
        assertThat(response.getUser_type()).isEqualTo(expected_user_type);
    }
}
