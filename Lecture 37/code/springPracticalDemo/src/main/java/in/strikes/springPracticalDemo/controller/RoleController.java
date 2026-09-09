package in.strikes.springPracticalDemo.controller;

import in.strikes.springPracticalDemo.entity.Role;
import in.strikes.springPracticalDemo.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private RoleService rolesService;

    public RoleController(RoleService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping
    public ResponseEntity<String> addRole(@RequestBody Role role) {
        rolesService.addRole(role);
        return ResponseEntity.ok("DONE");
    }
}
