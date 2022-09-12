package com.vivek.email.jugaad_bulk_action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

public class BulkReply {


	public static void bulkReplyToAllMessages(String content, Message[] messages, boolean toSet) 
			throws MessagingException, IOException {

		
		
		Arrays.sort(messages, (a,b)-> {try {
			return a.getReceivedDate().compareTo(b.getReceivedDate());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return 0;
		});
		
		System.out.println("Total Unread messages= "+messages.length);
		
		IntStream.range(0, messages.length)
			.forEach(num -> {
				try {
					Message reply = messages[num].reply(false);
					
					List<Address> addresses = new ArrayList<>();
					
					if(messages[num].getFrom()!=null) {
						addresses.addAll(Arrays.stream(messages[num].getFrom())
								.filter(from -> !from.toString().contains("@naukri"))
								.collect(Collectors.toList()));
					}
					
					if(messages[num].getRecipients(RecipientType.CC)!=null)
						addresses.addAll(Arrays.stream(messages[num].getRecipients(RecipientType.CC))
							.collect(Collectors.toList()));
//					if(messages[num].getRecipients(RecipientType.BCC)!=null)
//						addresses.addAll(Arrays.stream(messages[num].getRecipients(RecipientType.BCC))
//									.collect(Collectors.toList()));
					if(reply.getAllRecipients()!=null) {
						addresses.addAll(Arrays.stream(reply.getAllRecipients()).collect(Collectors.toList()));
					}
					
					if(addresses.size() == 0) {
						addresses.addAll(Arrays.stream(messages[num].getFrom()).collect(Collectors.toList()));
					}
					reply.setRecipients(RecipientType.TO, addresses.toArray(new Address[addresses.size()]));
					
					
					convertToHtml(content, reply);
					Transport.send(reply);
					messages[num].setFlag(Flag.SEEN, toSet);
					System.out.println("Processed num= "+num+" from= "+ InternetAddress.toString(messages[num].getFrom()) +" "
							+ messages[num].getReceivedDate());
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			});
		
	}
	
	public static Message convertToHtml(String content, Message reply) throws MessagingException {

	    Multipart multipart = new MimeMultipart( "alternative" );

	    MimeBodyPart htmlPart = new MimeBodyPart();
	    htmlPart.setContent( content, "text/html; charset=utf-8" );

	    multipart.addBodyPart( htmlPart );
	    reply.setContent( multipart );

	    // Unexpected output.
//	    System.out.println( "HTML = text/html : " + htmlPart.isMimeType( "text/html" ) );
//	    System.out.println( "HTML Content Type: " + htmlPart.getContentType() );

	    // Required magic (violates principle of least astonishment).
	    //reply.saveChanges();
	    return reply;
	}
}
