package sk.stuba.fei.uim.vsa.pr2.dto;


import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;


@Data
public class StudentAltResponseDto {
    Long id;
    Long aisId;
    String name;
    String email;
    Integer year;
    Integer term;
    String programme;
    Long thesisId;

    public StudentAltResponseDto(Student vypracovatel) {
        this.id = vypracovatel.getAisId();
        this.aisId = vypracovatel.getAisId();
        this.name = vypracovatel.getName();
        this.email = vypracovatel.getEmail();
        this.year = vypracovatel.getRocnik();
        this.term = vypracovatel.getSemester();
        this.programme = vypracovatel.getProgram();

        if(vypracovatel.getZaverecnaPraca()!=null){
            this.thesisId=vypracovatel.getZaverecnaPraca().getId();
        }
    }
    StudentAltResponseDto(){}
}
