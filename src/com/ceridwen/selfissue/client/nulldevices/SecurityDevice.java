/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
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
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package com.ceridwen.selfissue.client.nulldevices;

import org.w3c.dom.Node;

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
	public void init(Node node) {
	  return;
	}

	/* (non-Javadoc)
	 * @see com.ceridwen.circulation.devices.SecurityDevice#deinit()
	 */
	@Override
	public void deinit() {
    return;
	}

	@Override
	public void reset() {
    return;
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
		return locked;
	}
}
