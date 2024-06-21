package cn.tju.sse.spring_backend.service.pub.getinformation;

import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.CustomerGetinformationResponseDTO;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import cn.tju.sse.spring_backend.repository.pub.getinformation.CustomerGetinformationRepository;
import cn.tju.sse.spring_backend.repository.pub.getinformation.CustomerLoveGetinformationRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerGetinformationServiceTest {

    @Mock
    private CustomerGetinformationRepository customerGetinformationRepository;

    @Mock
    private CustomerLoveGetinformationRepository customerLoveGetinformationRepository;

    @InjectMocks
    private CustomerGetinformationService service;

    private static Stream<Arguments> provideCustomerData() {
        return Stream.of(
                Arguments.of("100", new CustomerEntity(100, "Nick1", "Note1", "Password1", 1), new CustomerLoveEntity[]{new CustomerLoveEntity(100, "Books", 5)}, "success"),
                Arguments.of("200", null, new CustomerLoveEntity[0], "customers not found"), // 测试不存在的顾客
                Arguments.of("300", new CustomerEntity(300, "Nick3", "Note3", "Password3", 1), new CustomerLoveEntity[0], "success") // 测试没有兴趣爱好的情况
        );
    }

    @ParameterizedTest
    @MethodSource("provideCustomerData")
    public void testCustomerGetinformation(String cusId, CustomerEntity customer, CustomerLoveEntity[] loves, String expectedMessage) {
        CustomerGetinformationRequestDTO request = new CustomerGetinformationRequestDTO();
        request.setCus_ID(cusId);

        if (customer != null) {
            when(customerGetinformationRepository.findById(Integer.parseInt(cusId))).thenReturn(Optional.of(customer));
            when(customerGetinformationRepository.existsByCusId(Integer.parseInt(cusId))).thenReturn(true);
        } else {
            when(customerGetinformationRepository.existsByCusId(Integer.parseInt(cusId))).thenReturn(false);
        }

        when(customerLoveGetinformationRepository.findAllByCusId(Integer.parseInt(cusId))).thenReturn(java.util.Arrays.asList(loves));

        CustomerGetinformationResponseDTO response = service.CustomerGetinformation(request);

        assertEquals(expectedMessage, response.getMessage());
        verify(customerGetinformationRepository, times(1)).existsByCusId(Integer.parseInt(cusId));
        if (customer != null) {
            verify(customerGetinformationRepository, times(1)).findById(Integer.parseInt(cusId));
            verify(customerLoveGetinformationRepository, times(1)).findAllByCusId(Integer.parseInt(cusId));
        }
    }
}
