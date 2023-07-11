package sk.stuba.fei.uim.vsa.pr2.model.dto.response.student;

import lombok.Data;


@Data
public abstract class StudentResponse {

    private Long id;
    private Long aisId;
    private String name;
    private String email;
    private Integer year;
    private Integer term;
    private String programme;

    public StudentResponse() {
    }

    public StudentResponse(Long id, Long aisId, String email) {
        this.id = id;
        this.aisId = aisId;
        this.email = email;
    }
}
