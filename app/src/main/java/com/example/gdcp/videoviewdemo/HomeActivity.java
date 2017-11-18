package com.example.gdcp.videoviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://www.dy2018.com/").get();
                    Elements elements=doc.getElementsByClass("co_content222");
                   for (Element element:elements){
                       Elements lis=element.getElementsByTag("li");
                       for (Element li:lis){

                           Log.i("title", "onCreate: "+   li.getElementsByTag("a").text());
                       }

                   }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }).start();

    }
}
