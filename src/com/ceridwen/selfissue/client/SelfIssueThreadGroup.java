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
