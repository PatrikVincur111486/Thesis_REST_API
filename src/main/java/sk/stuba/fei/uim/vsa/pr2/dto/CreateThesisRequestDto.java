package sk.stuba.fei.uim.vsa.pr2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.entities.Typ;
import sk.stuba.fei.uim.vsa.pr1.entities.ZaverecnaPraca;


@NoArgsConstructor
@Data
public class CreateThesisRequestDto {
    String registrationNumber;
    String title;
    String description;
    Typ type;

    public CreateThesisRequestDto(ZaverecnaPraca z) {
        this.registrationNumber = z.getRegistracneCislo();
        this.title = z.getNazov();
        this.description = z.getPopis();
        this.type = z.getTyp();
    }
}
