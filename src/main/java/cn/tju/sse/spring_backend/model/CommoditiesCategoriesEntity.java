package cn.tju.sse.spring_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "COMMODITIES_CATEGORIES", schema = "food_bank", catalog = "")
public class CommoditiesCategoriesEntity {
      
    @Id
    @Column(name = "COM_CATEGORY")
    private String comCategory;

    public String getComCategory() {
        return comCategory;
    }

    public void setComCategory(String comCategory) {
        this.comCategory = comCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommoditiesCategoriesEntity that = (CommoditiesCategoriesEntity) o;
        return Objects.equals(comCategory, that.comCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comCategory);
    }
}
