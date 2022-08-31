package com.vivek.email.jugaad_bulk_action;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BulkApply {


	public static void bulkApply(Message[] messages) throws MessagingException, IOException {
		for(Message msg: messages) {
			parseContentAndApply(msg);
		}
	}
	
	//assuming only one apply link is there..
	
	public static String  parseContentAndApply(Message message) throws MessagingException, IOException {
		System.out.println("Processing: "+" "+message.getSubject()+" "+ message.getReceivedDate());
		String result = "";
		if (message.isMimeType("text/*")) {
			result = fetchLinkFromContent(message.getContent().toString());
			
		} else if (message.isMimeType("multipart/*")) {
			
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = parseMimeMultipart(mimeMultipart);
		
		}
		
		
		if(result!=null && result.length()>0) {
			clickApply(result);
		} else {
			System.out.println("NO Apply Link Found!");
		}
		return result;
	}

	private static String parseMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
		String result = "";
		Elements eles =null;
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/*")) {
				
				eles = Jsoup.parse(bodyPart.getContent().toString()).select("a[href][title]");
				//considering only 1 apply link as of now.
				for(Element ele: eles) {
					if(ele.attr("title").equals("Send your Naukri profile to the recruiter in one click")) {
						return ele.attr("href");
					}
				}
				
			} else if (bodyPart.getContent() instanceof MimeMultipart){
				return parseMimeMultipart((MimeMultipart)bodyPart.getContent());
			}
		}
		
		return result;
	}
	
	public static String fetchLinkFromContent(String content) {
		Elements eles = Jsoup.parse(content).select("a[href][title]");
		for(Element ele : eles) {
			if(ele.attr("title").equals("Send your Naukri profile to the recruiter in one click")) {
				return ele.attr("href");
			}
		}
		
		return null;
	}

	public static void clickApply(String applyUrl) throws IOException {
		
		if ((applyUrl!=null) && (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE))) {

			Desktop desktop = java.awt.Desktop.getDesktop();
			
			URL url = new URL(applyUrl); 
			String nullFragment = null;
		    URI uri;
			try {
				uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
			    desktop.browse(uri);

				System.out.println("Apply link opened in browser");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("browsing from code not supported, for linux check gnu libraries installation ,"
					+ " for windows do debug!");
		}
			}

	

}
