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
package com.ceridwen.selfissue.client;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.ConnectionFactory;

public class SelfIssueThread extends Thread {
	private static Log log = LogFactory.getLog(SelfIssueThread.class);

	private SelfIssueFrame frame;
	private final boolean packFrame = false;

	public SelfIssueThread(ThreadGroup parent, String name) {
	    super(parent, name);
	}

	private void watchdog(int watchdogTimer, int watchdogThreshold,
			int watchdogMinimumMemory, final int watchdogMaximumErrors,
			boolean outOfOrderOnFail) {
		int watchdogErrors = 0;
	      int watchdog = watchdogThreshold;
	
	      while (true) {
	        if (ConnectionFactory.areConnectionsActive()) {
	          watchdog--;
	          if (watchdog < 1) {
	            if (watchdogErrors < watchdogMaximumErrors) {
	              log.error("Watchdog timeout in critical section");
	              watchdogErrors++;
	            } else {
	            	if (outOfOrderOnFail) {
	            		frame.setOutOfOrderPanel();
	            	}
	            }
	            watchdog = 0;
	          }
	        } else {
	          watchdog = watchdogThreshold;
	          watchdogErrors = 0;
	        }
	
	        Runtime runtime = Runtime.getRuntime();
	        long availableMemory = (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()))/(1024*1024);
	        if (availableMemory < watchdogMinimumMemory) {
	          log.fatal("Low Memory: " + availableMemory);
	      	  if (outOfOrderOnFail) {
	    		frame.setOutOfOrderPanel();
	    	  }
	        }
	
	        try {
	          sleep(watchdogTimer);
	        } catch (Exception ex) {
	          log.debug("Sleep failure: ", ex);
	        }
	      }
	}

	private void initiateTimeSync() {
     // TODO: Not implemented
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {    		
		}
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private void initiateUI() {
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
		} catch (Throwable t) {
		}
		  
		  
		frame = new SelfIssueFrame();
	
	    //Validate frames that have preset sizes
	    //Pack frames that have useful preferred size info, e.g. from their layout
	    if (packFrame) {
	      frame.pack();
	    }
	    else {
	      frame.validate();
	    }
	    //Center the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = frame.getSize();
	        
	    if (frameSize.height > screenSize.height) {
	      frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width) {
	      frameSize.width = screenSize.width;
	    }
	        
	    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	
	    frame.setVisible(true);
	    ThreadGroup tg = this.getThreadGroup();
	    if (tg instanceof SelfIssueClientThreadGroup) {
	    	((SelfIssueClientThreadGroup)tg).setFrame(frame);
	    }
	  }

	public void run() {
	    int WatchDogThreshold = Configuration.getIntProperty("UI/WatchDog/CriticalSectionThreshold");
	    int WatchDogTimer = Configuration.getIntProperty("UI/WatchDog/Timer") * 1000;
	    int WatchDogMinimumMemory = Configuration.getIntProperty("UI/WatchDog/MinimumMemory");
	    boolean ooo = Configuration.getBoolProperty("UI/WatchDog/ShowOutOfOrderScreenOnWatchDogProblem");
	    final int WatchDogMaximumErrors = 5;
	
	    try {
			this.setLookAndFeel();
	    	this.initiateTimeSync();	
	    	this.initiateUI();
	    	watchdog(WatchDogTimer, WatchDogThreshold, WatchDogMinimumMemory,
				WatchDogMaximumErrors, ooo);
	    }
	    catch(Exception e) {
	      log.fatal("Main thread failure: ", e);
	    }
	  }
}
