/* 
 * Copyright (C) 2019 Ceridwen Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ceridwen.selfissue.client.core;

import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.selfissue.client.devices.FailureException;
import com.ceridwen.selfissue.client.devices.IDReaderDeviceListener;
import com.ceridwen.selfissue.client.devices.TimeoutException;

public interface CirculationHandler {
    public enum IDReaderDeviceType {
        PATRON_IDREADER,
        ITEM_IDREADER
    };

    public abstract Class<?> getSpoolerClass();

    public abstract void spool(Message msg);

    public abstract int getSpoolSize();

    public abstract Message send(Message request);

    public abstract void printReceipt(String data);

    public abstract String checkStatus(int statusCode);

    // public abstract Class<? extends IDReaderDevice> getRFIDDeviceClass();
    // public abstract Class<? extends SecurityDevice> getSecurityDeviceClass();
    public abstract void initIDReaderDevice(IDReaderDeviceType type);

    public abstract void startIDReaderDevice(IDReaderDeviceListener listener);

    public abstract void stopIDReaderDevice();

    public abstract void deinitIDReaderDevice();

    public abstract void initItemSecurityDevice();

    public abstract void lockItem() throws TimeoutException, FailureException;

    public abstract void unlockItem() throws TimeoutException, FailureException;

    public abstract boolean isItemLocked() throws TimeoutException, FailureException;

    public abstract void deinitItemSecurityDevice();

    void recordEvent(int level, String addInfo, Date originalTransactionTime, Message request, Message response);
}

/**
 * public List peekSpoolerContents() throws RemoteException; public boolean
 * hasSpoolerContentsChanged() throws RemoteException; public void purgeSpool()
 * throws RemoteException;
 **/
