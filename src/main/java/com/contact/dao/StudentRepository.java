package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contact.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    //@Query("SELECT s FROM student s WHERE s.email =: email"
	@Query(value = "SELECT * FROM student where student.email = :email", nativeQuery = true)
    public Student getStudentByUserName(@Param("email") String email);
}
