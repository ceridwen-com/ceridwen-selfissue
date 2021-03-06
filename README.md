# Ceridwen Self Issue Client

The Ceridwen Self Issue/Self Check Out client software provides library users with the ability to self check out books to themselves rather than relying on a staffed circulation desk. It presents a graphical interface to the library user and communicates with the back-end library system via a network. It is not itself a library system or catalogue.

The self issue client uses 3M SIP version 2 (Self Issue Protocol developed by 3M, also known as SIP2) to communicate with a suitable 3M SIP enabled library system. 3M SIP is an industry standard protocol for patron checkout of library materials.

## Prerequisites
The Self Issue Client is platform independent and will run on Windows, Macs, Linux, etc.

To install and run the Self Issue Client you need a computer with:
* Java runtime environment (JRE) 1.6 or above</li>
* a network connection</li>
* a video card and monitor with a minimum of 1024 x 768 resolution

For the Self Issue Client to be useful you need a back-end library system providing a 3M SIP2 server and a network connection.

## Running

To run the configuration editor, run the following command in the directory into which you have extracted the zip distribution

```bash
java -Xbootclasspath/a:res -jar ConfigEditor.jar
```

To run the client, run the following command in the directory into which you have extracted the zip distribution</p>

```bash
java -Xbootclasspath/a:res -jar SelfIssue.jar
```

## Configuration

The Self Issue Client includes a GUI configuration tool which can change most aspects of the software including visual appearance, connection settings in order to connect to different library systems using 3M SIP over telnet or socket connections. The Client can also send error and logging messages via e-mail.

See the documentation for the [Configuration Editor](conf/config.html).

The Client can be configured to run in two modes:

* Trust Mode - in this mode, the Client will allow the issue of books which would normal be prevented by the library system. This is intended for use where there are no security systems to prevent unissued books being taken from the library. This was originally designed for use in Oxford Colleges to allow students a means of reconding when they had taken a book. When the Client cannot check the book out automatically on the library system, it will still allow the book to be issued, and will send an e-mail detailing the issue to the librarian to handle the issue on the system manually.</li>

* Normal Mode - in this mode, the Client will report any denials to issue books from the library system to the user.

The Client can also be configured to allow offline operations when the library system or network is unavailble. In this mode, books will still be issued, and the Client will inform the library system of the books issued when it can reconnect. If there are any problems it will e-mail the librarian for manual intervention.

The Client has an plug-in API to integrate with patron barcode readers, item barcode readers. and security systems. Sample plugins are included which will work with JavaCard compatible RFID devices. For further information or bespoke customisation please contact [development@ceridwen.com](mailto:development@ceridwen.com).

## Problems and Issues

For general problems please contact [development@ceridwen.com](mailto:development@ceridwen.com). For bugs and feature requests, please use our [online issue tracker]().

## License

The Ceridwen Self Issue Client is available as open source under [GPL v3](http://www.gnu.org/licenses/gpl.html). Contact [development@ceridwen.com](mailto:development@ceridwen.com) for other licensing options.

> Copyright (C) 2019 Ceridwen Limited
>
> This program is free software: you can redistribute it and/or modify
> it under the terms of the GNU General Public License as published by
> the Free Software Foundation, either version 3 of the License, or
> (at your option) any later version.
>
> This program is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> GNU General Public License for more details.
>
> You should have received a copy of the GNU General Public License
> along with this program.  If not, see <http://www.gnu.org/licenses/>.