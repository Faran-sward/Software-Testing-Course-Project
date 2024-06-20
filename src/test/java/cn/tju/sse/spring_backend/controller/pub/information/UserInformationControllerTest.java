package cn.tju.sse.spring_backend.controller.pub.information;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.information.UserInformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserInformationControllerTest {

    @Mock
    private UserInformationService userInformationService;

    @InjectMocks
    private UserInformationController userInformationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForController() throws IOException {
        String testClass = UserInformationControllerTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "userInformation";

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
    @MethodSource("readExcelForController")
    void userInformation(String user_ID, String message, String expectedUser_ID, String user_phone,
                         String user_password, String user_address, String user_balance,
                         String user_regTime, String user_type) {
        // Arrange
        UserInformationResponseDTO mockResponse = new UserInformationResponseDTO();
        mockResponse.setMessage(message);
        mockResponse.setUser_ID(expectedUser_ID);
        mockResponse.setUser_phone(user_phone);
        mockResponse.setUser_password(user_password);
        mockResponse.setUser_address(user_address);
        mockResponse.setUser_balance(user_balance);
        mockResponse.setUser_regTime(user_regTime);
        mockResponse.setUser_type(user_type);

        when(userInformationService.UserInformation(any(UserInformationRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<UserInformationResponseDTO> response = userInformationController.userInformation(user_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}
