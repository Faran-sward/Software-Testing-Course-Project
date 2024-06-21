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
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
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
        return Stream.of(
                Arguments.of("1", new StoreEntity(1, "Store A", "Introduction A", "License A", 1), new String[]{"Books", "Electronics"}, "success"),
                Arguments.of("2", null, new String[0], "store not found"),  // 测试不存在的商家
                Arguments.of("3", new StoreEntity(3, "Store C", "Introduction C", "License C", 1), new String[]{}, "success")  // 商家无分类信息
        );
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
            categoriesEntities[i] = new StoreCategoriesEntity(store.getStoId(), categories[i]);
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
