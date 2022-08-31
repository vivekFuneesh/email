package com.vivek.email.jugaad_bulk_action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BulkSend {

	private static Map<String, Message.RecipientType> recipientType;
	
	static {
		recipientType = new HashMap<String, Message.RecipientType>();
		recipientType.put("TO", Message.RecipientType.TO);
		recipientType.put("CC", Message.RecipientType.CC);
		recipientType.put("BCC", Message.RecipientType.BCC);
		
	}
	
	public static void sendTo(Session session, String[] recipients, String subject, String content, String from, 
			String recipientsType) {
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(
					recipientType.get(recipientsType),
					InternetAddress.parse(Arrays.stream(recipients).collect(Collectors.joining(",")))
					);
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);
			
			System.out.println("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	

}
