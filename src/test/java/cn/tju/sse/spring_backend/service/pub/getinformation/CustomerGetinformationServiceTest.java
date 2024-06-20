package cn.tju.sse.spring_backend.service.pub.getinformation;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.mapper.CustomerGetinformationRequestMapper;
import cn.tju.sse.spring_backend.dto.pub.getinformation.mapper.CustomerGetinformationResponseMapper;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import cn.tju.sse.spring_backend.repository.pub.getinformation.CustomerGetinformationRepository;
import cn.tju.sse.spring_backend.repository.pub.getinformation.CustomerLoveGetinformationRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerGetinformationServiceTest {

    @Mock
    private CustomerGetinformationRepository customerGetinformationRepository;

    @Mock
    private CustomerLoveGetinformationRepository customerLoveGetinformationRepository;

    @InjectMocks
    private CustomerGetinformationService customerGetinformationService;

    private final CustomerGetinformationResponseMapper customerGetinformationResponseMapper
            = CustomerGetinformationResponseMapper.INSTANCE;

    private final CustomerGetinformationRequestMapper customerGetinformationRequestMapper
            = CustomerGetinformationRequestMapper.INSTANCE;


    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = CustomerGetinformationServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "customerGetinformationTest";

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
//    @MethodSource("provideTestCases")
    @MethodSource("readExcelForRequestToEntity")
    void customerGetinformationTest(String cusId, boolean customerExists, boolean dbError ,String expectedMessage, String expectedCusId) {
        // Arrange
        CustomerGetinformationRequestDTO requestDTO = new CustomerGetinformationRequestDTO();
        requestDTO.setCus_ID(cusId);

        when(customerGetinformationRepository.existsByCusId(Integer.parseInt(cusId))).thenReturn(customerExists);

        if (customerExists) {
            Optional<CustomerEntity> customerEntity = Optional.of(new CustomerEntity());

            if (!dbError) {
                customerEntity = createCustomerEntity(cusId);
            }
            when(customerGetinformationRepository.findById(Integer.parseInt(cusId))).thenReturn(customerEntity);

            if (expectedMessage.equals("success")) {
                List<CustomerLoveEntity> loveEntities = createCustomerLoveEntities(cusId);
                when(customerLoveGetinformationRepository.findAllByCusId(Integer.parseInt(cusId))).thenReturn(loveEntities);
            }
        }

        // Act
        CustomerGetinformationResponseDTO responseDTO = customerGetinformationService.CustomerGetinformation(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            assertEquals(expectedCusId.length(), responseDTO.getCus_ID().length());
            assertEquals(expectedCusId, responseDTO.getCus_ID());
        }
    }

    private Optional<CustomerEntity> createCustomerEntity(String cusId) {
        Random random = new Random();
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCusId(Integer.parseInt(cusId));
        customerEntity.setCusNickname("Nickname" + random.nextInt(1000));
        customerEntity.setCusNotes("Notes" + random.nextInt(1000));
        customerEntity.setCusPaypassword("PayPassword" + random.nextInt(1000));
        customerEntity.setCusState(random.nextInt(2));
        return Optional.of(customerEntity);
    }

    private List<CustomerLoveEntity> createCustomerLoveEntities(String cusId) {
        Random random = new Random();
        String[] loves = {"Love1", "Love2", "Love3"};
        int[] weights = {random.nextInt(10), random.nextInt(10), random.nextInt(10)};

        return IntStream.range(0, loves.length)
                .mapToObj(i -> {
                    CustomerLoveEntity loveEntity = new CustomerLoveEntity();
                    loveEntity.setCusId(Integer.parseInt(cusId));
                    loveEntity.setComCategory(loves[i]);
                    loveEntity.setCusLoveWeight(weights[i]);
                    return loveEntity;
                }).toList();
    }

}
