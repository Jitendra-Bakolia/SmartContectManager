package com.smart.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;

@Component
public class FileUploadHelper {

	public final String UPLOAD_PATH = new ClassPathResource("static/image").getFile().getAbsolutePath();

	public FileUploadHelper() throws IOException {

	}

	public boolean uploadFile(MultipartFile multipartFile, String fileName) {
		boolean f = true;

		try {
						
			Path path = Paths.get(UPLOAD_PATH + File.separator + fileName);

			Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

	public boolean deleteFile(Contact contact) {
		boolean f = false;

		try {
			Path path = Paths.get(UPLOAD_PATH + File.separator + contact.getImage());
			Files.delete(path);
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}
	
	public boolean deleteUserProfile(String profileName) {
		boolean f = false;
		
		try {
			Path path = Paths.get(UPLOAD_PATH + File.separator + profileName);
			Files.delete(path);
			f = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}

}
