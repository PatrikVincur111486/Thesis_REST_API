package sk.stuba.fei.uim.vsa.pr2.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MockUserDb {
    private List<User> users;

    public MockUserDb(){
        users=new ArrayList<>();
    }
    public Optional<User> getUserByEmail(String email){
        return users.stream().filter(user -> Objects.equals(email, user.getEmail())).findFirst();
    }

}
