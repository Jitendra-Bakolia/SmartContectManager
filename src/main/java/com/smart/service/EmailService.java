package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String sub, String msg, String to) {

		// rest of the code

		boolean f = false;

		String from = "jkdevil25@gmail.com";

		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("jkdevil25@gmail.com", "Jiten@1998");
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(sub);
//			message.setText(msg);
			 message.setContent(msg,"text/html");

			System.out.println("Sending .......");

			Transport.send(message);

			System.out.println("Message send successfully ...  ");

			f = true;

		} catch (Exception e) {
			e.printStackTrace();

		}
		return f;
	}
	
}
