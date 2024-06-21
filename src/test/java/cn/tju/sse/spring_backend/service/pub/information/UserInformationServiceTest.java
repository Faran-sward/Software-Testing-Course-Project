package cn.tju.sse.spring_backend.service.pub.information;

import cn.tju.sse.spring_backend.dto.pub.information.UserInformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.information.UserInformationResponseDTO;
import cn.tju.sse.spring_backend.model.UsersEntity;
import cn.tju.sse.spring_backend.repository.pub.information.UserInformationRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserInformationServiceTest {

    @Mock
    private UserInformationRepository userInformationRepository;

    @InjectMocks
    private UserInformationService service;

    private static Stream<Arguments> provideUserData() {
        return Stream.of(
                Arguments.of("1", new UsersEntity("1234567890", "password1", "Address1", 1, new Date(System.currentTimeMillis()), 1, 1, new BigDecimal("100.00")), "success"),
                Arguments.of("2", null, "user not found"),  // 测试不存在的用户
                Arguments.of("3", new UsersEntity("2345678901", "password2", "Address2", 1, new Date(System.currentTimeMillis()), 1, 3, new BigDecimal("200.00")), "success")  // 用户存在
        );
    }

    @ParameterizedTest
    @MethodSource("provideUserData")
    public void testUserInformation(String userId, UsersEntity user, String expectedMessage) {
        UserInformationRequestDTO request = new UserInformationRequestDTO();
        request.setUser_ID(userId);

        if (user != null) {
            when(userInformationRepository.findById(Integer.parseInt(userId))).thenReturn(Optional.of(user));
            when(userInformationRepository.existsUsersEntityByUserId(Integer.parseInt(userId))).thenReturn(true);
        } else {
            when(userInformationRepository.existsUsersEntityByUserId(Integer.parseInt(userId))).thenReturn(false);
        }

        UserInformationResponseDTO response = service.UserInformation(request);

        assertEquals(expectedMessage, response.getMessage());
        verify(userInformationRepository, times(1)).existsUsersEntityByUserId(Integer.parseInt(userId));
        if (user != null) {
            verify(userInformationRepository, times(1)).findById(Integer.parseInt(userId));
        }
    }
}
