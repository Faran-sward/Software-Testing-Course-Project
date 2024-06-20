package cn.tju.sse.spring_backend.controller.pub.getinformation;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationResponseDTO;
import cn.tju.sse.spring_backend.service.pub.getinformation.StoreGetinformationService;
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

class StoreGetinformationControllerTest {

    @Mock
    private StoreGetinformationService storeGetinformationService;

    @InjectMocks
    private StoreGetinformationController storeGetinformationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForController() throws IOException {
        String testClass = StoreGetinformationControllerTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "storeGetinformation";

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
    void storeGetinformation(String sto_ID, String message, Integer expectedSto_ID, String sto_name,
                             String sto_introduction, String sto_licenseImg, String sto_state, String[] categories) {
        // Arrange
        StoreGetinformationResponseDTO mockResponse = new StoreGetinformationResponseDTO();
        mockResponse.setMessage(message);
        mockResponse.setSto_ID(expectedSto_ID);
        mockResponse.setSto_name(sto_name);
        mockResponse.setSto_introduction(sto_introduction);
        mockResponse.setSto_licenseImg(sto_licenseImg);
        mockResponse.setSto_state(sto_state);
        mockResponse.setCategories(categories);

        when(storeGetinformationService.StoreGetinformation(any(StoreGetinformationRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<StoreGetinformationResponseDTO> response = storeGetinformationController.storeGetinformation(sto_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}
