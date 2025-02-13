package com.dream.six.config;

import com.dream.six.entity.RoleEntity;
import com.dream.six.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RoleConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleConfig.class);

    private final RoleRepository roleRepository;

    public RoleConfig(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Initializing roles...");
        loadRoles();
    }

    private void loadRoles() {
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
        createRoleIfNotExists("SUPER_ADMIN");
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName).ifPresentOrElse(
                role -> logger.info("Role '{}' already exists", roleName),
                () -> {
                    RoleEntity newRole = new RoleEntity();
                    newRole.setName(roleName);
                    roleRepository.save(newRole);
                    logger.info("Role '{}' created", roleName);
                }
        );
    }
}
