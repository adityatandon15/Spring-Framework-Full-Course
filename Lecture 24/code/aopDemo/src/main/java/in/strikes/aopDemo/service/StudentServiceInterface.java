package in.strikes.aopDemo.service;

import in.strikes.aopDemo.dto.Student;

public interface StudentServiceInterface {

    Student createStudent(Student student);

    String getStudent(String s);
}
