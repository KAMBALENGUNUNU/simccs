package com.acp.simccs.config;

import com.acp.simccs.modules.identity.model.ERole;
import com.acp.simccs.modules.identity.model.Role;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.RoleRepository;
import com.acp.simccs.modules.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, ERole.ROLE_JOURNALIST));
            roleRepository.save(new Role(null, ERole.ROLE_EDITOR));
            roleRepository.save(new Role(null, ERole.ROLE_ADMIN));
        }

        // 2. Initialize Super Admin
        if (!userRepository.existsByEmail("admin@simccs.com")) {
            User admin = new User("admin@simccs.com", encoder.encode("admin123"), "Super Admin");
            admin.setIsEnabled(true); // Auto-enable

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("✅ SUPER ADMIN CREATED: admin@simccs.com / admin123");
        }
    }
}