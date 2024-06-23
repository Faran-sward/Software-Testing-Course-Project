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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        Random random = new Random();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        return IntStream.range(0, 100).mapToObj(i -> {
            String userId = String.valueOf(random.nextInt(1000));
            UsersEntity user = new UsersEntity("1234567890" + userId, "password" + userId, "Address" + userId, random.nextInt(2), new Date(System.currentTimeMillis()), random.nextInt(5), random.nextInt(10), new BigDecimal("100.00").multiply(new BigDecimal(random.nextInt(10) + 1)));

            // 保持8:2的成功和失败比例
            String expectedMessage;
            if (successCount[0] < 80) {
                expectedMessage = "success";
                successCount[0]++;
            } else {
                expectedMessage = "user not found";
                user = null;
                failureCount[0]++;
            }

            return Arguments.of(userId, user, expectedMessage);
        }).collect(Collectors.toList()).stream();
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
