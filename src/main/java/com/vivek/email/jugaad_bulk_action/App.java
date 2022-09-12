package com.vivek.email.jugaad_bulk_action;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;


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
		final String username = "vivek@gmail.com";
		final String password = "";//for Gmail use app password 

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
			
			String content = "<html><body>"
					+ ""
					+ "Hi, "
					+ " <br><br>"
					+ "<b>Brief-Resume Link(viewable, downloadable & updates will be reflected):</b>\n"
					+ " https://drive.google.com/file/d/1mHprEmR7XQIv3mQ5RlDzZ3OnZt0RP67U/view?usp=drivesdk  "
					+ " <br><br><br>"
					+ "--- <br>"
					+ "<b>Thanks & Regards "
					+ "<br>"
					+ "Vivek "
					+ "<br>"
					+ ""
					+ "</html></body>";
			commonReplyAndApplyToAllUnreadLabelled(content, mailStore, "JOB/ExtraJobs");
			//bulkApply(mailStore, "JOBS/ExtraJobs");
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
		System.out.println(messages.length+"\n\n"+ messages[0].getReceivedDate().getTime());

		
		BulkReply.bulkReplyToAllMessages(content, messages, false);
		
		//now apply will set unseen message back to seen.
		
		BulkApply.bulkApply(messages);
		
		System.out.println("Total Message - " + folder.getMessageCount()+" "+ folder.getDeletedMessageCount()+" "
				+folder.getNewMessageCount()+" "+ folder.getUnreadMessageCount());
		
		folder.close();
		
	}
	
	public static void bulkApply(Store mailStore, String label) throws MessagingException, IOException {
		Folder folder = mailStore.getFolder(label);
		
		folder.open(Folder.READ_WRITE);

		Message[] messages = folder.search(new FlagTerm( new Flags(Flag.SEEN), false));

		System.out.println("found messages: =" + messages.length);
		
		BulkApply.bulkApply(messages);
		//BulkFlag.bulkChangeFlag(Flag.ANSWERED, true, messages);
		folder.close();
	}

}
