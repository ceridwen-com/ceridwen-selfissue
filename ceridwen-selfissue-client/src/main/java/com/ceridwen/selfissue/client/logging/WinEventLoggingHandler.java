/*
 * Copyright (C) 2024 Ceridwen Limited
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
package com.ceridwen.selfissue.client.logging;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Matthew
 */
public final class WinEventLoggingHandler extends Handler {
	private static final String DEFAULT_SOURCE = "ceridwen.com";
	/**
	 * 
	 */
	private String _source = null;
	private String _server = null;

	private HANDLE _handle = null;

	/**
	 * @param server The server for remote logging
	 * @param source The Event View Source
	 */
	public WinEventLoggingHandler(String server, String source) {
		if (source == null || source.length() == 0) {
			source = DEFAULT_SOURCE;
		}

		this._server = server;
		setSource(source);
	}

	/**
	 * The <b>Source</b> option which names the source of the event. The current
	 * value of this constant is <b>Source</b>.
     * @param source
	 */
	public void setSource(String source) {

		if (source == null || source.length() == 0) {
			source = DEFAULT_SOURCE;
		}

		_source = source.trim();
	}

	/**
	 * @return
	 */
	public String getSource() {
		return _source;
	}

	/**
	 * 
	 */
        @Override
	public void close() {
		if (_handle != null) {
			if (!Advapi32.INSTANCE.DeregisterEventSource(_handle)) {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
			_handle = null;
		}
	}

	/**
	 * 
	 */
	private void registerEventSource() {
		close();

		try {
			_handle = registerEventSource(_server, _source);
		} catch (Exception e) {
			close();
//			throw new RuntimeException("Could not register event source.", e);
		}
	}

	/**
	 * 
	 */
	public void activateOptions() {
		registerEventSource();
	}

	
        
        
        
        @Override
        public void publish(LogRecord record) {
		if (_handle == null) {
			registerEventSource();
		}

		final int messageID = 0x1000;

		String[] buffer = { record.getMessage() };

		if (Advapi32.INSTANCE.ReportEvent(_handle, getEventLogType(record.getLevel()),
				getEventLogCategory(record.getLevel()), messageID, null, buffer.length, 0, buffer, null) == false) {
			//Exception e = new Win32Exception(Kernel32.INSTANCE.GetLastError());
			// error("Failed to report event [" + s + "].", event, e);
		}
	}

	/**
	 * @param server The server for remote logging
	 * @param source The Event View Source
	 * @param application The Event View application (location)
	 * @param eventMessageFile The message file location in the file system
	 * @param categoryMessageFile The message file location in the file system
	 * @return
	 */
	private HANDLE registerEventSource(String server, String source) {
		HANDLE h = Advapi32.INSTANCE.RegisterEventSource(server, source);
		if (h == null) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		return h;
	}

	/**
	 * Convert log4j Priority to an EventLog type. The log4j package supports 8
	 * defined priorities, but the NT EventLog only knows 3 event types of
	 * interest to us: ERROR, WARNING, and INFO.
	 * 
	 * @param level
	 *            Log4j priority.
	 * @return EventLog type.
	 */
	private static int getEventLogType(Level level) {
            if (level == Level.SEVERE) 
                return WinNT.EVENTLOG_ERROR_TYPE;
            else if (level == Level.WARNING)
                return WinNT.EVENTLOG_WARNING_TYPE;
            else
		return WinNT.EVENTLOG_INFORMATION_TYPE;
	}

	/**
	 * Convert log4j Priority to an EventLog category. Each category is backed
	 * by a message resource so that proper category names will be displayed in
	 * the NT Event Viewer.
	 * 
	 * @param level  Log4J priority.
	 * @return EventLog category.
	 */
	private static int getEventLogCategory(Level level) {
            if (level == Level.SEVERE) 
		return 5;
            if (level == Level.WARNING) 
		return 4;
            if (level == Level.INFO)
		return 3;
            if (level == Level.FINE)
		return 2;
            else		
                return 1;
	}

    @Override
    public void flush() {
    }
}    

