package cn.tju.sse.spring_backend.service.pub.information;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.information.mapper.UserInformationRequestMapper;
import cn.tju.sse.spring_backend.dto.pub.information.mapper.UserInformationResponseMapper;
import cn.tju.sse.spring_backend.model.UsersEntity;
import cn.tju.sse.spring_backend.repository.pub.information.UserInformationRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInformationServiceTest {

    @Mock
    private UserInformationRepository userInformationRepository;

    @InjectMocks
    private UserInformationService userInformationService;

    private final UserInformationRequestMapper userInformationRequestMapper
            = UserInformationRequestMapper.INSTANCE;

    private final UserInformationResponseMapper userInformationResponseMapper
            = UserInformationResponseMapper.INSTANCE;

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = UserInformationServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "userInformationTest";

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
    void userInformationTest(String userId, boolean userExists, boolean dbError, String expectedMessage, String expectedUserId) {
        // Arrange
        UserInformationRequestDTO requestDTO = new UserInformationRequestDTO();
        requestDTO.setUser_ID(userId);

        when(userInformationRepository.existsUsersEntityByUserId(Integer.parseInt(userId))).thenReturn(userExists);

        if (userExists) {
            Optional<UsersEntity> userEntity = Optional.of(new UsersEntity());

            if (!dbError) {
                userEntity = createUserEntity(userId);
            }
            when(userInformationRepository.findById(Integer.parseInt(userId))).thenReturn(userEntity);

            if (expectedMessage.equals("success")) {
                // 没有额外的实体需要创建
            }
        }

        // Act
        UserInformationResponseDTO responseDTO = userInformationService.UserInformation(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            assertEquals(expectedUserId, responseDTO.getUser_ID());
        }
    }

    private Optional<UsersEntity> createUserEntity(String userId) {
        Random random = new Random();
        UsersEntity userEntity = new UsersEntity();
        userEntity.setUserId(Integer.parseInt(userId));
        userEntity.setUserAddress("Address" + random.nextInt(1000));
        userEntity.setUserPhone("Phone" + random.nextInt(1000));
        userEntity.setUserPassword("Password" + random.nextInt(1000));
        userEntity.setUserState(random.nextInt(2));
        userEntity.setUserRegtime(new Date(System.currentTimeMillis() - random.nextInt(1000) * 24L * 60L * 60L * 1000L));
        userEntity.setUserType(random.nextInt(5));  // 假设用户类型在0到4之间
        userEntity.setUserBalance(BigDecimal.valueOf(random.nextDouble() * 10000));  // 随机余额
        return Optional.of(userEntity);
    }
}
