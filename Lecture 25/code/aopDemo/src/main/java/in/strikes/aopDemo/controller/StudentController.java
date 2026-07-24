package in.strikes.aopDemo.controller;

import in.strikes.aopDemo.dto.Student;
import in.strikes.aopDemo.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student s = studentService.createStudent(student);
        return ResponseEntity.ok(s);
    }

    @GetMapping
    public ResponseEntity<String> getStudenet() {
        String s = "All Student Data";
        return ResponseEntity.ok(studentService.getStudent(s));
    }
}
