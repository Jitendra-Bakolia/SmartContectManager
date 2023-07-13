package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.servlet.annotation.MultipartConfig;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@MultipartConfig
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;

	@Size(min = 2,max = 20,message = "Name should be between 2 to 20 characters")
	private String name;

	private String secondName;

	private String work;

	
	@Email(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\\\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\\\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
	private String email;

	@Size(min = 10,max = 13,message = "Phone number should be 10 to 13  digit")
  	private String phone;

	private String image;

	@Column(length = 1000)
	private String description;

	@ManyToOne
	@JsonIgnore
	private User user;

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Contact(int cId, String name, String secondName, String work, String email, String phone, String image,
			String description, User user) {
		super();
		this.cId = cId;
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}

	public Contact(String name, String secondName, String work, String email, String phone, String image,
			String description, User user) {
		super();
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	/*
	 * @Override public String toString() { StringBuilder builder = new
	 * StringBuilder();
	 * builder.append("Contact [cId=").append(cId).append(", name=").append(name).
	 * append(", secondName=")
	 * .append(secondName).append(", work=").append(work).append(", email=").append(
	 * email).append(", phone=")
	 * .append(phone).append(", image=").append(image).append(", description=").
	 * append(description) .append(", user=").append(user).append("]"); return
	 * builder.toString(); }
	 */
}
