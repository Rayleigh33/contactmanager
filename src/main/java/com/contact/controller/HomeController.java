package com.contact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.dao.StudentRepository;
import com.contact.entities.Student;
import com.contact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	
	@Autowired
	StudentRepository studentRepository;
   
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	@GetMapping("/about")
	public String about() {
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("student",new Student());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("student") Student student,BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			Model model, HttpSession session) {
		
		try {
			
			if(!agreement) {
				System.out.println("you have not agreed to terms and conditions");
				throw new Exception("you have not agreed to terms and conditions");
			}
			
			if(result.hasErrors()) {
				System.out.println("error " + result.toString());
				model.addAttribute("student",student);
				return "signup";
			}
			
			student.setRole("STUDENT");
			student.setEnabled(true);
			//student.setPassword(passwordEncoder.encode(student.getPassword()));
			
			
			System.out.println("agreement " + agreement);
			System.out.println("student" + student);
			
			 this.studentRepository.save(student);
			
			model.addAttribute("student", new Student());
			
			session.setAttribute("message", new Message("successfully registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("student",student);
			session.setAttribute("message", new Message("something went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}
		
	}
	
	@GetMapping("/signin")
	public String customSignIn() {
		return "signin";
	}
}
