package com.yhs.base.factory;

import com.thoughtworks.xstream.XStream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 04628-duanchengjun
 * @version Id: XStreamFactory.java, v 0.1 2019/4/25 10:12 duanchengjun Exp $
 */
public class XStreamFactory {

    private Map<String, XStream> xStreams = null;

    private XStreamFactory() {
        xStreams = new ConcurrentHashMap<>();
    }

    public static XStreamFactory getInstance() {
        return SingletonContainer._hinstance;
    }

    public XStream getXStream(String typeName) {
        XStream xStream = xStreams.get(typeName);
        if (xStream != null) {
            return xStream;
        } else {
            synchronized (XStreamFactory.class) {
                xStream = new XStream();
                xStreams.put(typeName, xStream);
            }
        }
        return xStream;
    }

    private static class SingletonContainer {
        private static final XStreamFactory _hinstance = new XStreamFactory();
    }

}
