package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ZAVERECNA_PRACA")
@NamedQuery(name = ZaverecnaPraca.FIND_BY_STUDENT_ID, query = "select zaverecnaPraca from ZaverecnaPraca zaverecnaPraca where zaverecnaPraca.vypracovatel = :vypracovatel")
@NamedQuery(name = ZaverecnaPraca.FIND_BY_PEDAGOG_ID, query = "select zaverecnaPraca from ZaverecnaPraca zaverecnaPraca where zaverecnaPraca.veduciPrace.aisId = :veduciPraceId")
@NamedNativeQuery(name = ZaverecnaPraca.FIND_ALL, query = "select * from ZAVERECNA_PRACA", resultClass = ZaverecnaPraca.class)
public class ZaverecnaPraca implements Serializable {

    public static final String FIND_ALL = "ZaverecnaPraca.findAll";
    public static final String FIND_BY_STUDENT_ID = "ZaverecnaPraca.findByStudentId";
    public static final String FIND_BY_PEDAGOG_ID = "ZaverecnaPraca.findByPedagogId";
    private static final long serialVersionUID = -4806603755191869826L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String registracneCislo;
    private String nazov;
    private String popis;
    private String pracovisko;
    @ManyToOne(optional = false)
    private Pedagog veduciPrace;
    @OneToOne
    private Student vypracovatel;
    private Date datumZverejnenia;
    private Date deadline;
    @Enumerated(EnumType.STRING)
    private Typ typ;
    @Enumerated(EnumType.STRING)
    private Status status;

    public ZaverecnaPraca(Pedagog veduciPrace, String nazov, Typ typ, String popis) {
        this.nazov = nazov;
        this.popis = popis;
        this.veduciPrace = veduciPrace;
        this.typ = typ;
        this.status = Status.VOĽNÁ;
    }

}
