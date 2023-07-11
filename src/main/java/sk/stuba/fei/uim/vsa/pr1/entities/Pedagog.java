package sk.stuba.fei.uim.vsa.pr1.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "PEDAGOG")
@NamedQuery(name = Pedagog.FIND_BY_EMAIL, query = "select t from Pedagog t WHERE t.email= :email")
@NamedNativeQuery(name = Pedagog.FIND_ALL, query = "select * from PEDAGOG", resultClass = Pedagog.class)
public class Pedagog implements Serializable {
    private static final long serialVersionUID = 9044353650515747903L;
    public static final String FIND_ALL = "Pedagog.findAll";
    public static final String FIND_BY_EMAIL = "Pedagog.findByEmail";

    @Id
    private Long aisId;
    private String meno;
    @Column(unique = true,nullable = false)
    private String email;
    private String password;
    private String institut;
    private String oddelenie;
    @OneToMany(mappedBy = "veduciPrace",cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ZaverecnaPraca> vypisanePrace;

    public Pedagog(Long aisId, String meno, String email, String oddelenie, String password) {
        this.aisId = aisId;
        this.meno = meno;
        this.email = email;
        this.oddelenie = oddelenie;
        this.password=password;
    }

    public void addZaverecnaPraca(ZaverecnaPraca zaverecnaPraca){
        if(vypisanePrace==null){
            vypisanePrace=new ArrayList<>();
        }
        this.vypisanePrace.add(zaverecnaPraca);
    }

}
