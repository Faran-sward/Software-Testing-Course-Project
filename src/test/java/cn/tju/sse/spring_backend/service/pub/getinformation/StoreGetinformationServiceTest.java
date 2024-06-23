package cn.tju.sse.spring_backend.service.pub.getinformation;

import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationRequestDTO;
import cn.tju.sse.spring_backend.dto.pub.getinformation.StoreGetinformationResponseDTO;
import cn.tju.sse.spring_backend.model.StoreCategoriesEntity;
import cn.tju.sse.spring_backend.model.StoreEntity;
import cn.tju.sse.spring_backend.repository.pub.getinformation.StoreCategoriesGetinformationRepository;
import cn.tju.sse.spring_backend.repository.pub.getinformation.StoreGetinformationRepository;
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
public class StoreGetinformationServiceTest {

    @Mock
    private StoreGetinformationRepository storeGetinformationRepository;

    @Mock
    private StoreCategoriesGetinformationRepository storeCategoriesGetinformationRepository;

    @InjectMocks
    private StoreGetinformationService service;

    private static Stream<Arguments> provideStoreData() {
        Random random = new Random();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        return IntStream.range(0, 100).mapToObj(i -> {
            String stoId = String.valueOf(random.nextInt(1000));
            StoreEntity store = new StoreEntity(random.nextInt(1000), "Store " + stoId, "Introduction " + stoId, "License " + stoId, random.nextInt(2));
            String[] categories = IntStream.range(0, random.nextInt(3)).mapToObj(j -> "Category" + j).toArray(String[]::new);

            // 保持8:2的成功和失败比例
            String expectedMessage;
            if (successCount[0] < 80) {
                expectedMessage = "success";
                successCount[0]++;
            } else {
                expectedMessage = "store not found";
                store = null;
                categories = new String[0];
                failureCount[0]++;
            }

            return Arguments.of(stoId, store, categories, expectedMessage);
        }).collect(Collectors.toList()).stream();
    }

    @ParameterizedTest
    @MethodSource("provideStoreData")
    public void testStoreGetinformation(String stoId, StoreEntity store, String[] categories, String expectedMessage) {
        StoreGetinformationRequestDTO request = new StoreGetinformationRequestDTO();
        request.setSto_ID(stoId);

        if (store != null) {
            when(storeGetinformationRepository.findById(Integer.parseInt(stoId))).thenReturn(Optional.of(store));
            when(storeGetinformationRepository.existsByStoId(Integer.parseInt(stoId))).thenReturn(true);
        } else {
            when(storeGetinformationRepository.existsByStoId(Integer.parseInt(stoId))).thenReturn(false);
        }

        StoreCategoriesEntity[] categoriesEntities = new StoreCategoriesEntity[categories.length];
        for (int i = 0; i < categories.length; i++) {
            categoriesEntities[i] = new StoreCategoriesEntity(store != null ? store.getStoId() : Integer.parseInt(stoId), categories[i]);
        }
        when(storeCategoriesGetinformationRepository.findAllByStoreId(Integer.parseInt(stoId))).thenReturn(java.util.Arrays.asList(categoriesEntities));

        StoreGetinformationResponseDTO response = service.StoreGetinformation(request);

        assertEquals(expectedMessage, response.getMessage());
        verify(storeGetinformationRepository, times(1)).existsByStoId(Integer.parseInt(stoId));
        if (store != null) {
            verify(storeGetinformationRepository, times(1)).findById(Integer.parseInt(stoId));
            verify(storeCategoriesGetinformationRepository, times(1)).findAllByStoreId(Integer.parseInt(stoId));
        }
    }
}
