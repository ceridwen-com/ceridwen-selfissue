**Version 2.8.2: 11/04/2024**
Major UI overhaul
Monitor scaling support
Restructure configuration settings (config files not backwards compatible)
Added and updated Loggers and LoggingHandlers

**Version 2.7.0: 19/06/2019**
Updated dependencies to latest versions
Tidied pom file
Move to JDK 9 as minimum requirement

**Version 2.6.10: 19/06/2019**
Update pom to meet maven central requirements
Deprecate netx
Update copyright notices

﻿**Version 2.6.9: 16/06/2019 **
CheckIn Panel fixes
Support variable field ordering
Added console logging
Updated dependencies

**Version 2.6.8: 20/02/2017**
Fixed Login SIP Message support

**Version 2.6.7: 23/04/2016**  
Added support for configuring different charset encodings in the underlying SIP2 library 
Update dependencies 

**Version 2.66: 27/07/2012 (447)**
Add warnings in ConfigEditor about configuration loss when resetting configuration to default or reloading configuration
Add wait cursor when loading configuration in ConfigEditor
Allow | as well as ¦ as command character for keyboards without ¦ character
Update jgoodies-looks from 2.5.0 to 2.5.2
Update jaxfront from 2.77 to 2.79
Update ceridwen circulation from 2.81 to 2.90
Dependencies: ceridwen circulation 2.90
              ceridwen utilities 1.61
              apache commons beanutils 1.8.3
              apache commons lang 2.6
              apache commons lang 3.1
              apache commons logging 1.1.1 
              apache commons net 3.1
              jaxfront 2.79
              jgoodies-common 1.4.0
              jgoodies-looks 2.5.2
              xalan-j 2.7.1

**Version 2.65: 20/05/2012 (442)**
Added PersistentQueueImplementation configuration option for backwards compatibility
Generate configuration editor documentation from config.xsd
Added configuration help text to config.xsd (to display in editor)
Editor will warn if you attempt to close with unsaved changes
Allowed empty string in file selection combobox in Configuration editor (Bugfix: not possible to disable audio prompts in editor)
Changed ordering under Systems tab in configuration editor
Removed obsolete AllowEncodePassword in configuration editor command interfaces
Roll back Java 1.7 dependencies
Dependencies: ceridwen circulation 2.81
              ceridwen utilities 1.61
              apache commons beanutils 1.8.3
              apache commons lang 3.1
              apache commons logging 1.1.1 
              apache commons net 3.1
              jaxfront 2.77
              jgoodies-looks 2.5.0
              xalan-j 2.7.1

**Version 2.62: 25/03/2012 (414)**
Bugfix: Wrong comparison in ConnectionFactory.releaseAll means that open connections will not be closed
Update Ceridwen Utilities from 1.51 to 1.60
Update Ceridwen Circulation from 2.80 to 2.81
Dependencies: ceridwen circulation 2.81
              ceridwen utilities 1.60
              apache commons beanutils 1.8.3
              apache commons lang 3.1
              apache commons logging 1.1.1 
              apache commons net 3.1
              jaxfront 2.77
              jgoodies-looks 2.5.0
              xalan-j 2.7.1

**Version 2.61: 15/03/2012 (407)**
Bugfix: Client hangs during shutdown
Dependencies: ceridwen circulation 2.80
              ceridwen utilities 1.51
              apache commons beanutils 1.8.3
              apache commons lang 3.1
              apache commons logging 1.1.1 
              apache commons net 3.1
              jaxfront 2.77
              jgoodies-looks 2.5.0
              xalan-j 2.7.1

**Version 2.60: 15/03/2012 (405)**
Update Ceridwen Utilities from 1.4 to 1.51
Update Ceridwen Circulation from 2.7 to 2.8
Update Apache Commons Lang3 from 3.0 to 3.1
Update Apache Commons Net from 2.0 to 3.1
Update jGoodies-looks from 2.4.2 to 2.5.0
Dependencies: ceridwen circulation 2.80
              ceridwen utilities 1.51
              apache commons beanutils 1.8.3
              apache commons lang 3.1
              apache commons logging 1.1.1 
              apache commons net 3.1
              jaxfront 2.77
              jgoodies-looks 2.5.0
              xalan-j 2.7.1

**Version 2.50: 06/02/2012 (391) - Public Release**
Added configuration editor
Requires Ceridwen 3M SIP Circulation Library 2.7; Ceridwen Utilities Library 1.40
Requires Java 7

**Version 2.40: 22/07/2011 (366)**
SelfIssue events can now be logged to Syslog
SelfIssue exceptions can now be logged to Syslog
Exceptions can now be sent to multiple log handlers
Refactoring of RFID support:
  Can configure an IDReaderDevice for the Patron screen (for user cards/contacless cards)
  Can configure an IDReaderDevice for the Checkin/Checkout screen (for item/book RFID tags)
  Can configure an ItemSecurityDevice for the Checkin/Checkout screen (e.g. for tattle tape)
  Sample JavaSmartCardDevice implementation of IDReaderDevice 
Added support for optionally sending:
  SIP Login message at the beginning of a SIP session
  SIP EndPatronSession message at the end of a SIP session
Added option to prompt user for a Patron Password
Added ability for user to select Checkin screen (via button on Checkout screen)
  (in addition to staff being able to select a checkin screen via the command interface)
Bugfix: sender of SMTP log handlers no longer hardcoded
Bugfix: downgraded some connection errors from log.error to log.warn since the client handles these in the background 
Bugfix: exception handlers are throttled if exceptions occur too rapidly
Bugfix: Configuration error messages more informative
Requires Ceridwen 3M SIP Circulation Library 2.60; Ceridwen Utilities Library 1.40

**Version 2.30: 3/11/2010 (255) - Public Release**
Added new configuration options to conf.xml:
  Ability to change title text font and size
  New Palette section in UI to configure client colours
  Out Of Order screen can be optionally triggered on unhandled exceptions
  Out Of Order screen can be optionally triggered on stale Logger messages
  SIP not supports socket
  Ability to set TerminalPassword on sent SIP messages
  Can switch SIP between adding checksum and sequence number error correction to sent SIP messages
  Can optionally check checksum and sequence number on received SIP messages
  Can configure seperate RFID and Security (lock/unlock) devices
New commands added to command interface:
  Out Of Order can be triggered by command interface
  Hidden command in Out Of Order screen to return to normal
  Test Connection should be more informative about failures
Updated copyright notices to GPL3
Requires Ceridwen 3M SIP Circulation Library 2.50; Ceridwen Utilities Library 1.20

**Version 2.20: 27/10/2010 (124)**
Added command line documentation and sample jnlp files for use with netx
Updated deprecated Date.ToLocale() to use DateFormat.getDateInstance().format()
Updated commons logging from 1.1 to 1.1.1
Updated commons net from 1.4.1 to 2.0
Updated commons beanutils from 1.7.0 to 1.8.3
Updated xalan from 2.7.0 to 2.7.1
Updated source code to JDK 1.6

**Version 2.15: 16/1/2007 (91)**
Bugfix: Added data and time of transaction in e-mailed events 

**Version 2.10: 3/6/2005 (66)**
Added support for InstitutionId in SIP messages (configured in conf.xml)
Spooled SIP messages will now expire
Added dialog for fatal configuration loading errors
Modified connection code so that client will only maintain a single connection
Bugfix: Explicitly set TransactionDate in all messages (not just offline ones)
Bugfix: changed handling of expiry of spooled objects to fix bug where object never expires
Bugfix: Security device will be deactivated if BookPanel timesout

**Version 2.00: 25/10/2004 (58)**