package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.mapper.UserModifyRequestMapper;
import cn.tju.sse.spring_backend.model.UsersEntity;
import cn.tju.sse.spring_backend.repository.pub.modify.UserModifyRepository;
import cn.tju.sse.spring_backend.utils.SecurityUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserModifyServiceTest {

    @Mock
    private UserModifyRepository userModifyRepository;

    @InjectMocks
    private UserModifyService userModifyService;

    private final UserModifyRequestMapper userModifyRequestMapper
            = UserModifyRequestMapper.INSTANCE;

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = UserModifyServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "userModifyTest";

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
    void userModifyTest(String userId, boolean userExists, String userPhone, String userPassword, String userAddress, String expectedMessage) {
        // Arrange
        UserModifyRequestDTO requestDTO = new UserModifyRequestDTO();
        requestDTO.setUser_ID(userId);
        requestDTO.setUser_phone(userPhone);
        requestDTO.setUser_password(userPassword);
        requestDTO.setUser_address(userAddress);

        when(userModifyRepository.existsByUserId(Integer.parseInt(userId))).thenReturn(userExists);

        if (userExists) {
            UsersEntity userEntity = createUserEntity(userId, userPhone, userPassword, userAddress);
            lenient().when(userModifyRepository.findById(Integer.parseInt(userId))).thenReturn(Optional.of(userEntity));
            lenient().when(userModifyRepository.save(any(UsersEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        // Act
        UserModifyResponseDTO responseDTO = userModifyService.UserModify(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            verify(userModifyRepository, times(1)).save(argThat(user ->
                    user.getUserPhone().equals(userPhone) &&
//                            user.getUserPassword().equals(SecurityUtils.encodePassword(userPassword)) &&
                            SecurityUtils.matchesPassword(userPassword, user.getUserPassword()) &&
                            user.getUserAddress().equals(userAddress)
            ));
        }
    }

    private UsersEntity createUserEntity(String userId, String userPhone, String userPassword, String userAddress) {
        UsersEntity userEntity = new UsersEntity();
        userEntity.setUserId(Integer.parseInt(userId));
        userEntity.setUserPhone(userPhone);
        userEntity.setUserPassword(userPassword);
        userEntity.setUserAddress(userAddress);
        return userEntity;
    }
}
