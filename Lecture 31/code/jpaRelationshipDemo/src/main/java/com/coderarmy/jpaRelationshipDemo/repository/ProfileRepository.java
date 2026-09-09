package com.coderarmy.jpaRelationshipDemo.repository;

import com.coderarmy.jpaRelationshipDemo.model.Profile;
import com.coderarmy.jpaRelationshipDemo.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Profile profile) {
        entityManager.persist(profile);
    }
}
