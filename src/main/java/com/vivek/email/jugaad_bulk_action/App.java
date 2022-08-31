package com.vivek.email.jugaad_bulk_action;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;


/**
 * Hello world! Let's do bulk-reply And Apply with same content to emails with a label without changing subject
 *
 */
public class App 
{
	
	public static void main( String[] args )
	{
		System.out.println( "Hello World!" );

		final String mailStoreType = "imaps";
		final String smtpHost = "smtp.gmail.com";
		final String username = "your_user_name.";
		final String password = "your_password.";//for Gmail use app password 

		Properties prop = new Properties();
		prop.put("mail.smtp.host", smtpHost);
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); //TLS

		Session session = Session.getInstance(prop,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Store mailStore;
		try {
			mailStore = session.getStore(mailStoreType);

			mailStore.connect(smtpHost, username, password);
			
			String content = "Hi,\r\n"
					+ "\r\n"
					+ "https://drive.google.com/file/d/1mHprEmR7XQIv3mQ5RlDzZ3OnZt0RP67U/view?usp=drivesdk \r\n"
					+ "\r\n"
					+ "---\r\n"
					+ "Thanks & Regards\r\n"
					+ "Vivek\r\n";
			
			commonReplyAndApplyToAllUnreadLabelled(content, mailStore, "JOB/ExtraJobs");
			
			mailStore.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void commonReplyAndApplyToAllUnreadLabelled(String content, Store mailStore, String label) 
			throws MessagingException, IOException {

		Folder folder = mailStore.getFolder(label);
		
		folder.open(Folder.READ_WRITE);

		Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
	
		//reply will set opened message as seen automatically, so sending false to unseen it
		BulkReply.bulkReplyToAllMessages(content, messages, false);
		
		//now apply will set unseen message back to seen.
		BulkApply.bulkApply(messages);
		
		System.out.println("Total Message - " + folder.getMessageCount()+" "+ folder.getDeletedMessageCount()+" "
				+folder.getNewMessageCount()+" "+ folder.getUnreadMessageCount());
		
		folder.close();
		
	}

}
