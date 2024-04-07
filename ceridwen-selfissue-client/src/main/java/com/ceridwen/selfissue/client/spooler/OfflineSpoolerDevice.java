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
package com.ceridwen.selfissue.client.spooler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.collections.Queue;
import com.ceridwen.util.collections.Spooler;
import com.ceridwen.util.collections.SpoolerProcessor;


public class OfflineSpoolerDevice implements OfflineSpooler {
  private final Spooler<OfflineSpoolObject> spool;
  private static final long delay = 10000;

  public OfflineSpoolerDevice(String file, SpoolerProcessor<OfflineSpoolObject> processor, int period) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    Queue<OfflineSpoolObject> persistentQueue = Configuration.getPersistentQueue(file);
    spool = new Spooler<>(persistentQueue, processor, delay, period); 
  }

  @Override
  public void add(OfflineSpoolObject m) throws IOException {
    spool.add(m);
  }
  @Override
  public int size() {
    return spool.size();
  }
}
