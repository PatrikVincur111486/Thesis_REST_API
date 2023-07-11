package sk.stuba.fei.uim.vsa.pr2.auth;

import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr1.ThesisService;
import sk.stuba.fei.uim.vsa.pr1.entities.Pedagog;
import sk.stuba.fei.uim.vsa.pr1.entities.Student;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

import java.util.Base64;

@NoArgsConstructor
public class Authentication {
    public Object authenticateUser(String authHeader) {

        if (authHeader == null || !authHeader.contains("Basic")) {
            return null;
        }
        String[] credentials = extractFromaAuthHeader(authHeader);

        String email = credentials[0];

        ThesisService service = new ThesisService();

        Object user = service.getUserByEmail(email);

        if (user == null) {
            return null;
        }else if (user instanceof Pedagog) {
            Pedagog u = (Pedagog) user;
            if (!BCryptService.verify(credentials[1], u.getPassword())) {
                return null;
            }
        } else if (user instanceof Student) {
            Student u = (Student) user;
            if (!BCryptService.verify(credentials[1], u.getPassword())) {
                return null;
            }
        }
        return user;
    }
        private String[] extractFromaAuthHeader(String authHeader){
            return new String(Base64.getDecoder()
                    .decode(authHeader
                            .replace("Basic", "")
                            .trim()))
                    .split(":");
        }
}
