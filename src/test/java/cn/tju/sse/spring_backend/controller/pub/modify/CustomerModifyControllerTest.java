package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.CustomerModifyService;
import org.junit.jupiter.api.BeforeEach;
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

class CustomerModifyControllerTest {

    @Mock
    private CustomerModifyService customerModifyService;

    @InjectMocks
    private CustomerModifyController customerModifyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForController() throws IOException {
        String testClass = CustomerModifyControllerTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "customerModify";

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
    void customerModify(String cus_ID, String cus_nickname, String cus_notes, String cus_payPassword,
                        String[] cus_category, String message) {
        // Arrange
        CustomerModifyRequestDTO request = new CustomerModifyRequestDTO();
        request.setCus_ID(cus_ID);
        request.setCus_nickname(cus_nickname);
        request.setCus_notes(cus_notes);
        request.setCus_payPassword(cus_payPassword);
        request.setCus_category(cus_category);

        CustomerModifyResponseDTO mockResponse = new CustomerModifyResponseDTO();
        mockResponse.setMessage(message);

        when(customerModifyService.customerModify(any(CustomerModifyRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<CustomerModifyResponseDTO> response = customerModifyController.customerModify(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}
