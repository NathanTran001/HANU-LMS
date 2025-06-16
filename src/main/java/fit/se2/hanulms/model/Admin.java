package fit.se2.hanulms.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ADMIN")
//@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends LMSUser {
    public Admin() {
        super();
    }

    public Admin(UserTemplate userTemplate, PasswordEncoder passwordEncoder) {
        super(userTemplate, passwordEncoder, Set.of(Role.ADMIN));
    }

}
