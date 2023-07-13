package com.smart.forgot;

import java.security.Principal;
import java.util.Random;

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
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository repository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	Random random = new Random(1000);

	// Email id from open handler
	@GetMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {

		// generating otp of 4 digit
		int otp = random.nextInt(9999);

		System.out.println("My otp is : - > " + otp);

		// Write otp to send email ....

		String sub = "OTP From SCM";
		String msg = "<div style='border:1px solid #e2e2e2; padding:20px;'>" + "<h1>" + "<i>SCM</i> OTP is : " + otp
				+ "</h1>" + "</div>";

		if (this.emailService.sendEmail(sub, msg, email)) {

			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			return "verify_otp";

		} else {

			session.setAttribute("msg", new Msg("Check your email id...!", "red"));
			return "forgot_email_form";

		}

	}

	@PostMapping("/verify_otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {

		int myOtp = (int) session.getAttribute("otp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {
			// password change form....

			User user = this.repository.getUserByUserName(email);

			if (user == null) {
				// send error message
				session.setAttribute("msg", new Msg("User dose not exits with this email..!!", "red"));
				return "forgot_email_form";
			} else {
				// send change password form...

				model.addAttribute("user", new User());

				return "password_change_form";

			}

		} else {
			session.setAttribute("msg", new Msg("You have enterd wrong OTP  !", "red"));
			return "verify_otp";
		}

	}

	// password update proccessing....
	@PostMapping("/doUpdatePassword")
	public String doUpdatePassword(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			HttpSession session) {

		try {

			String userName = (String) session.getAttribute("email");

			User realUser = this.repository.getUserByUserName(userName);

			if (user.getPassword().equals(user.getRePassword())) {
				realUser.setPassword(passwordEncoder.encode(user.getPassword()));
				realUser.setRePassword(passwordEncoder.encode(user.getPassword()));

				this.repository.save(realUser);

				session.setAttribute("msg", new Msg("Password change successfully...", "green"));

				return "signin";

			} else {
				// send unequals...error
				session.setAttribute("msg", new Msg("Both password's are not match .. !", "red"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "password_change_form";

	}

}
