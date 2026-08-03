package in.strikes.springDataJPADemo.controller;

import in.strikes.springDataJPADemo.entity.Student;
import in.strikes.springDataJPADemo.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<String> createStudent(
            @RequestBody Student student) {

        studentService.createStudent(student);
        return ResponseEntity.ok("DONE");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(
            @PathVariable Long id) {
        Student student = studentService.fetchStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("name/{name}")
    public ResponseEntity<List<Student>> getStudent(
            @PathVariable String name) {
        List<Student> students = studentService.fetchAll(name);
        return ResponseEntity.ok(students);
    }
}
