package cn.tju.sse.spring_backend.controller.pub.modify;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.StoreModifyResponseDTO;
import cn.tju.sse.spring_backend.service.pub.modify.StoreModifyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class StoreModifyControllerTest {

    @Mock
    private StoreModifyService storeModifyService;

    @InjectMocks
    private StoreModifyController storeModifyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForController() throws IOException {
        String testClass = StoreModifyControllerTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "storeModify";

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
    void storeModify(String sto_ID, String sto_introduction, String sto_name, String[] categories,
                     MultipartFile sto_licenseImg, MultipartFile[] sto_picture, String message) {
        // Arrange
        StoreModifyRequestDTO request = new StoreModifyRequestDTO();
        request.setSto_ID(sto_ID);
        request.setSto_introduction(sto_introduction);
        request.setSto_name(sto_name);
        request.setCategories(categories);
        request.setStoLicenseImg(sto_licenseImg);
        request.setStoPicture(sto_picture);

        StoreModifyResponseDTO mockResponse = new StoreModifyResponseDTO();
        mockResponse.setMessage(message);

        when(storeModifyService.storeModify(any(StoreModifyRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<StoreModifyResponseDTO> response = storeModifyController.storeModify(
                sto_ID, sto_introduction, sto_name, categories, sto_licenseImg, sto_picture);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }
}
