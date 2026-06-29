package in.strikes.crudSpringBootDemo.repository;

import in.strikes.crudSpringBootDemo.entity.Student;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {

    public Student saveStudent(Student studentReq) {
        // save to DB
        System.out.println("Inside Student Repository");
        System.out.println("Exiting Student Repository");

        Student s1 = new Student();
        s1.setName("Aditya");
        s1.setAge(28);
        s1.setEmail("aditya@gmail.com");
        s1.setRollNo(101);
        s1.setSubject("Spring Framework");

        return s1;
    }
}

