package it.marmas.task.manager.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

 
 
 	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String URL_RESET="http://localhost:8080/auth/change_password";
	private static final String URL_ACTIVATION="http://localhost:8080/auth/activate_account";
	private static final String TOKEN="token";
	
	@Value("${spring.mail.password}")
	private  String password;
	
	@Value("${spring.mail.username}")
	private  String from;

	 
	@Autowired
	private JavaMailSender mailSender;


    

 
	
	public boolean sendResetPasswordEmail(String emailTo,String token,String username) {
		try  {
			
		 String url=URL_RESET+"?"+TOKEN+"="+token;
		 String subject="Password Reset Request";
		 String content= loadResetPasswordTemplate(url,2025,username);
		 MimeMessage message= null;
		 
		message= createMessage(emailTo, subject, content);
		 
 		 mailSender.send(message);
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		 logger.info("email successfully sent");
		 
 		 return true;
	}
	
	public boolean sendActivationAccountEmail(String emailTo,String username,String token) {
		try {
		 logger.info("wrapping token in email : "+token);
		 String url=URL_ACTIVATION+"?"+TOKEN+"="+token;
		 String subject="Authentication Email ";
		 String content=loadActivationAccountTemplate(url,2025,username);
 		 MimeMessage message=null;
		 
		message= createMessage(emailTo, subject, content);
		 
 		 mailSender.send(message);
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		 logger.info("email successfully sent");
		 
		 logger.info("saving record in db");
 		 
 		 return true;
	}
/*	private String loadActivationAccountTemplate(String url,int year,String supportEmail) throws IOException {
		   ClassPathResource resource = new ClassPathResource("templates/activation-success.html");
		    String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
		  return   html.replace("{{year}}",""+year).replace("{{supportEmail}}",supportEmail);
			
	}
	 */
	private String loadActivationAccountTemplate(String url,int year,String username) throws IOException {
		   ClassPathResource resource = new ClassPathResource("templates/activation-request.html");
		    String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
		  return   html.replace("{{username}}",username).replace("{{link}}",url).replace("{{year}}", ""+year);
			
	}

	private String loadResetPasswordTemplate(String url,int year,String username) throws IOException {
	    ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
	    String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
	    return html.replace("{{resetUrl}}", url).replace("{{year}}", ""+year).replace("{{username}}",username);
		
	}
	private  MimeMessage createMessage(String emailTo, String subject ,String content) throws MessagingException {
		
		 MimeMessage message= mailSender.createMimeMessage();
		 
		 MimeMessageHelper helper= new MimeMessageHelper(message,true,"UTF-8");
		 helper.setTo(emailTo);
		 helper.setSubject(subject);
		 helper.setFrom(from);
		 helper.setText(content,true);
		 if(content.contains("logoImg")) {
			ClassPathResource img = new ClassPathResource("static/imgs/logo.png");
		 helper.addInline("logoImg", img);  
		 }
		 
		 return message;
	}

	@Override
//	@Async
	public void sendReminderEmail(String email,String username,String timezone, List<String> taskList) {
 		 String subject="tasks deadline";
 		 
		 try {
			String content= loadReminderTemplate(taskList,username);
			MimeMessage message =createMessage(email, subject, content);
			mailSender.send(message);;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ;
		} 
		 logger.info("email sent successfully");
		 return;
	}
	
	private String loadReminderTemplate(List<String>taskList,String username) throws IOException {
		  StringBuilder tasksHtml = new StringBuilder();
	        for (String task : taskList) {
	            tasksHtml.append("<li>").append(task).append("</li><br />");
	        }
 	    ClassPathResource resource = new ClassPathResource("templates/reminder.html");
	    String html = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
	    return html.replace("{{username}}", username).replace("{{tasks}}",tasksHtml.toString());
		
	}
}
