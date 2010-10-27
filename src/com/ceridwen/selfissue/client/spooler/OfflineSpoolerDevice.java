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
package com.ceridwen.selfissue.client.spooler;

import java.io.File;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import com.ceridwen.util.PersistentQueue;
import com.ceridwen.util.Spooler;
import com.ceridwen.util.SpoolerProcessor;

public class OfflineSpoolerDevice implements OfflineSpooler {
  private Spooler spool;

  public OfflineSpoolerDevice(File file, SpoolerProcessor processor, int period) {
    spool = new Spooler(new PersistentQueue(file), processor, period);
  }

  public void add(OfflineSpoolObject m) {
    spool.add(m);
  }
  public int size() {
    return spool.size();
  }
  protected void finalize() throws java.lang.Throwable {
    spool.cancelScheduler();
    super.finalize();
  }
}
