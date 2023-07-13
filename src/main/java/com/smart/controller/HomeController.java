package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Msg;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("title", "Home ~ SmartContect");
		model.addAttribute("Head", "Hi this is head part");

		return "home";
	}

	@GetMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "Sign-Up ~ SmartContect");
		model.addAttribute("user", new User());

		return "signup";
	}

	@PostMapping("register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "check", defaultValue = "false") boolean agree, Model model, HttpSession session) {

		try {
			if (!agree) {
				session.setAttribute("msg", new Msg("You have not agreed terms and condition", "red"));
				return "signup";
			}

			if (!user.getPassword().equals(user.getRePassword())) {
				session.setAttribute("msg", new Msg("Both password's are not same..!!", "red"));
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setRePassword(passwordEncoder.encode(user.getRePassword()));

			userRepository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("msg", new Msg("Registration Successfully", "green"));

			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("msg", new Msg("Email already registerd", "red"));
			return "signup";

		}

	}

	@GetMapping("/signin")
	public String signIn() {
		return "signin";
	}

}
