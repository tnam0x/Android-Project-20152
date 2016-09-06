package com.namtran.entity;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Parse dữ liệu tiền tệ từ link thành 3 trường: kí hiệu, tên, tỉ giá
 * Dùng cho chức năng Đổi tiền tệ
 * */
public class CurrencyPullParser {
    private static ForeignCurrencyItem change(Element e1, Element e2, Element e3) {
        float b;
        try {
            String[] a = e3.text().split(",");
            b = Float.parseFloat(a[0] + a[1]);
        } catch (Exception e) {
            b = Float.parseFloat(e3.text());
        }
        return new ForeignCurrencyItem(e1.text(), e2.text(), b);
    }

    public static ArrayList<ForeignCurrencyItem> parseHTML() {
        ArrayList<ForeignCurrencyItem> mCurrency = new ArrayList<>();
        Document document;
        try {
            document = Jsoup.connect("https://www.vietcombank.com.vn/exchangerates/").get();
            Elements link = document.select("td");
            for (int i = 0; i < 95; i = i + 5) {
                ForeignCurrencyItem item = change(link.get(i), link.get(i + 1), link.get(i + 4));
                mCurrency.add(item);
                Log.d("Currency", item.symbol + item.toString());
            }
        } catch (IOException e) {
            Log.e("CurrencyPullParser", e.getMessage());
        } finally {
            mCurrency.add(new ForeignCurrencyItem("VND", "VietNam Dong", 1));
        }
        return mCurrency;
    }
}
