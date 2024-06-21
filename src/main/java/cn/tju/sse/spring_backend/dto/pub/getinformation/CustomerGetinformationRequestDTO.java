package cn.tju.sse.spring_backend.dto.pub.getinformation;

import lombok.*;

/**
 * @ClassName CustomerGetinformationRequestDTO
 * @Author RaoJI
 * Description 用于接收前端发来的顾客id
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomerGetinformationRequestDTO {
    private String cus_ID;
}
