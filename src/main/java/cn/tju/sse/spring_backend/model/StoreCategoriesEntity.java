package cn.tju.sse.spring_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STORE_CATEGORIES", schema = "food_bank", catalog = "")
@IdClass(StoreCategoriesEntityPK.class)
public class StoreCategoriesEntity {
      
    @Id
    @Column(name = "STORE_ID")
    private int storeId;
      
    @Id
    @Column(name = "COM_CATEGORY")
    private String comCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreCategoriesEntity that = (StoreCategoriesEntity) o;
        return storeId == that.storeId && Objects.equals(comCategory, that.comCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, comCategory);
    }
}
