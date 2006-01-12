package com.ceridwen.selfissue.client;

import java.lang.management.ManagementFactory;
import javax.management.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SelfIssueFrameMBInit
{
  private static Log log = LogFactory.getLog(SelfIssueFrameMBInit.class);

  public SelfIssueFrameMBInit(SelfIssueFrame frame)
  {
    try {
      MBeanServer mbs;
      mbs =
          ManagementFactory.getPlatformMBeanServer();

      StandardMBean mbean = new StandardMBean(frame,
                                              SelfIssueFrameMBean.class);
      ObjectName name = new ObjectName("com.ceridwen.selfissue.client:type=SelfIssue");
      mbs.registerMBean(mbean, name);
    } catch (NullPointerException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MalformedObjectNameException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MBeanRegistrationException ex1) {
      log.fatal("MBean error", ex1);
    } catch (InstanceAlreadyExistsException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NotCompliantMBeanException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NoClassDefFoundError ex1) {
      log.fatal("MBean error", ex1);
    }
  }
}
