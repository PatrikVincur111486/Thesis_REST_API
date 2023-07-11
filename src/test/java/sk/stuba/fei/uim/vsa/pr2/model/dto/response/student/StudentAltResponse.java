package sk.stuba.fei.uim.vsa.pr2.model.dto.response.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentAltResponse extends StudentResponse {

    private Long thesis;

    public StudentAltResponse() {
    }

    public StudentAltResponse(Long id, Long aisId, String email) {
        super(id, aisId, email);
    }
}
