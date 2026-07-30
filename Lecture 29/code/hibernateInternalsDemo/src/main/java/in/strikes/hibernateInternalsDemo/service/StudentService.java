package in.strikes.hibernateInternalsDemo.service;

import in.strikes.hibernateInternalsDemo.model.Student;
import in.strikes.hibernateInternalsDemo.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Transactional
    public Student getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Transactional
    public void updateStudent(Student studentReq, Long id) {
        Student student1 = studentRepository.findById(id);

        if(student1 == null) {
            throw new RuntimeException("Student not found");
        }

        //studentRepository.detach(student1);

        //student1 = studentRepository.attachAgain(student1);

        student1.setName(studentReq.getName());
        studentRepository.flush();

        student1.setAge(studentReq.getAge());
        studentRepository.flush();

        student1.setEmail(studentReq.getEmail());
        studentRepository.flush();
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id);

        if(student == null) {
            throw new RuntimeException("Student not found");
        }

        studentRepository.remove(student);
    }
}
