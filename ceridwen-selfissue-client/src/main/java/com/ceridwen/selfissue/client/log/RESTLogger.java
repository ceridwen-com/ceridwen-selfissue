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
package com.ceridwen.selfissue.client.log;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import java.io.IOException;

public class RESTLogger extends OnlineLogLogger {
    String baseUrl;

    @Override
    public void initialise(Node config, OutOfOrderInterface ooo) {
        super.initialise(config, ooo);
        this.baseUrl = (ssl?"https":"http") + "://" + host + ":" + ((port==0)?(ssl?443:80):port) + target;
    }

    public synchronized boolean sendRest(OnlineLogEvent event) { 
        StringBuilder restString = new StringBuilder();
        try {
            BeanInfo bi = Introspector.getBeanInfo(event.getClass(), Object.class);
            boolean first = true;
            for (PropertyDescriptor prop : bi.getPropertyDescriptors()) {
                try {
                    Object value = prop.getReadMethod().invoke(event, new Object[] {});
                    if (value != null) {
                        if (!first) {
                            restString.append("&");
                        }
                        first = false;
                        restString.append(prop.getName());
                        restString.append("=");
                        restString.append(value);
                    }
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                }
            }
            try {
                // Send data
                String urlStr = this.baseUrl;
                String params = restString.toString();
                if  (StringUtils.isNotEmpty(params)) {
                     urlStr += "?" + params;
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
    
                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (IntrospectionException ex) {
            return false;
        }
    }
    
    @Override
    public boolean log(OnlineLogEvent event) {
        return this.sendRest(event);
    }
}
