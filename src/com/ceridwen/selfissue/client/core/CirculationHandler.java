package com.ceridwen.selfissue.client.core;

import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;

public interface CirculationHandler {

	public abstract Class getSpoolerClass();
	public abstract void spool(Message msg);
	public abstract int getSpoolSize();

	public abstract Message send(Message request);

	public abstract void printReceipt(String data);

	public abstract String checkStatus(int statusCode);

	public abstract Class getSecurityDeviceClass();
	public abstract void stopSecurityDevice();
	public abstract void startSecurityDevice(SecurityListener listener);
	public abstract void initSecurityDevice();
	public abstract void deinitSecurityDevice();
	public abstract void resetSecurityDevice(); 
	public abstract void pauseSecurityDevice(); 
	public abstract void resumeSecurityDevice();
	public abstract void lockItem() throws TimeoutException, FailureException;
	public abstract void unlockItem() throws TimeoutException, FailureException;

	void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request, Message response);
}

/**
public List peekSpoolerContents() throws RemoteException;
public boolean hasSpoolerContentsChanged() throws RemoteException;
public void purgeSpool() throws RemoteException;

**/