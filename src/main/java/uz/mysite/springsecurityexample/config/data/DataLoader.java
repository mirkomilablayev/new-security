package uz.mysite.springsecurityexample.config.data;


import uz.mysite.springsecurityexample.entity.User;
import uz.mysite.springsecurityexample.entity.UserRole;
import uz.mysite.springsecurityexample.repository.RoleRepository;
import uz.mysite.springsecurityexample.repository.UserRepository;
import uz.mysite.springsecurityexample.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;


    @Value(value = "${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) throws Exception {
        if (ddl.equalsIgnoreCase("create") || ddl.equalsIgnoreCase("create-drop")) {
            roleRepo.save(new UserRole(Constants.USER_ROLE));
            UserRole admin_role = roleRepo.save(new UserRole(Constants.ADMIN_ROLE));

            User user = new User();
            user.setFullName("administrator");
            user.setAbout("this is admin from the system");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin_1212"));
            user.setRoles(new HashSet<>(List.of(roleRepo.findByName(Constants.ADMIN_ROLE).orElse(admin_role))));
//            user.setId(UUID.randomUUID().toString());
            userRepo.save(user);

        }
    }
}
