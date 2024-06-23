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

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        Random random = new Random();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        return IntStream.range(0, 100).mapToObj(i -> {
            String cusId = String.valueOf(random.nextInt(1000));
            CustomerEntity customer = new CustomerEntity(random.nextInt(1000), "Nick" + cusId, "Note" + cusId, "Password" + cusId, random.nextInt(2));
            CustomerLoveEntity[] loves = IntStream.range(0, random.nextInt(3)).mapToObj(j -> new CustomerLoveEntity(random.nextInt(1000), "Category" + j, random.nextInt(6))).toArray(CustomerLoveEntity[]::new);

            // 保持8:2的成功和失败比例
            String expectedMessage;
            if (successCount[0] < 80) {
                expectedMessage = "success";
                successCount[0]++;
            } else {
                expectedMessage = "customers not found";
                customer = null;
                loves = new CustomerLoveEntity[0];
                failureCount[0]++;
            }

            return Arguments.of(cusId, customer, loves, expectedMessage);
        }).collect(Collectors.toList()).stream();
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
