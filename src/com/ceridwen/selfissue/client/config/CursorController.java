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