package com.ceridwen.selfissue;
import com.ceridwen.selfissue.client.config.Configuration;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class EncodePassword {
  public EncodePassword() {
  }
  public static void main(String[] args) {
	System.out.println("SelfIssue Password Encoder");
	if (args.length != 1) {
	      System.out.println("Usage: encode <password>");		
	} else {
	    String enc = Configuration.Encrypt(args[0]);
	    if (enc != null && enc.length() > 0) {
	      System.out.println(enc);
	    } else {
	      System.out.println("Usage: encode <password>");
	    }
	}
  }
}
