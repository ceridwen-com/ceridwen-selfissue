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
package com.ceridwen.selfissue.client.config;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class CursorController {
    public static final Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final int delay = 500; // in milliseconds

    private CursorController() {}
    
    public static AbstractAction createAction(final String item, final ImageIcon icon, final Component component, final AbstractAction mainAbstractAction) {
    	AbstractAction abstractAction = new AbstractAction(item, icon) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -4837119342387430823L;

			public void actionPerformed(final ActionEvent ae) {
                
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                    	component.setCursor(busyCursor);
                    }
                };
                Timer timer = new Timer(); 
                
                try {   
                    timer.schedule(timerTask, delay);
                    mainAbstractAction.actionPerformed(ae);
                } finally {
                    timer.cancel();
                    component.setCursor(defaultCursor);
                }
            }
        };
        return abstractAction;
    }
}