package in.strikes.springSecurityDemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @GetMapping
    public ResponseEntity<String> getStudents() {
        return ResponseEntity.ok("Student retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<String> createStudent() {
        return ResponseEntity.ok("Student created successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteStudent() {
        return ResponseEntity.ok("Student deleted successfully");
    }

    @PutMapping
    public ResponseEntity<String> updateStudent() {
        return ResponseEntity.ok("Student updated successfully");
    }

    @GetMapping("/csrf")
    public CsrfToken getToken(CsrfToken csrfToken) {
        return csrfToken;
    }

}
