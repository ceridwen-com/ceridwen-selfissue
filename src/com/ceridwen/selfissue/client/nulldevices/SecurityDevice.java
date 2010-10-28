/**
 * 
 */
package com.ceridwen.selfissue.client.nulldevices;

import com.ceridwen.circulation.devices.FailureException;
import com.ceridwen.circulation.devices.TimeoutException;

/**
 * @author Matthew
 *
 */
public class SecurityDevice extends
		com.ceridwen.circulation.devices.SecurityDevice {
	
	boolean locked;

	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#deinit()
	 */
	@Override
	public void deinit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#lock()
	 */
	@Override
	public void lock() throws TimeoutException, FailureException {
		locked = true;
	}

	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#unlock()
	 */
	@Override
	public void unlock() throws TimeoutException, FailureException {
		locked = false;
	}

	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#isLocked()
	 */
	@Override
	public boolean isLocked() throws TimeoutException, FailureException {
		// TODO Auto-generated method stub
		return locked;
	}
}
