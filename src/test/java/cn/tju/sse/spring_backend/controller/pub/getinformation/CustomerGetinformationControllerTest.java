package cn.tju.sse.spring_backend.controller.pub.getinformation;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.getinformation.CustomerGetinformationService;
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
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CustomerGetinformationControllerTest {

    @Mock
    private CustomerGetinformationService customerGetinformationService;

    @InjectMocks
    private CustomerGetinformationController customerGetinformationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForController() throws IOException {
        String testClass = CustomerGetinformationControllerTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "customerGetinformation";

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
    void customerGetinformation(String cus_ID, String message, String expectedCus_ID, String cus_nickname,
                                String cus_notes, String cus_payPassword, String cus_state, String[] cus_loves) {
        // Arrange
        CustomerGetinformationResponseDTO mockResponse = CustomerGetinformationResponseDTO.builder()
                .message(message)
                .cus_ID(expectedCus_ID)
                .cus_nickname(cus_nickname)
                .cus_notes(cus_notes)
                .cus_payPassword(cus_payPassword)
                .cus_state(cus_state)
                .cus_loves(cus_loves)
                .build();

        when(customerGetinformationService.CustomerGetinformation(any(CustomerGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<CustomerGetinformationResponseDTO> response = customerGetinformationController.customerGetinformation(cus_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}
