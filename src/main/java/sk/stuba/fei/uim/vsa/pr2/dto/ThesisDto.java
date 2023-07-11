package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr1.entities.Status;
import sk.stuba.fei.uim.vsa.pr1.entities.Typ;
import sk.stuba.fei.uim.vsa.pr1.entities.ZaverecnaPraca;

import java.util.Date;

@Data
public class ThesisDto {
    Long id;
    String registrationNumber;
    String title;
    String description;
    String department;
    TeacherAltResponseDto supervisor;
    StudentAltResponseDto author;
    Date published;
    Date deadline;
    Typ type;
    Status status;

    public ThesisDto(ZaverecnaPraca z) {
        this.id = z.getId();
        this.registrationNumber = z.getRegistracneCislo();
        this.title = z.getNazov();
        this.description = z.getPopis();
        this.department = z.getPracovisko();
        this.published = z.getDatumZverejnenia();
        this.deadline = z.getDeadline();
        this.type = z.getTyp();
        this.status = z.getStatus();
        this.supervisor = new TeacherAltResponseDto(z.getVeduciPrace());

        if(z.getVypracovatel()!= null){
            this.author = new StudentAltResponseDto(z.getVypracovatel());
        }

    }
    public ThesisDto(){}
}
