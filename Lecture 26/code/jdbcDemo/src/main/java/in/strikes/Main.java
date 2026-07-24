package in.strikes;

import in.strikes.model.Student;
import in.strikes.repository.StudentRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        StudentRepository studentRepository = new StudentRepository();

        //studentRepository.createStudent(new Student("Rohan", "rohan@gmail.com", 21));

//        studentRepository.updateStudent(
//                new Student("Rohit Negi", "rohit@gmail.com", 26), 8L);

        //studentRepository.deleteStudent(9L);

        studentRepository.getStudent();

    }
}








