package com.coderarmy.jpaRelationshipDemo.repository;

import com.coderarmy.jpaRelationshipDemo.model.Department;
import com.coderarmy.jpaRelationshipDemo.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class StudentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Student student) {
        entityManager.persist(student);
    }


    public Student findById(Long id) {
        return entityManager.find(Student.class, id);
    }

    @EntityGraph(attributePaths = {"department", "profile"})
    public List<Student> findAll() {

    }

}
