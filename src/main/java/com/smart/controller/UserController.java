package com.smart.controller;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.PaymentDataRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.PaymentData;
import com.smart.entities.User;
import com.smart.helper.FileUploadHelper;
import com.smart.helper.Msg;

import com.razorpay.*;

@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FileUploadHelper fileUploadHelper;

	@Autowired
	private PaymentDataRepository paymentDataRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// mehtor for adding common data for all
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
//		get the user using userName
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	// dashbord home

	@GetMapping("/dashbord")
	public String userDashobord(Model model, Principal principal) {
		String userName = principal.getName();
//		get the user using userName
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("title", user.getName() + "-Dashbord");
		return "normal/dashbord";
	}

	// open add from handler

	@GetMapping("/add-contact")
	public String openAddConteactForm(Model model) {
		model.addAttribute("title", "Add-Contact");
		model.addAttribute("header", "Add-Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// prosessing add contact form..

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("photo") MultipartFile multipartFile, Principal principal, Model model, HttpSession session) {

		try {

			String random = new Random(25000).toString();

			String name = principal.getName();

			User user = this.userRepository.getUserByUserName(name);

			contact.setUser(user);

			if (multipartFile.isEmpty()) {
				// file is empty

				if (contact.getImage().isBlank()) {
					contact.setImage("DefaultContact.png");
				}

			} else {
				// file have somthing

				if (!contact.getImage().isBlank() && !contact.getImage().equals("DefaultContact.png")) {
					fileUploadHelper.deleteFile(contact);
				}

				String fileNeme = user.getName().concat(random.concat(multipartFile.getOriginalFilename()));

				contact.setImage(fileNeme);

//				File file = new ClassPathResource("static/image").getFile();
//
//				Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
//
//				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				if (fileUploadHelper.uploadFile(multipartFile, fileNeme)) {
					System.out.println("\n\n File Save");
				}

			}

			user.getContacts().add(contact);

			this.userRepository.save(user);

			model.addAttribute("contact", new Contact());

//			message system
			session.setAttribute("msg", new Msg("Your contact added successfully  !!  Add More...", "green"));

			return "normal/add_contact_form";
		} catch (Exception e) {
			e.printStackTrace();

			session.setAttribute("msg", new Msg("Somthing went wrong  !!  Try again...", "red"));

			return "normal/add_contact_form";
		}
	}

//	Show Contact handler..
// 	per page = 6[n]
//	current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "Show-Contacts");

//		Contact list

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);
//		
//		List<Contact> contacts = user.getContacts();

		// current page and connect per page
		Pageable pageRequest = PageRequest.of(page, 6);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageRequest);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

//	showing perticular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> optional = this.contactRepository.findById(cId);

		Contact contact = optional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		} else {
			model.addAttribute("title", "ERROR");
		}

		return "normal/contact_detail";

	}

	@GetMapping("/delete-contact/{cId}/{currentPage}")
	public String deleteContact(@PathVariable("cId") Integer cid, @PathVariable("currentPage") Integer currentpage,
			Model model, Principal principal, HttpSession session) {

		Contact contact = this.contactRepository.findById(cid).get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			contact.setUser(null);

			if (contact.getImage().equals("DefaultContact.png")) {

				this.contactRepository.delete(contact);
				session.setAttribute("msg", new Msg("Connect deleted successfully...", "green"));
			} else {

				if (fileUploadHelper.deleteFile(contact)) {
					this.contactRepository.delete(contact);
					session.setAttribute("msg", new Msg("Connect deleted successfully...", "green"));
				}
			}

		} else {
			session.setAttribute("msg", new Msg("You are not authorized to delete this contact...", "red"));
		}

		return "redirect:/user/show-contacts/" + currentpage;
	}

//	Edit handlear
	@GetMapping("/update-contcat/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId, Model model) {

		model.addAttribute("title", "Update-Form");
		model.addAttribute("header", "Update-Contact");

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);

		return "normal/add_contact_form";
	}

	// Your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile page");
		return "normal/profile";
	}

	// profile satting handler
	@GetMapping("/setting")
	public String profileSetting(Model model, Principal principal) {
		User loginuser = userRepository.getUserByUserName(principal.getName());
		model.addAttribute("loginuser", loginuser);
		model.addAttribute("title", "setting");
		return "normal/setting";
	}

	// for update user Image
	@PostMapping("/updateImage")
	public String updateImage(@RequestParam("profileImage") MultipartFile mFile, Principal principal,
			HttpSession session) {
		User loginuser = userRepository.getUserByUserName(principal.getName());

		String originalFilename = mFile.getOriginalFilename();

		String fileName = loginuser.getName().concat(originalFilename);

		if (mFile != null) {

			if (!loginuser.getImageUrl().equals("default.png")) {
				if (fileUploadHelper.deleteUserProfile(loginuser.getImageUrl())) {
					System.out.println("\n\n\nFile Deleted\n\n\n");
				}
			}

			if (fileUploadHelper.uploadFile(mFile, fileName)) {
				session.setAttribute("msg", new Msg("Profile Update Successfully", "green"));
			}

			loginuser.setImageUrl(fileName);

			userRepository.save(loginuser);
		}

		return "redirect:/user/setting";
	}

	// for update user password
	@PostMapping("/updatePassword")
	public String updatePassword(Model model) {
		model.addAttribute("user", new User());
		return "normal/updatepassword";
	}

	// password update proccessing....
	@PostMapping("/doUpdatePassword")
	public String doUpdatePassword(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam("oldPassword") String oldPassword, Model model, Principal principal, HttpSession session) {

		try {

			String userName = principal.getName();

			String pass = userRepository.getPasswordByUserName(userName);

			if (!oldPassword.isEmpty() && !user.getPassword().isEmpty() && !user.getRePassword().isEmpty()) {

				if (passwordEncoder.matches(oldPassword, pass)) {

					if (user.getPassword().equals(user.getRePassword())) {

						user.setPassword(passwordEncoder.encode(user.getPassword()));
						user.setRePassword(passwordEncoder.encode(user.getRePassword()));

						userRepository.save(user);

						session.setAttribute("msg", new Msg("Password changed successfully", "green"));

						return "redirect:/user/setting";

					} else {
						session.setAttribute("msg",
								new Msg("New Password and Re-Enter New Password must be same...!", "red"));

					}

				} else {
					session.setAttribute("msg", new Msg("Wrong Password ... Enter Real password", "red"));
				}

			} else {
				session.setAttribute("msg", new Msg("Somthing is empty..", "red"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("msg", new Msg("Somthing Went Worng here....!!", "red"));
		}

		return "normal/updatepassword";

	}

	// for update user Bio
	@PostMapping("/updateBio")
	public String updateBio(Model model, Principal principal) {

		String userName = principal.getName();

		User user = userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);

		return "normal/updatebio";
	}

	@PostMapping("/doUpdateBio")
	public String doUpdateBio(@Valid @ModelAttribute("user") User user, BindingResult result, Principal principal,
			Model model, HttpSession session) {

		String userName = principal.getName();

		String userPassword = userRepository.getPasswordByUserName(userName);

		if (passwordEncoder.matches(user.getPassword(), userPassword)) {

			user.setPassword(userPassword);

			userRepository.save(user);

			session.setAttribute("msg", new Msg("Your BIO changed successfully", "green"));

		} else {
			session.setAttribute("msg", new Msg("Wrong password...Please Enter correct password", "red"));
		}

		return "normal/updatebio";
	}

	// payment system...

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {

		LocalDateTime time = LocalDateTime.now();

		int amount = Integer.parseInt(data.get("amount").toString());

		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_Af4jVQZjh1EYX7", "geNn7mq0iK7IZm6hpavIJAuy");

		JSONObject ob = new JSONObject();
		ob.put("amount", amount * 100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_253695");

//		creating new order

		Order order = razorpayClient.Orders.create(ob);

		System.out.println("Order : " + order);

		// if you can save this to your DB...

		PaymentData paymentData = new PaymentData();

		int price = order.get("amount");

		price = price / 100;

		paymentData.setAmount(price);
		paymentData.setOrderId(order.get("id"));
		paymentData.setPaymentId(null);
		paymentData.setRecipt(order.get("receipt"));
		paymentData.setStatus("created");
		paymentData.setUser(this.userRepository.getUserByUserName(principal.getName()));
		paymentData.setTime(time);

		this.paymentDataRepository.save(paymentData);

		return order.toString();
	}

	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

		PaymentData paymentData = this.paymentDataRepository.findByOrderId(data.get("order_id").toString());

		paymentData.setPaymentId(data.get("payment_id").toString());
		paymentData.setStatus(data.get("status").toString());

		this.paymentDataRepository.save(paymentData);


		return ResponseEntity.ok(Map.of("msg","updated"));
	}

}
