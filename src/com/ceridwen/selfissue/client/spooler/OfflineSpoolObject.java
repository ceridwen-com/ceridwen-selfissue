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
package com.ceridwen.selfissue.client.spooler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.selfissue.client.config.Configuration;

public class OfflineSpoolObject implements java.io.Serializable {
    /**
	 * 
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3237582773044705455L;
	/**
	 * 
	 */
	
	public Date added = new Date();
    public boolean expired = false;
    public boolean stale = false;
    public Message message;

    public OfflineSpoolObject() {
      this.added = new Date();
      this.expired = false;
      this.stale = false;
      this.message = null;
    }

    public OfflineSpoolObject(Message message) {
      this.added = new Date();
      this.expired = false;
      this.stale = false;
      this.message = message;
    }

    public boolean isStale() {
      return this.stale;
    }

    public boolean isAboutToStale() {
      if (this.stale) {
        return false;
      } else {
        Date currentDate = new Date();
        long age = (currentDate.getTime() - this.added.getTime())/(60*60*1000);
        if (age > Configuration.getIntProperty("Systems/Spooler/OverdueAgeWarn")) {
          this.stale = true;
          return true;
        } else {
          return false;
        }
      }
    }

    public boolean isExpired() {
      return this.expired;
    }

  public Date getAdded() {
    return added;
  }

  public Message getMessage() {
    return message;
  }

  public boolean isAboutToExpire() {
      if (this.expired) {
        return false;
      } else {
        Date currentDate = new Date();
        long age = (currentDate.getTime() - this.added.getTime())/(60*60*1000);
        if (age > Configuration.getIntProperty("Systems/Spooler/OverdueAgeExpire")) {
          this.expired = true;
          return true;
        } else {
          return false;
        }
      }
    }

  private void readObject(ObjectInputStream ois) throws IOException,
      ClassNotFoundException {

    ois.defaultReadObject();
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {

    oos.defaultWriteObject();
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public void setStale(boolean stale) {
    this.stale = stale;
  }

}
