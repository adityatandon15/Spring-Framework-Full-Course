package in.strikes.hibernateInternalsDemo.repository;

import in.strikes.hibernateInternalsDemo.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class StudentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // create
    public void save(Student student) {
        entityManager.persist(student);
    }

    public void saveAll(List<Student> students) {

        int counter = 0;

        for(Student student : students) {

            entityManager.persist(student);
            counter++;

            if(counter % 100 == 0) {
                entityManager.flush();
                entityManager.clear();
                // entityManager.refresh(student);
            }
        }
    }

    // read
    public Student findById(Long id) {
        Student s1 = entityManager.find(Student.class, id);

        //entityManager.contains(s1);

        return s1;
    }

    // delete
    public void remove(Student student) {
        entityManager.remove(student);
    }

    public void detach(Student student) {
        entityManager.detach(student);
    }

    public Student attachAgain(Student student) {
        return entityManager.merge(student);
    }

    public void flush() {
        entityManager.flush();
    }
}
