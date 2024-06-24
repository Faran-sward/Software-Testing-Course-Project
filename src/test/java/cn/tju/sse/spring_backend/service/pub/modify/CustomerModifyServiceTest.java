package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.TestFileConfig;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyResponseDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.mapper.CustomerModifyRequestMapper;
import cn.tju.sse.spring_backend.model.CommoditiesCategoriesEntity;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import cn.tju.sse.spring_backend.repository.pub.modify.CommoditiesCategoriesRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.CustomerLoveModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.CustomerModifyRepository;
import cn.tju.sse.spring_backend.utils.SecurityUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.tongji.setest.utils.testCase.TestCase;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerModifyServiceTest {

    @Mock
    private CustomerModifyRepository customerModifyRepository;

    @Mock
    private CustomerLoveModifyRepository customerLoveModifyRepository;

    @Mock
    private CommoditiesCategoriesRepository commoditiesCategoriesRepository;

    @InjectMocks
    private CustomerModifyService customerModifyService;

    @Mock
    private CustomerModifyRequestMapper customerModifyRequestMapper;


    // 转换DataList为Stream<Arguments>
    public static Stream<Arguments> readExcelForRequestToEntity() throws IOException {
        String testClass = CustomerModifyServiceTest.class.getName();
        String testClassName = testClass.substring(testClass.lastIndexOf(".") + 1);

        String testMethodName = "customerModifyTest";

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
    void customerModifyTest(String cusId, boolean cusExists, String cusNickname, String cusPayPassword, String cusNotes,
                            String[] categories, String expectedMessage, String[] expectedCategories) {
        // Arrange
        CustomerModifyRequestDTO requestDTO = new CustomerModifyRequestDTO();
        requestDTO.setCus_ID(cusId);
        requestDTO.setCus_nickname(cusNickname);
        requestDTO.setCus_payPassword(cusPayPassword);
        requestDTO.setCus_notes(cusNotes);
        requestDTO.setCus_category(expectedCategories);

        when(customerModifyRepository.existsById(Integer.parseInt(cusId))).thenReturn(cusExists);

        if (cusExists) {
            CustomerEntity customerEntity = createCustomerEntity(cusId, cusNickname, cusPayPassword, cusNotes);
            lenient().when(customerModifyRequestMapper.requestToEntity(requestDTO)).thenReturn(customerEntity);

            CustomerLoveEntity[] loveEntities = createCustomerLoveEntities(expectedCategories);
            lenient().when(customerModifyRequestMapper.requestToLoveEntities(requestDTO)).thenReturn(loveEntities);

            List<CommoditiesCategoriesEntity> categoryEntities = createCommoditiesCategoriesEntities(categories);
            lenient().when(commoditiesCategoriesRepository.findAll()).thenReturn(categoryEntities);

            if ("success".equals(expectedMessage)) {
                when(customerModifyRepository.findById(Integer.parseInt(cusId))).thenReturn(Optional.of(customerEntity));
                when(customerModifyRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
                when(customerLoveModifyRepository.saveAll(anyIterable())).thenReturn(Arrays.asList(loveEntities));
            }
        }

        // Act
        CustomerModifyResponseDTO responseDTO = customerModifyService.customerModify(requestDTO);

        // Assert
        assertEquals(expectedMessage, responseDTO.getMessage());
        if ("success".equals(expectedMessage)) {
            assertNotNull(responseDTO);

            // 验证保存的CustomerEntity
            ArgumentCaptor<CustomerEntity> customerCaptor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerModifyRepository).save(customerCaptor.capture());
            CustomerEntity savedCustomer = customerCaptor.getValue();
            assertEquals(cusId, String.valueOf(savedCustomer.getCusId()));
            assertEquals(cusNickname, savedCustomer.getCusNickname());
            assertTrue(SecurityUtils.matchesPassword(cusPayPassword, savedCustomer.getCusPaypassword()));
            assertEquals(cusNotes, savedCustomer.getCusNotes());

            // 验证保存的CustomerLoveEntity
            ArgumentCaptor<Iterable<CustomerLoveEntity>> loveCaptor = ArgumentCaptor.forClass(Iterable.class);
            verify(customerLoveModifyRepository).saveAll(loveCaptor.capture());
            Iterable<CustomerLoveEntity> savedLoves = loveCaptor.getValue();
            for (CustomerLoveEntity loveEntity : savedLoves) {
                assertTrue(Arrays.asList(categories).contains(loveEntity.getComCategory()));
            }
        }
    }

    private CustomerEntity createCustomerEntity(String cusId, String cusNickname, String cusPayPassword, String cusNotes) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCusId(Integer.parseInt(cusId));
        customerEntity.setCusNickname(cusNickname);
        customerEntity.setCusPaypassword(cusPayPassword);
        customerEntity.setCusNotes(cusNotes);
        return customerEntity;
    }

    private CustomerLoveEntity[] createCustomerLoveEntities(String[] categories) {
        return Arrays.stream(categories)
                .map(category -> {
                    CustomerLoveEntity loveEntity = new CustomerLoveEntity();
                    loveEntity.setComCategory(category);
                    return loveEntity;
                }).toArray(CustomerLoveEntity[]::new);
    }

    private List<CommoditiesCategoriesEntity> createCommoditiesCategoriesEntities(String[] categories) {
        return Arrays.stream(categories)
                .map(CommoditiesCategoriesEntity::new)
                .toList();
    }
}
