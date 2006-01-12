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
