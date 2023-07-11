package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateStudentRequestDto {
    private Long aisId;
    private String name;
    private String email;
    private String password;
    private Integer year;
    private Integer term;
    private String programme;
}

