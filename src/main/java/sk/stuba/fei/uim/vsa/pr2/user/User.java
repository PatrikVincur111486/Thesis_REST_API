package sk.stuba.fei.uim.vsa.pr2.user;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@Data
@AllArgsConstructor
public class User implements Principal {

    private Long id;
    private String email;
    private String passwrod;

    @Override
    public String getName() {
        return email;
    }
}
