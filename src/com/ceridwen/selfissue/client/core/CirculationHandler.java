/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client.core;

import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;

public interface CirculationHandler {

	public abstract Class<?> getSpoolerClass();
	public abstract void spool(Message msg);
	public abstract int getSpoolSize();

	public abstract Message send(Message request);

	public abstract void printReceipt(String data);

	public abstract String checkStatus(int statusCode);

	public abstract Class<?> getSecurityDeviceClass();
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
