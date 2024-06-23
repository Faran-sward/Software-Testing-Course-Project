package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.CustomerModifyResponseDTO;
import cn.tju.sse.spring_backend.model.CommoditiesCategoriesEntity;
import cn.tju.sse.spring_backend.model.CustomerEntity;
import cn.tju.sse.spring_backend.model.CustomerLoveEntity;
import cn.tju.sse.spring_backend.repository.pub.modify.CommoditiesCategoriesRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.CustomerLoveModifyRepository;
import cn.tju.sse.spring_backend.repository.pub.modify.CustomerModifyRepository;
import cn.tju.sse.spring_backend.utils.SecurityUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerModifyServiceTest {

    @Mock
    private CustomerModifyRepository customerModifyRepository;

    @Mock
    private CustomerLoveModifyRepository customerLoveModifyRepository;

    @Mock
    private CommoditiesCategoriesRepository commoditiesCategoriesRepository;

    @InjectMocks
    private CustomerModifyService service;

    private static Stream<Arguments> provideCustomerModifyData() {
        Random random = new Random();
        return Stream.generate(() -> {
            boolean isSuccess = random.nextInt(10) < 8;
            String cusId = String.valueOf(random.nextInt(1000));
            String nickname = isSuccess ? "ValidNickname" : "";
            String notes = isSuccess ? "ValidNotes" : "";
            String password = "password" + random.nextInt(1000);
            String[] categories = isSuccess ? new String[]{"Books"} : new String[]{"InvalidCategory"};
            boolean customerExists = isSuccess;
            boolean validCategories = isSuccess;
            String expectedMessage = isSuccess ? "success" : (customerExists ? (validCategories ? "nickname is too long" : "categories not right") : "customer not found");

            return Arguments.of(cusId, nickname, notes, password, categories, customerExists, validCategories, expectedMessage);
        }).limit(100);
    }

    @ParameterizedTest
    @MethodSource("provideCustomerModifyData")
    public void testCustomerModify(String cusId, String nickname, String notes, String password, String[] categories, boolean customerExists, boolean validCategories, String expectedMessage) {
        CustomerModifyRequestDTO request = new CustomerModifyRequestDTO();
        request.setCus_ID(cusId);
        request.setCus_nickname(nickname);
        request.setCus_notes(notes);
        request.setCus_payPassword(password);
        request.setCus_category(categories);

        if (customerExists) {
            CustomerEntity foundCustomer = new CustomerEntity();
            foundCustomer.setCusId(Integer.parseInt(cusId));
            foundCustomer.setCusNickname("oldNickname");
            foundCustomer.setCusNotes("oldNotes");
            foundCustomer.setCusPaypassword("oldPassword");

            when(customerModifyRepository.existsById(Integer.parseInt(cusId))).thenReturn(true);
            when(customerModifyRepository.findById(Integer.parseInt(cusId))).thenReturn(Optional.of(foundCustomer));
        } else {
            when(customerModifyRepository.existsById(Integer.parseInt(cusId))).thenReturn(false);
        }

        if (validCategories) {
            when(commoditiesCategoriesRepository.findAll()).thenReturn(List.of(new CommoditiesCategoriesEntity("Books"), new CommoditiesCategoriesEntity("Electronics")));
        } else {
            when(commoditiesCategoriesRepository.findAll()).thenReturn(List.of(new CommoditiesCategoriesEntity("Books")));
        }

        CustomerModifyResponseDTO response = service.customerModify(request);

        assertEquals(expectedMessage, response.getMessage());
        if (customerExists && validCategories && !"nickname is too long".equals(expectedMessage)) {
            verify(customerModifyRepository).save(any(CustomerEntity.class));
        }
    }
}
