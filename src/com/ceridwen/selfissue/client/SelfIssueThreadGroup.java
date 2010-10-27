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
package com.ceridwen.selfissue.client;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

class SelfIssueClientThreadGroup extends ThreadGroup {
  private static Log log = LogFactory.getLog(SelfIssueClientThreadGroup.class);

  public SelfIssueClientThreadGroup(String name) {
    super(name);
  }

  public SelfIssueClientThreadGroup(ThreadGroup parent, String name) {
    super(parent, name);
  }

  public void uncaughtException(Thread t, Throwable e) {
    log.fatal("Uncaught exception:", e);
  }
}
