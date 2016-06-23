package com.weather.user.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weather.user.weatherforecast.XMLParser;
import com.weather.user.weatherforecast.RssNewsXMLParsingHandler;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import static android.provider.BaseColumns._ID;

public class Weather_Main extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too +memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather__main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                //state參數:0是空閒；1是滑動中；2是load完成
                if(state == 2)
                {
                    //強制重新載入，使第一次顯示資料正常
                    int temp;
                    temp = mViewPager.getCurrentItem();
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setCurrentItem(temp);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_weather__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //PlaceholderFragment.newInstance(0).textView.getText();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView textView;
        private Button delete_btn;
        private RssNews[] Arr_RssNews;
        private String trgUrl= "http://www.cwb.gov.tw/rss/forecast/36_08.xml";
        private ListView data_ListView;
        private ArrayList<String> mNames = new ArrayList<String>();
        private ArrayList<String> mNames2 = new ArrayList<String>();
        private ListAdapter myAdapter;
        private ListAdapter myAdapter2;
        int idx = 999;

        private MyDB mydb = null;

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weather__main, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);
            data_ListView = (ListView) rootView.findViewById(R.id.Data_listView);
            delete_btn = (Button) rootView.findViewById(R.id.Delete_btn);
            mydb = new MyDB(getActivity());

            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1)
            {
                //第一頁顯示
                delete_btn.setVisibility(View.GONE);
                    new Thread() {
                        @Override
                        public void run() {
                            Arr_RssNews = getRssNews();
                            if (Arr_RssNews != null)
                                // 在Handler上發出「要更新版面」的訊息
                                mHandler.sendEmptyMessage(1);
                        }
                    }.start();

            }
            else
            {
                //第二頁顯示
                textView.setText("DB data");
                mNames2 = new ArrayList<String>();
                delete_btn.setVisibility(View.VISIBLE);
                SQLiteDatabase db = mydb.getReadableDatabase();
                String[] columns = {_ID,"data"};
                Cursor cursor = db.query("WEATHER", columns, null, null, null, null, null);
                getActivity().startManagingCursor(cursor);
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(0);
                    String data = cursor.getString(1);
                    mNames2.add(data);
                }
                //simple_list_item_single_choice,mNames2設定LISTVIEW顯示樣式
                myAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_single_choice,mNames2);
                data_ListView.setAdapter(myAdapter2);

                //設定LISTVIEW選擇樣式
                data_ListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                data_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
                    {
                        AbsListView list = (AbsListView)adapterView;
                        idx = list.getCheckedItemPosition();
                    }
                });
            }

            delete_btn.setOnClickListener(new Button.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    //刪除按鈕事件
                    if(idx != 999)
                    {
                        SQLiteDatabase db = mydb.getReadableDatabase();
                        String[] columns = {_ID, "data"};
                        ArrayList<String> db_ID = new ArrayList<String>();
                        ArrayList<String> db_data = new ArrayList<String>();
                        Cursor cursor = db.query("WEATHER", columns, null, null, null, null, null);
                        getActivity().startManagingCursor(cursor);
                        while (cursor.moveToNext()) {
                            int id = cursor.getInt(0);
                            String data = cursor.getString(1);
                            db_ID.add("" + id);
                            db_data.add(data);
                        }
                        db.delete("WEATHER", _ID + "=" + db_ID.get(mNames2.indexOf(db_data.get(idx))), null);
                        mNames2.remove(idx);
                        data_ListView.setAdapter(myAdapter2);
                        idx = 999;
                    }
                }
            });
            return rootView;
        }


        Handler mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    // 更新資料，將Rss新聞的標題用迴圈依序印出來
                    case 1:
                        for (int i = 0; i < Arr_RssNews.length; i++)
                        {
                            textView.setText(Arr_RssNews[1].getTitle());
                            mNames = Arr_RssNews[1].getdescription();
                            mNames.trimToSize();
                            GetFile();
                        }
                        break;
                }
            }
        };

        public RssNews[] getRssNews()
        {
            //連線到RSS並下載資料
            if (trgUrl == null)
                return null;
            try
            {
                // 建立一個Parser物件，並指定擷取規則 (ParsingHandler)
                XMLParser dataXMLParser = new XMLParser(
                        new RssNewsXMLParsingHandler());
                // 呼叫getData方法取得物件陣列
                Object[] data = (Object[]) dataXMLParser.getData(trgUrl);
                if (data != null)
                {
                    // 如果資料形態正確，就回傳
                    if (data[0] instanceof RssNews[])
                    {
                        return (RssNews[]) data[0];
                    }
                }
            } catch (SAXException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ParserConfigurationException e)
            {
                e.printStackTrace();
            }
            // 若有錯誤則回傳null
            return null;
        }

        public void GetFile()
        {
            //從下載資料放進資料庫中，並將下載資料顯示於第一頁中
            SQLiteDatabase db = mydb.getWritableDatabase();
            ContentValues Value = new ContentValues();
            ContentValues test = new ContentValues();
            SQLiteDatabase dbr = mydb.getReadableDatabase();
            String[] columns = {_ID,"data"};
            Cursor cursor = dbr.query("WEATHER", columns, null, null, null, null, null);
            getActivity().startManagingCursor(cursor);

            for(int i=0;i<mNames.size();i++)
            {
                Value.put("data",mNames.get(i));
                if(mNames.get(i).length()>6)
                if(cursor.getCount() < 1)
                {
                    db.insert("WEATHER", null, Value);
                }
            }
            myAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mNames);
            data_ListView.setAdapter(myAdapter);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

}
