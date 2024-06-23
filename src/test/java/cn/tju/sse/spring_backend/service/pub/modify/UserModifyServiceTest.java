package cn.tju.sse.spring_backend.service.pub.modify;

import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.modify.UserModifyResponseDTO;
import cn.tju.sse.spring_backend.model.UsersEntity;
import cn.tju.sse.spring_backend.repository.pub.modify.UserModifyRepository;
import cn.tju.sse.spring_backend.utils.SecurityUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserModifyServiceTest {

    @Mock
    private UserModifyRepository userModifyRepository;

    @InjectMocks
    private UserModifyService service;

    private static Stream<Arguments> provideUserModifyData() {
        Random random = new Random();
        return Stream.generate(() -> {
            boolean isSuccess = random.nextInt(10) < 8;
            String userId = String.valueOf(random.nextInt(1000));
            String phone = isSuccess ? "1234567890" : "123456789012345678901";
            String password = "password" + random.nextInt(1000);
            String address = isSuccess ? "ValidAddress" : "";
            boolean userExists = isSuccess;
            String expectedMessage = isSuccess ? "success" : (userExists ? "phone is too long" : "user not found");

            return Arguments.of(userId, phone, password, address, userExists, expectedMessage);
        }).limit(100);
    }

    @ParameterizedTest
    @MethodSource("provideUserModifyData")
    public void testUserModify(String userId, String phone, String password, String address, boolean userExists, String expectedMessage) {
        UserModifyRequestDTO request = new UserModifyRequestDTO();
        request.setUser_ID(userId);
        request.setUser_phone(phone);
        request.setUser_password(password);
        request.setUser_address(address);

        if (userExists) {
            UsersEntity foundUser = new UsersEntity();
            foundUser.setUserId(Integer.parseInt(userId));
            foundUser.setUserPhone("oldPhone");
            foundUser.setUserPassword("oldPassword");
            foundUser.setUserAddress("oldAddress");

            when(userModifyRepository.existsByUserId(Integer.parseInt(userId))).thenReturn(true);
            when(userModifyRepository.findById(Integer.parseInt(userId))).thenReturn(Optional.of(foundUser));
        } else {
            when(userModifyRepository.existsByUserId(Integer.parseInt(userId))).thenReturn(false);
        }

        UserModifyResponseDTO response = service.UserModify(request);

        assertEquals(expectedMessage, response.getMessage());
        if (userExists && !"phone is too long".equals(expectedMessage)) {
            verify(userModifyRepository).save(any(UsersEntity.class));
        }
    }
}
