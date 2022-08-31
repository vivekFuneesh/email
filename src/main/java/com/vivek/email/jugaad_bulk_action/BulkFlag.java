package com.vivek.email.jugaad_bulk_action;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;

public class BulkFlag {

	public static void bulkChangeFlag(Flag changeTo, boolean toSet, Message[] messages) throws MessagingException {
		for(Message msg : messages) {
			msg.setFlag(changeTo, toSet);
		}
	}

}
