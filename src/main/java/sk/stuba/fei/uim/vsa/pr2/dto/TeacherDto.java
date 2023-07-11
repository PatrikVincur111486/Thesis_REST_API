package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.ZaverecnaPraca;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherDto {
    Long id;
    Long aisId;
    String name;
    String email;
    String institute;
    String department;
    List<ZaverecnaPraca> theses;

    public TeacherDto(Pedagog pedagog) {
        this.id = pedagog.getAisId();
        this.aisId = pedagog.getAisId();
        this.name = pedagog.getMeno();
        this.email = pedagog.getEmail();
        this.institute = pedagog.getInstitut();
        this.department = pedagog.getOddelenie();

        this.theses=new ArrayList<>();
        if(pedagog.getVypisanePrace()!=null){
            for(ZaverecnaPraca z: pedagog.getVypisanePrace()){
                this.theses.add(z);
            }
        }
    }

    TeacherDto(){}
}
