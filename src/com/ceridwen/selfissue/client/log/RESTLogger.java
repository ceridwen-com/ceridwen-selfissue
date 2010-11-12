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

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.OutOfOrderInterface;

public class RESTLogger extends OnlineLogLogger {
    String baseUrl;

    public void initialise(Node config, OutOfOrderInterface ooo) {
        super.initialise(config, ooo);
        this.baseUrl = Configuration.getSubProperty(config, "URL");
    }

    public synchronized boolean sendRest(OnlineLogEvent event) { 
        StringBuffer restString = new StringBuffer();
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
                } catch (IllegalArgumentException ex) {
                } catch (IllegalAccessException ex) {
                } catch (InvocationTargetException ex) {
                }
            }
            try {
                // Send data
                String urlStr = this.baseUrl;
                String params = restString.toString();
                if  (params.length() > 0) {
                     urlStr += "?" + params;
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
    
                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                return true;
            } catch (Exception e) {
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
