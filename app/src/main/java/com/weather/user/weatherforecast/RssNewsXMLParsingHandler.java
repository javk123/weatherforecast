package com.weather.user.weatherforecast;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import android.util.Log;
import com.weather.user.weatherforecast.RssNews;
import com.weather.user.weatherforecast.XMLParsingHandler;

/**
 * Created by user on 2016/6/18.
 */
public class RssNewsXMLParsingHandler extends XMLParsingHandler
{
    private static final String TAG = "RssNews ParsingHandler";
    /** 用來儲存Rss新聞的物件 */
    private RssNews newsItem;
    /** 用來儲存Rss新聞的物件Stack(堆疊) */
    private Stack<RssNews> mNewsItem_list;

    /** 建構子 */
    public RssNewsXMLParsingHandler()
    {
    }

    /**
     * @return回傳RssNews[]。程式會將讀到的物件{ RssNews[] }包成Object[]然後回傳
     */
    @Override
    public Object[] getParsedData()
    {
        RssNews[] Arr_RssNews = (RssNews[]) mNewsItem_list
                .toArray(new RssNews[mNewsItem_list.size()]);
        // 解析結果回報
        Log.v(TAG, String.format("RssNews Count=%d", Arr_RssNews.length));
        return new Object[] { Arr_RssNews };
    }

    /**
     * XML文件開始解析時呼叫此method
     */
    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        // 在文件開始的時候，宣告出該RssNews形態的Stack(堆疊)
        mNewsItem_list = new Stack<RssNews>();
    }

    /**
     * XML文件結束時呼叫此method
     */
    @Override
    public void endDocument() throws SAXException
    {
        super.endDocument();
    }

    /**
     * 解析到Element的開頭時呼叫此method
     */
    @Override
    public  void startElement(String namespaceURI, String localName,
                            String qName, Attributes atts) throws SAXException
    {
        super.startElement(namespaceURI, localName, qName, atts);
        // 若搞不清楚現在在哪裡的話可以用printNodePos();
        //Log.v(TAG,printNodePos());
        if (getInNode().size() >= 3
                && getInNode().get(getInNode().size() - 3).equals("rss")
                && getInNode().get(getInNode().size() - 2).equals("channel")
                && getInNode().get(getInNode().size() - 1).equals("item"))
        {
            // 在rss -> channel -> item這個位置中
            // 新增一個RssNews
            newsItem = new RssNews();
        }
    }

    /**
     * 解析到Element的結尾時呼叫此method
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException
    {

        if (getInNode().size() >= 3
                && getInNode().get(getInNode().size() - 3).equals("rss")
                && getInNode().get(getInNode().size() - 2).equals("channel")
                && getInNode().get(getInNode().size() - 1).equals("item"))
        {
            // 在rss -> channel -> item這個位置中
            // 新增一筆新聞資料到 Stack(堆疊) 裡
            mNewsItem_list.add(newsItem);
            newsItem = null;
        }
        super.endElement(namespaceURI, localName, qName);
    }

    /**
     * 取得Element的開頭結尾中間夾的字串
     */
    @Override
    public void characters(String fetchStr)
    {
        if (getInNode().size() >= 4
                && getInNode().get(getInNode().size() - 4).equals("rss")
                && getInNode().get(getInNode().size() - 3).equals("channel")
                && getInNode().get(getInNode().size() - 2).equals("item"))
        {
            // 在rss -> channel -> item -> XXX這個位置中

            // 新增Node的上所有資料

            // 在rss -> channel -> item -> title這個位置中
            if (getInNode().lastElement().equals("title"))
                // 設定標題
                newsItem.setTitle(fetchStr);
                // 在rss -> channel -> item -> link這個位置中
            else if (getInNode().lastElement().equals("link"))
                // 設定連結
                newsItem.setLink(fetchStr);
                // 在rss -> channel -> item -> pubDate這個位置中
            else if (getInNode().lastElement().equals("pubDate"))
                // 設定發佈日期
                newsItem.setPubDate(fetchStr);
            else if (getInNode().lastElement().equals("description"))
                // 設定內容
            {
                if(fetchStr.length() > 1) {
                    newsItem.setdescription(fetchStr.split("<BR>")[0]);
                    Log.v(TAG, fetchStr);
                }
            }
        }
    }
}
