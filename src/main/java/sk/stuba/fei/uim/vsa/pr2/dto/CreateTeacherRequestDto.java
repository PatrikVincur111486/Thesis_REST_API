package sk.stuba.fei.uim.vsa.pr2.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateTeacherRequestDto {
    Long aisId;
    String name;
    String email;
    String password;
    String institute;
    String department;
}
