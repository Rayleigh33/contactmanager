package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.dao.ContactRepository;
import com.contact.dao.StudentRepository;
import com.contact.entities.Contact;
import com.contact.entities.Student;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
    StudentRepository studentRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model) {
        Student student = studentRepository.getStudentByUserName("har@gmail.com");
		
		System.out.println("student:::::   " + student.getEmail() + " " + student.getId() + " " + student.getName() + " " + student.getRole());
		model.addAttribute("student",student);
	}
	
	//dashboard handler
	@GetMapping("/index")
	public String dashboard(Model model) {
		
		return "normal/user_dashboard";
	}
	
	//handler to add form
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("contact",new Contact());
		return "normal/add_contact";
	}
	
	//data submission handler
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file) {
		
		try {
			String name = "har@gmail.com";
			Student student = this.studentRepository.getStudentByUserName(name);
			
			contact.setStudent(student);
			
			if(file.isEmpty()) {
				System.out.println("image field is empty");
				contact.setImage("contact.png");			}
			
			else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				
				System.out.println("----------"+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("image uploaded succesfully");
			}
			
			student.getContacts().add(contact);
			this.studentRepository.save(student);
			System.out.println("contact info::::   " + contact);
			
			System.out.println("contact added to database");
			
			
		} catch (Exception e) {
			System.out.println("error:  "+e.getMessage());
			
			
		}
		return "/normal/add_contact";
	}
	
	//show all contacts
	@GetMapping("/show-contacts/{page}")
	public String showAllContacts(@PathVariable("page") Integer page ,Model model) {
		String userName = "har@gmail.com";
		
		
		
		Student student = this.studentRepository.getStudentByUserName(userName);
		
		//current page - page
				//contact per page - 5
				Pageable pageable = PageRequest.of(page, 5);
		
	    Page<Contact> contacts =	this.contactRepository.findContactsByStudent(student.getId(),pageable);
	    model.addAttribute("contacts",contacts);
	    model.addAttribute("currentPage",page);
	    model.addAttribute("totalPages",contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	//showing single contact
	@GetMapping("/contact/{id}")
	public String singleContact(@PathVariable("id") Integer id, Model model) {
		
		Optional<Contact> optionalContact = this.contactRepository.findById(id);
		Contact contact = optionalContact.get();
		
		String userName = "har@gmail.com";
		Student student = this.studentRepository.getStudentByUserName(userName);
		
		if(student.getId() == contact.getStudent().getId()) {
			model.addAttribute("contact",contact);
		}
		
		return "normal/single_contact";
	}
	
	//delete contact handler
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") Integer id) {
		Contact contact = this.contactRepository.findById(id).get();
		
		contact.setStudent(null);
		
		this.contactRepository.delete(contact);
		return "redirect:/user/show-contacts/0";
	}
	
	//open update contact form handler
	@PostMapping("/update-contact/{id}")
	public String updateContact(@PathVariable("id") Integer id, Model model) {
		Contact contact = this.contactRepository.findById(id).get();
		model.addAttribute("contact",contact);
		return "normal/update_contact";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,Model model, 
			@RequestParam("profileImage") MultipartFile file, HttpSession session) {
		
		try {
			
			Contact oldContact = this.contactRepository.findById(contact.getId()).get();
			
			if(!file.isEmpty()) {
				//file reupload
				
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldContact.getImage());
			}
			
			Student student = this.studentRepository.getStudentByUserName("har@gmail.com");
			contact.setStudent(student);
			
			this.contactRepository.save(contact);
		} catch (Exception e) {
			System.out.println("error:  "+e.getMessage());
		}
		
		return "redirect:/user/contact/"+contact.getId();
	}
	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile() {
		return "normal/profile";
	}
}
