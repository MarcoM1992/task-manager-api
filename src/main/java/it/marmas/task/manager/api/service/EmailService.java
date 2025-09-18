package it.marmas.task.manager.api.service;

import java.util.List;

public interface EmailService {
	public boolean sendActivationAccountEmail(String emailTo,String username,String token);
	boolean sendResetPasswordEmail(String email,String token,String username);
	public void sendReminderEmail(String email,String key,String timezone, List<String> value);
}
