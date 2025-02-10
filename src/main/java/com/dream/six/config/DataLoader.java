package com.dream.six.config;

import com.dream.six.entity.RoleEntity;
import com.dream.six.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        loadRoles();
    }

    private void loadRoles() {
        createRoleIfNotExists("SUPERADMIN");
        createRoleIfNotExists("USER");

    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByNameAndIsDeletedFalse(roleName)
                .orElseGet(() -> {
                    RoleEntity newRole = new RoleEntity();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });
    }
}
