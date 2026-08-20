package in.strikes.springPracticalDemo.service;

import in.strikes.springPracticalDemo.entity.Role;
import in.strikes.springPracticalDemo.repository.RoleRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void addRole(Role role) {
        roleRepository.save(role);
    }
}
