package namtran.lab.entity;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HTMLParser {
    private static ForeignCurrency change(Element e1, Element e2, Element e3) {
        float b;
        try {
            String[] a = e3.text().split(",");
            b = Float.parseFloat(a[0] + a[1]);
        } catch (Exception e) {
            b = Float.parseFloat(e3.text());
        }
        return new ForeignCurrency(e1.text(), e2.text(), b);
    }

    public static ArrayList<ForeignCurrency> parseHTML() {
        ArrayList<ForeignCurrency> mCurrency = new ArrayList<>();
        Document document;
        try {
            document = Jsoup.connect("https://www.vietcombank.com.vn/exchangerates/").get();
            Elements links = document.select("td");
            for (int i = 0; i < 95; i = i + 5) {
                ForeignCurrency m = change(links.get(i), links.get(i + 1), links.get(i + 4));
                mCurrency.add(m);
                Log.d("Currency", m.symbol + m.toString());
            }
        } catch (IOException e) {
            Log.e("HTMLParser", e.getMessage());
        } finally {
            mCurrency.add(new ForeignCurrency("VND", "VietNam Dong", 1));
        }
        return mCurrency;
    }
}
