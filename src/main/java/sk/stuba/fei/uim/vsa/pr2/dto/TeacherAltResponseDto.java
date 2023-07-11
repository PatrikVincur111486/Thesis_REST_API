package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.ZaverecnaPraca;


import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherAltResponseDto {
    Long id;
    Long aisId;
    String name;
    String email;
    String institute;
    String department;
    List<Long> theses;

    public TeacherAltResponseDto(Pedagog teacher) {
        this.id = teacher.getAisId();
        this.aisId = teacher.getAisId();
        this.name = teacher.getMeno();
        this.email = teacher.getEmail();
        this.institute = teacher.getInstitut();
        this.department = teacher.getOddelenie();

        this.theses=new ArrayList<>();
        if(teacher.getVypisanePrace()!=null){
            for(ZaverecnaPraca z:teacher.getVypisanePrace()){
                this.theses.add(z.getId());
            }
        }
    }
    public TeacherAltResponseDto(){}
}
