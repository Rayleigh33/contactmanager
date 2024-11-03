package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	  @Query(value = "SELECT * from contact where contact.student_id = :studentId",nativeQuery = true)
      public Page<Contact> findContactsByStudent(@Param("studentId") int studentId, Pageable pageable);
}
