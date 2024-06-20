package cn.tju.sse.spring_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CUSTOMER_LOVE", schema = "food_bank", catalog = "")
@IdClass(CustomerLoveEntityPK.class)
public class CustomerLoveEntity {
      
    @Id
    @Column(name = "CUS_ID")
    private int cusId;
      
    @Id
    @Column(name = "COM_CATEGORY")
    private String comCategory;
    @Basic
    @Column(name = "CUS_LOVE_WEIGHT")
    private int cusLoveWeight;

    public int getCusId() {
        return cusId;
    }

    public void setCusId(int cusId) {
        this.cusId = cusId;
    }

    public String getComCategory() {
        return comCategory;
    }

    public void setComCategory(String comCategory) {
        this.comCategory = comCategory;
    }

    public int getCusLoveWeight() {
        return cusLoveWeight;
    }

    public void setCusLoveWeight(int cusLoveWeight) {
        this.cusLoveWeight = cusLoveWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerLoveEntity that = (CustomerLoveEntity) o;
        return cusId == that.cusId && cusLoveWeight == that.cusLoveWeight && Objects.equals(comCategory, that.comCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cusId, comCategory, cusLoveWeight);
    }

    // Static method to create an array of CustomerLoveEntity
    public static CustomerLoveEntity[] createArray(int cusId, String[] categories, int[] weights) {
        if (categories.length != weights.length) {
            throw new IllegalArgumentException("Length of categories and weights must be the same");
        }

        CustomerLoveEntity[] entities = new CustomerLoveEntity[categories.length];
        for (int i = 0; i < categories.length; i++) {
            entities[i] = CustomerLoveEntity.builder()
                    .cusId(cusId)
                    .comCategory(categories[i])
                    .cusLoveWeight(weights[i])
                    .build();
        }
        return entities;
    }
}
