package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;

@Data
public class StudentDto {
    Long id;
    Long aisId;
    String name;
    String email;
    Integer year;
    Integer term;
    String programme;
    ThesisDto thesis;
    public StudentDto(Student student) {
        this.id = student.getAisId();
        this.aisId = student.getAisId();
        this.name = student.getName();
        this.email = student.getEmail();
        this.year = student.getRocnik();
        this.term = student.getSemester();
        this.programme = student.getProgram();

        if(student.getZaverecnaPraca()!= null){
            this.thesis=new ThesisDto(student.getZaverecnaPraca());
        }
    }
    public StudentDto(){}
}
