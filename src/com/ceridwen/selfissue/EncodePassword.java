package com.ceridwen.selfissue;
import java.rmi.dgc.VMID;
import com.ceridwen.selfissue.client.Configuration;

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
    String enc = Configuration.Encrypt(args[0]);
    System.out.println(enc);
    Configuration.Decrypt(enc);
  }

}