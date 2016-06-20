package com.weather.user.weatherforecast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Created by user on 2016/6/18.
 */
public class XMLParser
{
    protected XMLParsingHandler xmlParsingHandler;

    public XMLParser(XMLParsingHandler parser)
    {
        xmlParsingHandler = parser;
    }

    public Object[] DataSet(InputStream inputStream) throws SAXException,
            IOException, ParserConfigurationException
    {
        Object[] data;
           /* 產生SAXParser物件 */
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
           /* 產生XMLReader物件 */
        XMLReader xr = sp.getXMLReader();
           /* 設定自定義的MyHandler給XMLReader */
        if (xmlParsingHandler == null)
        {
            throw new NullPointerException("xmlParsingHandler is null");
        }
        else
        {
            xr.setContentHandler(xmlParsingHandler);
                /* 解析XML */
            xr.parse(new InputSource(inputStream));
                /* 取得RSS標題與內容列表 */
            data = (Object[]) xmlParsingHandler.getParsedData();
        }
        inputStream.close();
        return data;

    }

    public Object[] getData(String urlPath) throws SAXException, IOException,
            ParserConfigurationException
    {
        URL url = new URL(urlPath);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setConnectTimeout(15000);
        uc.setReadTimeout(15000); // 設定timeout時間
        uc.setRequestMethod("GET");
        uc.setDoInput(true);
        uc.connect(); // 開始連線
        int status = uc.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK)
        {
            Object[] data = DataSet(url.openStream());
            return data;
        }
        return null;
    }

}
