package com.phanmemquanlychitieu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Adapter.BaoCaoHienTai;
import Adapter.BaoCaoNam;
import Adapter.BaoCaoQuy;
import Adapter.BaoCaoThang;
import Adapter.DoiNgay;
import Database.dbChi;
import Database.dbThu;
import Objects.BaoCao;
import Objects.TienThuChi;

public class BaoCaoThuChi extends Activity {

    dbThu dbthu;
    SQLiteDatabase mDbthu;
    Cursor mCursorthu;
    //danh sach chi
    dbChi dbchi;
    // SimpleCursorAdapter mAdapterthu;
    SQLiteDatabase mDbchi;
    Cursor mCursorchi;
    // private SimpleCursorAdapter mAdapterchi;
    ListView listhientai;
    DanhSachThuChi dsthuchi;
    Object object;
    String title[] = {"Hôm Nay", "Tháng nay", "Năm Nay"};
    BaoCao danhsach;
    BaoCao danhsachthang;
    BaoCao danhsachnam;
    BaoCaoHienTai adapterbaocaohientai;
    BaoCao objectchi2;
    TienThuChi objectthuchi;
    //Khai bao nut cho chon bieu do
    Button btn_chiCot, btn_thuCot, btn_chiTron, btn_thuTron;
    //Khai báo biến báo cáo tháng
    EditText thangnam;
    ImageButton img_timnam;
    ImageButton img_btn_kqbaocaothang;
    ListView danhsachbaocaothang;
    BaoCaoThang adapterbaocaothang;
    String datetimenam = "";
    ArrayList<Integer> taikhoan;
    ArrayList<BaoCao> arrbaocaothang;
    ArrayList<Integer> imageId = null;
    BaoCao baocaothang;
    ArrayList<String> arrcalendar;
    ArrayList<String> arrtitlethang;
    //khai báo biến báo cáo quý
    EditText edit_quy;
    ImageButton img_timquy;
    ImageButton img_btn_kqbaocaoquy;
    ListView danhsachbaocaoquy;
    BaoCaoQuy adapterbaocaoquy;
    String datetimequy = "";
    ArrayList<BaoCao> arrbaocaoquy;
    ArrayList<String> arr;
    //Khai báo biến báo cáo năm
    BaoCaoNam adapterbaocaonam;
    ArrayList<BaoCao> arrnam;
    BaoCao objectnam;
    ListView lvnam;
    BaoCao baocaonam;
    private ListView listthu;
    private ArrayList<BaoCao> arrthu = null;
    private ArrayList<BaoCao> arrchi = null;
    private ArrayList<BaoCao> arrhientai;
    private DoiNgay doingaythu = null;
    private DoiNgay doingaychi = null;
    private String datehientai = "";
    private String thanghientai = "";
    private String namhientai = "";
    private double sumthu;
    private double sumchi;
    private double sumthuthang;
    private double sumchithang;
    private double sumthunam;
    private double sumchinam;
    //Khai bao bien
    double a, b, c, d, e, f, g, i, j, k, l, m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baocaothuchi);

        listhientai = (ListView) findViewById(R.id.listView_baocaohientai);
        arrhientai = new ArrayList<BaoCao>();
        dbthu = new dbThu(this);
        dbchi = new dbChi(this);
        loadTab();
        danhSachThu();
        danhSachChi();
        if (arrchi.size() == 0 && arrthu.size() == 0) {
            BaoCaoThuChi.this.finish();
        }
        layNgayGiohientai();

        //tinh tong gia tri thu chi
        for (int i = 0; i < arrthu.size(); i++) {
            if (arrthu.get(i).getNgay().equals(datehientai)) {
                String a = arrthu.get(i).getTienthu();
                sumthu += Double.parseDouble(a);
            }
        }
        //tong chi
        for (int i = 0; i < arrchi.size(); i++) {
            if (arrchi.get(i).getNgay().equals(datehientai)) {
                String a = arrchi.get(i).getTienchi();
                sumchi += Double.parseDouble(a);
            }
        }

        baoCaoNgayHienTai();
        baoCaoThangHienTai();
        baoCaoNamHienTai();
        adapterbaocaohientai = new BaoCaoHienTai(this, R.layout.t_custom_danhsachhientai, arrhientai);
        listhientai.setAdapter(adapterbaocaohientai);
        //danh sach hien tai
        listhientai.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    objectthuchi = new TienThuChi();
                    a = objectthuchi.kqngaychi("Ăn Uống", datehientai, arrchi);
                    b = objectthuchi.kqngaychi("Quần Áo", datehientai, arrchi);
                    c = objectthuchi.kqngaychi("Cho vay", datehientai, arrchi);
                    d = objectthuchi.kqngaychi("Sinh Hoạt", datehientai, arrchi);
                    e = objectthuchi.kqngaychi("Đi Lại", datehientai, arrchi);
                    f = objectthuchi.kqngaychi("Trả Nợ", datehientai, arrchi);
                    g = objectthuchi.kqngaychi("Khác", datehientai, arrchi);
                    i = objectthuchi.kqngaythu("Tiền Lương", datehientai, arrthu);
                    j = objectthuchi.kqngaythu("Đòi Nợ", datehientai, arrthu);
                    k = objectthuchi.kqngaythu("Bán Đồ", datehientai, arrthu);
                    l = objectthuchi.kqngaythu("Đi Vay", datehientai, arrthu);
                    m = objectthuchi.kqngaythu("Khác", datehientai, arrthu);
                    selectChart();
                } else if (position == 1) {
                    objectthuchi = new TienThuChi();
                    a = objectthuchi.kqthangchi("Ăn Uống", thanghientai, arrchi);
                    b = objectthuchi.kqthangchi("Quần Áo", thanghientai, arrchi);
                    c = objectthuchi.kqthangchi("Cho vay", thanghientai, arrchi);
                    d = objectthuchi.kqthangchi("Sinh Hoạt", thanghientai, arrchi);
                    e = objectthuchi.kqthangchi("Đi Lại", thanghientai, arrchi);
                    f = objectthuchi.kqthangchi("Trả Nợ", thanghientai, arrchi);
                    g = objectthuchi.kqthangchi("Khác", thanghientai, arrchi);
                    i = objectthuchi.kqthangthu("Tiền Lương", thanghientai, arrthu);
                    j = objectthuchi.kqthangthu("Đòi Nợ", thanghientai, arrthu);
                    k = objectthuchi.kqthangthu("Bán Đồ", thanghientai, arrthu);
                    l = objectthuchi.kqthangthu("Đi Vay", thanghientai, arrthu);
                    m = objectthuchi.kqthangthu("Khác", thanghientai, arrthu);
                    selectChart();
                } else if (position == 2) {
                    objectthuchi = new TienThuChi();
                    a = objectthuchi.kqnamchi("Ăn Uống", namhientai, arrchi);
                    b = objectthuchi.kqnamchi("Quần Áo", namhientai, arrchi);
                    c = objectthuchi.kqnamchi("Cho vay", namhientai, arrchi);
                    d = objectthuchi.kqnamchi("Sinh Hoạt", namhientai, arrchi);
                    e = objectthuchi.kqnamchi("Đi Lại", namhientai, arrchi);
                    f = objectthuchi.kqnamchi("Trả Nợ", namhientai, arrchi);
                    g = objectthuchi.kqnamchi("Khác", namhientai, arrchi);
                    i = objectthuchi.kqnamthu("Tiền Lương", namhientai, arrthu);
                    j = objectthuchi.kqnamthu("Đòi Nợ", namhientai, arrthu);
                    k = objectthuchi.kqnamthu("Bán Đồ", namhientai, arrthu);
                    l = objectthuchi.kqnamthu("Đi Vay", namhientai, arrthu);
                    m = objectthuchi.kqnamthu("Khác", namhientai, arrthu);
                    selectChart();
                }
            }
        });

        //danh sach theo tháng
        thangnam = (EditText) findViewById(R.id.editText_nhapthang);
        img_timnam = (ImageButton) findViewById(R.id.imageButton_chonthang);
        img_btn_kqbaocaothang = (ImageButton) findViewById(R.id.imageButton_baocaothang);
        danhsachbaocaothang = (ListView) findViewById(R.id.listView_baocaothang);
        layNamHienTai();
        //lấy du lieu thang
        img_btn_kqbaocaothang.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                imageId = new ArrayList<>();
                imageId.add(R.drawable.thang1);
                imageId.add(R.drawable.thang2);
                imageId.add(R.drawable.thang3);
                imageId.add(R.drawable.thang4);
                imageId.add(R.drawable.thang5);
                imageId.add(R.drawable.thang6);
                imageId.add(R.drawable.thang7);
                imageId.add(R.drawable.lichcongtac);
                imageId.add(R.drawable.thang9);
                imageId.add(R.drawable.thang10);
                imageId.add(R.drawable.danhmuc);
                imageId.add(R.drawable.thang12);

                //ds thang
                arrcalendar = new ArrayList<String>();
                arrcalendar.add("01");
                arrcalendar.add("02");
                arrcalendar.add("03");
                arrcalendar.add("04");
                arrcalendar.add("05");
                arrcalendar.add("06");
                arrcalendar.add("07");
                arrcalendar.add("08");
                arrcalendar.add("09");
                arrcalendar.add("10");
                arrcalendar.add("11");
                arrcalendar.add("12");

                //tieu de thag
                arrtitlethang = new ArrayList<String>();
                arrtitlethang.add("Tháng 1");
                arrtitlethang.add("Tháng 2");
                arrtitlethang.add("Tháng 3");
                arrtitlethang.add("Tháng 4");
                arrtitlethang.add("Tháng 5");
                arrtitlethang.add("Tháng 6");
                arrtitlethang.add("Tháng 7");
                arrtitlethang.add("Tháng 8");
                arrtitlethang.add("Tháng 9");
                arrtitlethang.add("Tháng 10");
                arrtitlethang.add("Tháng 11");
                arrtitlethang.add("Tháng 12");

                arrbaocaothang = new ArrayList<BaoCao>();
                baocaothang = new BaoCao();
                DoiNgay doi = new DoiNgay();
                double thangthu1 = baocaothang.thuThangThu(thangnam.getText().toString(), arrthu, "01");
                double thangchi1 = baocaothang.thuThangChi(thangnam.getText().toString(), arrchi, "01");
                baocaothang.setTienthu(String.valueOf(thangthu1));
                baocaothang.setTienchi(String.valueOf(thangchi1));
                arrbaocaothang.add(baocaothang);

                BaoCao baocaothang2 = new BaoCao();
                double thangthu2 = baocaothang2.thuThangThu(thangnam.getText().toString(), arrthu, "02");
                double thangchi2 = baocaothang2.thuThangChi(thangnam.getText().toString(), arrchi, "02");
                baocaothang2.setTienthu(String.valueOf(thangthu2));
                baocaothang2.setTienchi(String.valueOf(thangchi2));
                arrbaocaothang.add(baocaothang2);

                BaoCao baocaothang3 = new BaoCao();
                double thangthu3 = baocaothang3.thuThangThu(thangnam.getText().toString(), arrthu, "03");
                double thangchi3 = baocaothang3.thuThangChi(thangnam.getText().toString(), arrchi, "03");
                baocaothang3.setTienthu(String.valueOf(thangthu3));
                baocaothang3.setTienchi(String.valueOf(thangchi3));
                arrbaocaothang.add(baocaothang3);

                BaoCao baocaothang4 = new BaoCao();
                double thangthu4 = baocaothang4.thuThangThu(thangnam.getText().toString(), arrthu, "04");
                double thangchi4 = baocaothang4.thuThangChi(thangnam.getText().toString(), arrchi, "04");
                baocaothang4.setTienthu(String.valueOf(thangthu4));
                baocaothang4.setTienchi(String.valueOf(thangchi4));
                arrbaocaothang.add(baocaothang4);

                BaoCao baocaothang5 = new BaoCao();
                double thangthu5 = baocaothang5.thuThangThu(thangnam.getText().toString(), arrthu, "05");
                double thangchi5 = baocaothang5.thuThangChi(thangnam.getText().toString(), arrchi, "05");
                baocaothang5.setTienthu(String.valueOf(thangthu5));
                baocaothang5.setTienchi(String.valueOf(thangchi5));
                arrbaocaothang.add(baocaothang5);

                BaoCao baocaothang6 = new BaoCao();
                double thangthu6 = baocaothang6.thuThangThu(thangnam.getText().toString(), arrthu, "06");
                double thangchi6 = baocaothang6.thuThangChi(thangnam.getText().toString(), arrchi, "06");
                baocaothang6.setTienthu(String.valueOf(thangthu6));
                baocaothang6.setTienchi(String.valueOf(thangchi6));
                arrbaocaothang.add(baocaothang6);

                BaoCao baocaothang7 = new BaoCao();
                double thangthu7 = baocaothang7.thuThangThu(thangnam.getText().toString(), arrthu, "07");
                double thangchi7 = baocaothang7.thuThangChi(thangnam.getText().toString(), arrchi, "07");
                baocaothang7.setTienthu(String.valueOf(thangthu7));
                baocaothang7.setTienchi(String.valueOf(thangchi7));
                arrbaocaothang.add(baocaothang7);

                BaoCao baocaothang8 = new BaoCao();
                double thangthu8 = baocaothang8.thuThangThu(thangnam.getText().toString(), arrthu, "08");
                double thangchi8 = baocaothang8.thuThangChi(thangnam.getText().toString(), arrchi, "08");
                baocaothang8.setTienthu(String.valueOf(thangthu8));
                baocaothang8.setTienchi(String.valueOf(thangchi8));
                arrbaocaothang.add(baocaothang8);

                BaoCao baocaothang9 = new BaoCao();
                double thangthu9 = baocaothang9.thuThangThu(thangnam.getText().toString(), arrthu, "09");
                double thangchi9 = baocaothang9.thuThangChi(thangnam.getText().toString(), arrchi, "09");
                baocaothang9.setTienthu(String.valueOf(thangthu9));
                baocaothang9.setTienchi(String.valueOf(thangchi9));
                arrbaocaothang.add(baocaothang9);

                BaoCao baocaothang10 = new BaoCao();
                double thangthu10 = baocaothang10.thuThangThu(thangnam.getText().toString(), arrthu, "10");
                double thangchi10 = baocaothang10.thuThangChi(thangnam.getText().toString(), arrchi, "10");
                baocaothang10.setTienthu(String.valueOf(thangthu10));
                baocaothang10.setTienchi(String.valueOf(thangchi10));
                arrbaocaothang.add(baocaothang10);

                BaoCao baocaothang11 = new BaoCao();
                double thangthu11 = baocaothang11.thuThangThu(thangnam.getText().toString(), arrthu, "11");
                double thangchi11 = baocaothang11.thuThangChi(thangnam.getText().toString(), arrchi, "11");
                baocaothang11.setTienthu(String.valueOf(thangthu11));
                baocaothang11.setTienchi(String.valueOf(thangchi11));
                arrbaocaothang.add(baocaothang11);

                BaoCao baocaothang12 = new BaoCao();
                double thangthu12 = baocaothang12.thuThangThu(thangnam.getText().toString(), arrthu, "12");
                double thangchi12 = baocaothang12.thuThangChi(thangnam.getText().toString(), arrchi, "12");
                baocaothang12.setTienthu(String.valueOf(thangthu12));
                baocaothang12.setTienchi(String.valueOf(thangchi12));
                arrbaocaothang.add(baocaothang12);
                dsThang();
                adapterbaocaothang = new BaoCaoThang(BaoCaoThuChi.this, R.layout.t_custom_danhsachthang, arrbaocaothang, imageId, arrtitlethang);
                danhsachbaocaothang.setAdapter(adapterbaocaothang);
            }
        });

        danhsachbaocaothang.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                objectthuchi = new TienThuChi();
                a = objectthuchi.kqthangchi("Ăn Uống", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                b = objectthuchi.kqthangchi("Quần Áo", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                c = objectthuchi.kqthangchi("Cho vay", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                d = objectthuchi.kqthangchi("Sinh Hoạt", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                e = objectthuchi.kqthangchi("Đi Lại", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                f = objectthuchi.kqthangchi("Trả Nợ", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                g = objectthuchi.kqthangchi("Khác", thangnam.getText().toString() + arrcalendar.get(position), arrchi);
                i = objectthuchi.kqthangthu("Tiền Lương", thangnam.getText().toString() + arrcalendar.get(position), arrthu);
                j = objectthuchi.kqthangthu("Đòi Nợ", thangnam.getText().toString() + arrcalendar.get(position), arrthu);
                k = objectthuchi.kqthangthu("Bán Đồ", thangnam.getText().toString() + arrcalendar.get(position), arrthu);
                l = objectthuchi.kqthangthu("Đi Vay", thangnam.getText().toString() + arrcalendar.get(position), arrthu);
                m = objectthuchi.kqthangthu("Khác", thangnam.getText().toString() + arrcalendar.get(position), arrthu);
                selectChart();
            }
        });
        img_timnam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(null);
            }
        });


        //danh sách theo quý
        edit_quy = (EditText) findViewById(R.id.editText_nhapnamquy);
        img_timquy = (ImageButton) findViewById(R.id.imageButton_chonquy);
        img_btn_kqbaocaoquy = (ImageButton) findViewById(R.id.imageButton_baocaoquy);
        danhsachbaocaoquy = (ListView) findViewById(R.id.listView_baocaoquy);
        edit_quy.setText(datetimequy);
        img_timquy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDatePickerDialog1(null);
            }
        });
        img_btn_kqbaocaoquy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                arrbaocaoquy = new ArrayList<BaoCao>();
                baocaothang = new BaoCao();
                arr = new ArrayList<String>();
                arr.add("Quý I");
                arr.add("Quý II");
                arr.add("Quý III");
                arr.add("Quý IV");
                double quy1 = baocaothang.thuQuy("01", "03", edit_quy.getText().toString(), arrthu);
                double quy1a = baocaothang.chiQuy("01", "03", edit_quy.getText().toString(), arrchi);
                baocaothang.setTienthu(String.valueOf(quy1));
                baocaothang.setTienchi(String.valueOf(quy1a));
                arrbaocaoquy.add(baocaothang);

                BaoCao baocaothang1 = new BaoCao();
                double quy2 = baocaothang1.thuQuy("04", "06", edit_quy.getText().toString(), arrthu);
                double quy2a = baocaothang1.chiQuy("04", "06", edit_quy.getText().toString(), arrchi);
                baocaothang1.setTienthu(String.valueOf(quy2));
                baocaothang1.setTienchi(String.valueOf(quy2a));
                arrbaocaoquy.add(baocaothang1);

                BaoCao baocaothang2 = new BaoCao();
                double quy3 = baocaothang2.thuQuy("07", "09", edit_quy.getText().toString(), arrthu);
                double quy3a = baocaothang2.chiQuy("07", "09", edit_quy.getText().toString(), arrchi);
                baocaothang2.setTienthu(String.valueOf(quy3));
                baocaothang2.setTienchi(String.valueOf(quy3a));
                arrbaocaoquy.add(baocaothang2);

                BaoCao baocaothang3 = new BaoCao();
                double quy4 = baocaothang3.thuQuy("10", "12", edit_quy.getText().toString(), arrthu);
                double quy4a = baocaothang3.chiQuy("10", "12", edit_quy.getText().toString(), arrchi);
                baocaothang3.setTienthu(String.valueOf(quy4));
                baocaothang3.setTienchi(String.valueOf(quy4a));
                arrbaocaoquy.add(baocaothang3);
                adapterbaocaoquy = new BaoCaoQuy(BaoCaoThuChi.this, R.layout.t_custom_danhsachquy, arrbaocaoquy, arr);
                danhsachbaocaoquy.setAdapter(adapterbaocaoquy);
            }
        });

        danhsachbaocaoquy.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                objectthuchi = new TienThuChi();
                a = 0;
                b = 0;
                c = 0;
                d = 0;
                e = 0;
                f = 0;
                g = 0;
                i = 0;
                j = 0;
                k = 0;
                l = 0;
                m = 0;
                if (arg2 == 0) {

                    a = objectthuchi.kqquychi("Ăn Uống", "01", "03", edit_quy.getText().toString(), arrchi);
                    b = objectthuchi.kqquychi("Quần Áo", "01", "03", edit_quy.getText().toString(), arrchi);
                    c = objectthuchi.kqquychi("Cho vay", "01", "03", edit_quy.getText().toString(), arrchi);
                    d = objectthuchi.kqquychi("Sinh Hoạt", "01", "03", edit_quy.getText().toString(), arrchi);
                    e = objectthuchi.kqquychi("Đi Lại", "01", "03", edit_quy.getText().toString(), arrchi);
                    f = objectthuchi.kqquychi("Trả Nợ", "01", "03", edit_quy.getText().toString(), arrchi);
                    g = objectthuchi.kqquychi("Khác", "01", "03", edit_quy.getText().toString(), arrchi);
                    i = objectthuchi.kqquythu("Tiền Lương", "01", "03", edit_quy.getText().toString(), arrthu);
                    j = objectthuchi.kqquythu("Đòi Nợ", "01", "03", edit_quy.getText().toString(), arrthu);
                    k = objectthuchi.kqquythu("Bán Đồ", "01", "03", edit_quy.getText().toString(), arrthu);
                    l = objectthuchi.kqquythu("Đi Vay", "01", "03", edit_quy.getText().toString(), arrthu);
                    m = objectthuchi.kqquythu("Khác", "01", "03", edit_quy.getText().toString(), arrthu);
                } else if (arg2 == 1) {

                    a = objectthuchi.kqquychi("Ăn Uống", "04", "06", edit_quy.getText().toString(), arrchi);
                    b = objectthuchi.kqquychi("Quần Áo", "04", "06", edit_quy.getText().toString(), arrchi);
                    c = objectthuchi.kqquychi("Cho vay", "04", "06", edit_quy.getText().toString(), arrchi);
                    d = objectthuchi.kqquychi("Sinh Hoạt", "04", "06", edit_quy.getText().toString(), arrchi);
                    e = objectthuchi.kqquychi("Đi Lại", "04", "06", edit_quy.getText().toString(), arrchi);
                    f = objectthuchi.kqquychi("Trả Nợ", "04", "06", edit_quy.getText().toString(), arrchi);
                    g = objectthuchi.kqquychi("Khác", "04", "06", edit_quy.getText().toString(), arrchi);
                    i = objectthuchi.kqquythu("Tiền Lương", "04", "06", edit_quy.getText().toString(), arrthu);
                    j = objectthuchi.kqquythu("Đòi Nợ", "04", "06", edit_quy.getText().toString(), arrthu);
                    k = objectthuchi.kqquythu("Bán Đồ", "04", "06", edit_quy.getText().toString(), arrthu);
                    l = objectthuchi.kqquythu("Đi Vay", "04", "06", edit_quy.getText().toString(), arrthu);
                    m = objectthuchi.kqquythu("Khác", "04", "06", edit_quy.getText().toString(), arrthu);
                } else if (arg2 == 2) {

                    a = objectthuchi.kqquychi("Ăn Uống", "07", "09", edit_quy.getText().toString(), arrchi);
                    b = objectthuchi.kqquychi("Quần Áo", "07", "09", edit_quy.getText().toString(), arrchi);
                    c = objectthuchi.kqquychi("Cho vay", "07", "09", edit_quy.getText().toString(), arrchi);
                    d = objectthuchi.kqquychi("Sinh Hoạt", "07", "09", edit_quy.getText().toString(), arrchi);
                    e = objectthuchi.kqquychi("Đi Lại", "07", "09", edit_quy.getText().toString(), arrchi);
                    f = objectthuchi.kqquychi("Trả Nợ", "07", "09", edit_quy.getText().toString(), arrchi);
                    g = objectthuchi.kqquychi("Khác", "07", "09", edit_quy.getText().toString(), arrchi);
                    i = objectthuchi.kqquythu("Tiền Lương", "07", "09", edit_quy.getText().toString(), arrthu);
                    j = objectthuchi.kqquythu("Đòi Nợ", "07", "09", edit_quy.getText().toString(), arrthu);
                    k = objectthuchi.kqquythu("Bán Đồ", "07", "09", edit_quy.getText().toString(), arrthu);
                    l = objectthuchi.kqquythu("Đi Vay", "07", "09", edit_quy.getText().toString(), arrthu);
                    m = objectthuchi.kqquythu("Khác", "07", "09", edit_quy.getText().toString(), arrthu);
                } else if (arg2 == 3) {

                    a = objectthuchi.kqquychi("Ăn Uống", "10", "12", edit_quy.getText().toString(), arrchi);
                    b = objectthuchi.kqquychi("Quần Áo", "10", "12", edit_quy.getText().toString(), arrchi);
                    c = objectthuchi.kqquychi("Cho vay", "10", "12", edit_quy.getText().toString(), arrchi);
                    d = objectthuchi.kqquychi("Sinh Hoạt", "10", "12", edit_quy.getText().toString(), arrchi);
                    e = objectthuchi.kqquychi("Đi Lại", "10", "12", edit_quy.getText().toString(), arrchi);
                    f = objectthuchi.kqquychi("Trả Nợ", "10", "12", edit_quy.getText().toString(), arrchi);
                    g = objectthuchi.kqquychi("Khác", "10", "12", edit_quy.getText().toString(), arrchi);
                    i = objectthuchi.kqquythu("Tiền Lương", "10", "12", edit_quy.getText().toString(), arrthu);
                    j = objectthuchi.kqquythu("Đòi Nợ", "10", "12", edit_quy.getText().toString(), arrthu);
                    k = objectthuchi.kqquythu("Bán Đồ", "10", "12", edit_quy.getText().toString(), arrthu);
                    l = objectthuchi.kqquythu("Đi Vay", "10", "12", edit_quy.getText().toString(), arrthu);
                    m = objectthuchi.kqquythu("Khác", "10", "12", edit_quy.getText().toString(), arrthu);
                }
                selectChart();
            }
        });

        //danh sách theo năm


        DoiNgay doi = new DoiNgay();
        lvnam = (ListView) findViewById(R.id.listView_baocaonam);
        double thu = 0;
        double chi = 0;
        arrnam = new ArrayList<BaoCao>();

        //code cua loc
        ArrayList<String> namthu = new ArrayList<String>();
        ArrayList<String> namchi = new ArrayList<String>();
        ArrayList<String> locnam = new ArrayList<String>();
        arrchi = doi.sapXep1(arrchi);
        arrthu = doi.sapXep1(arrthu);
        namchi.add(doi.doiNam1(arrchi.get(0).getNgay()));
        for (int i = 0; i < arrchi.size(); i++) {
            if (namchi.get(namchi.size() - 1).equals(doi.doiNam1(arrchi.get(i).getNgay()))) {

            } else {
                namchi.add(doi.doiNam1(arrchi.get(i).getNgay()));
            }
        }
        namthu.add(doi.doiNam1(arrthu.get(0).getNgay()));
        for (int i = 0; i < arrthu.size(); i++) {
            if (namthu.get(namthu.size() - 1).equals(doi.doiNam1(arrthu.get(i).getNgay()))) {
            } else {
                namthu.add(doi.doiNam1(arrthu.get(i).getNgay()));
            }
        }
        for (int i = 0; i < namthu.size(); i++) {
            locnam.add(namthu.get(i));
        }
        for (int i = 0; i < namchi.size(); i++) {
            locnam.add(namchi.get(i));
        }

        for (int i = 0; i < locnam.size(); i++) {
            for (int j = i + 1; i < locnam.size(); i++) {
                if (locnam.get(i).equals(locnam.get(j))) {
                    locnam.remove(j);
                    j--;
                }
            }
        }
        for (int i = 0; i < locnam.size(); i++) {
            baocaonam = new BaoCao();
            for (int j = 0; j < arrthu.size(); j++) {
                if (locnam.get(i).equals(doi.doiNam1(arrthu.get(j).getNgay()))) {
                    thu += Double.parseDouble(arrthu.get(j).getTienthu());
                }
            }

            baocaonam.setTienthu(String.valueOf(thu));
            for (int k = 0; k < arrchi.size(); k++) {
                if (locnam.get(i).equals(doi.doiNam1(arrchi.get(k).getNgay()))) {
                    chi += Double.parseDouble(arrchi.get(k).getTienchi());
                }
            }

            baocaonam.setTienchi(String.valueOf(chi));
            baocaonam.setNgay(locnam.get(i));
            arrnam.add(baocaonam);
            thu = 0;
            chi = 0;
        }

        arrnam = doi.sapXepnam(arrnam);
        adapterbaocaonam = new BaoCaoNam(this, R.layout.t_custom_danhsachnam, arrnam);
        lvnam.setAdapter(adapterbaocaonam);
        lvnam.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                objectthuchi = new TienThuChi();
                //gia trị nam chon tren listview
                String nam = arrnam.get(arg2).getNgay();
                a = objectthuchi.kqnamchi("Ăn Uống", nam, arrchi);
                b = objectthuchi.kqnamchi("Quần Áo", nam, arrchi);
                c = objectthuchi.kqnamchi("Cho vay", nam, arrchi);
                d = objectthuchi.kqnamchi("Sinh Hoạt", nam, arrchi);
                e = objectthuchi.kqnamchi("Đi Lại", nam, arrchi);
                f = objectthuchi.kqnamchi("Trả Nợ", nam, arrchi);
                g = objectthuchi.kqnamchi("Khác", nam, arrchi);
                i = objectthuchi.kqnamthu("Tiền Lương", nam, arrthu);
                j = objectthuchi.kqnamthu("Đòi Nợ", nam, arrthu);
                k = objectthuchi.kqnamthu("Bán Đồ", nam, arrthu);
                l = objectthuchi.kqnamthu("Đi Vay", nam, arrthu);
                m = objectthuchi.kqnamthu("Khác", nam, arrthu);

                selectChart();
            }
        });
    }

    public void dsThang() {
        if (arrbaocaothang.size() != 0) {
            for (int i = 0; i < arrbaocaothang.size(); i++) {
                if (Double.parseDouble(arrbaocaothang.get(i).getTienchi()) == 0 && Double.parseDouble(arrbaocaothang.get(i).getTienthu()) == 0) {
                    arrbaocaothang.remove(i);
                    imageId.remove(i);
                    arrcalendar.remove(i);
                    arrtitlethang.remove(i);
                    i--;
                }
            }
        }
    }

    public void baoCaoNgayHienTai() {
        danhsach = new BaoCao();
        doingaychi = new DoiNgay();
        danhsach.setTitle(title[0]);
        danhsach.setNgay(doingaychi.ngay(datehientai));
        danhsach.setTienthu(String.valueOf(sumthu));
        danhsach.setTienchi(String.valueOf(sumchi));
        arrhientai.add(danhsach);
    }

    public void baoCaoThangHienTai() {
        doingaychi = new DoiNgay();
        for (int i = 0; i < arrthu.size(); i++) {
            if (doingaychi.doiThang1(arrthu.get(i).getNgay()).equals(thanghientai)) {
                String a = arrthu.get(i).getTienthu();
                sumthuthang += Double.parseDouble(a);
            }
        }
        for (int i = 0; i < arrchi.size(); i++) {
            if (doingaychi.doiThang1(arrchi.get(i).getNgay()).equals(thanghientai)) {
                String a = arrchi.get(i).getTienchi();
                sumchithang += Double.parseDouble(a);
            }
        }
        danhsachthang = new BaoCao();
        doingaychi = new DoiNgay();
        danhsachthang.setTitle(title[1]);
        danhsachthang.setNgay(doingaychi.thang(datehientai));
        danhsachthang.setTienthu(String.valueOf(sumthuthang));
        danhsachthang.setTienchi(String.valueOf(sumchithang));
        arrhientai.add(danhsachthang);
    }

    public void baoCaoNamHienTai() {
        doingaychi = new DoiNgay();
        for (int i = 0; i < arrthu.size(); i++) {
            if (doingaychi.doiNam1(arrthu.get(i).getNgay()).equals(namhientai)) {
                String a = arrthu.get(i).getTienthu();
                sumthunam += Double.parseDouble(a);
            }
        }
        for (int i = 0; i < arrchi.size(); i++) {
            if (doingaychi.doiNam1(arrchi.get(i).getNgay()).equals(namhientai)) {
                String a = arrchi.get(i).getTienchi();
                sumchinam += Double.parseDouble(a);
            }
        }
        danhsachnam = new BaoCao();
        doingaychi = new DoiNgay();
        danhsachnam.setTitle(title[2]);
        danhsachnam.setNgay(doingaychi.nam(datehientai));
        danhsachnam.setTienthu(String.valueOf(sumthunam));
        danhsachnam.setTienchi(String.valueOf(sumchinam));
        arrhientai.add(danhsachnam);
    }

    public void loadTab() {
        final TabHost tab = (TabHost) findViewById(android.R.id.tabhost);
        //goi cau lenh setup
        tab.setup();
        TabHost.TabSpec spec;
        //tao tab 1
        spec = tab.newTabSpec("t1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("hiện");
        tab.addTab(spec);
        //tao tab2
        spec = tab.newTabSpec("t2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("tháng");
        tab.addTab(spec);
        //
        spec = tab.newTabSpec("t3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("quý");
        tab.addTab(spec);

        spec = tab.newTabSpec("t4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("năm");
        tab.addTab(spec);
        tab.setCurrentTab(0);
    }

    public void layNgayGiohientai() {
        Calendar ngay = Calendar.getInstance();
        //dinh dang 12h
        String dinhdang24 = "dd/MM/yyyy";
        SimpleDateFormat dinhdang = null;
        dinhdang = new SimpleDateFormat(dinhdang24, Locale.getDefault());
        doingaychi = new DoiNgay();
        datehientai = doingaychi.doiDate(dinhdang.format(ngay.getTime()));
        thanghientai = doingaychi.doiThang(dinhdang.format(ngay.getTime()));
        namhientai = doingaychi.doiNam(dinhdang.format(ngay.getTime()));
    }

    public void layNamHienTai() {
        Calendar nam = Calendar.getInstance();
        //dinh dang 12h
        String dinhdang24 = "yyyy";
        SimpleDateFormat dinhdang = null;
        dinhdang = new SimpleDateFormat(dinhdang24, Locale.getDefault());
        thangnam.setText(dinhdang.format(nam.getTime()));
        datetimequy = dinhdang.format(nam.getTime());
    }

    //danh sach cac nam
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //danh sach quy
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showDatePickerDialog1(View v) {
        DialogFragment newFragment = new DatePickerFragment1();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void danhSachThu() {
        mDbthu = dbthu.getWritableDatabase();
        String query = "select * from thu";
        mCursorthu = mDbthu.rawQuery(query, null);
        arrthu = new ArrayList<BaoCao>();
        doingaythu = new DoiNgay();
        if (mCursorthu.moveToFirst()) {
            do {
                objectchi2 = new BaoCao();
                objectchi2.setTienthu(mCursorthu.getString(2));
                objectchi2.setNgay(doingaythu.doiDate(mCursorthu.getString(4)));
                objectchi2.setNhom(mCursorthu.getString(3));
                arrthu.add(objectchi2);
            } while (mCursorthu.moveToNext());
        }
    }

    public void danhSachChi() {
        mDbchi = dbchi.getWritableDatabase();
        String querychi = "select * from chi";
        mCursorchi = mDbchi.rawQuery(querychi, null);
        arrchi = new ArrayList<BaoCao>();
        doingaychi = new DoiNgay();
        if (mCursorchi.moveToFirst()) {
            do {
                objectchi2 = new BaoCao();
                objectchi2.setTienchi(mCursorchi.getString(2));
                objectchi2.setNgay(doingaychi.doiDate(mCursorchi.getString(4)));
                objectchi2.setNhom(mCursorchi.getString(3));
                arrchi.add(objectchi2);
            } while (mCursorchi.moveToNext());
        }
    }

    public void openPieChartChi(double anuong, double quanao, double chovay, double sinhhoat, double dilai, double trano, double khac) {

        // Pie Chart Section Value
        ArrayList<Double> distribution = new ArrayList<Double>();
        distribution.add(0, anuong);
        distribution.add(1, quanao);
        distribution.add(2, chovay);
        distribution.add(3, sinhhoat);
        distribution.add(4, dilai);
        distribution.add(5, trano);
        distribution.add(6, khac);

        // Color of each Pie Chart Sections
        ArrayList<String> code = new ArrayList<String>();
        code.add(0, "Ăn Uống" + ":" + anuong);
        code.add(1, "Quần Áo " + ":" + quanao);
        code.add(2, "Cho vay" + ":" + chovay);
        code.add(3, "Sinh Hoạt" + ":" + sinhhoat);
        code.add(4, "Đi Lại" + ":" + dilai);
        code.add(5, "Trả Nợ" + ":" + trano);
        code.add(6, "Khác" + ":" + khac);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(0, Color.BLUE);
        colors.add(1, Color.MAGENTA);
        colors.add(2, Color.GREEN);
        colors.add(3, Color.CYAN);
        colors.add(4, Color.RED);
        colors.add(5, Color.YELLOW);
        colors.add(6, Color.LTGRAY);
        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries("");
        for (int i = 0; i < distribution.size(); i++) {
            // Adding a slice with its values and name to the Pie Chart
            if (distribution.get(i) == 0.0) {
                distribution.remove(i);
                code.remove(i);
                colors.remove(i);
                i--;
            } else {
                distributionSeries.add(code.get(i), distribution.get(i));
            }
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < distribution.size(); i++) {
            if (distribution.get(i) == 0.0) {
                distribution.remove(i);
                code.remove(i);
                colors.remove(i);
                i--;
            } else {
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
                seriesRenderer.setColor(colors.get(i));
                seriesRenderer.setDisplayChartValues(true);
                defaultRenderer.addSeriesRenderer(seriesRenderer);
            }
        }
        defaultRenderer.setChartTitle(" Biểu Đồ Tròn Chi Tiêu");
        defaultRenderer.setLabelsTextSize(40);
        defaultRenderer.setChartTitleTextSize(40);
        defaultRenderer.setLegendTextSize(40);
        defaultRenderer.setZoomButtonsVisible(false);
        defaultRenderer.setApplyBackgroundColor(true);
        defaultRenderer.setBackgroundColor(Color.DKGRAY);
        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getPieChartIntent(getBaseContext(), distributionSeries, defaultRenderer, "Quản Lý Chi Tiêu");
        // Start Activity
        startActivity(intent);

    }

    //BIEU DO TRON THU NHAP
    public void openPieChartThu(double tienluong, double doino, double bando, double divay, double khac) {

        // Pie Chart Section Value
        ArrayList<Double> distribution = new ArrayList<Double>();
        distribution.add(0, tienluong);
        distribution.add(1, doino);
        distribution.add(2, bando);
        distribution.add(3, divay);
        distribution.add(4, khac);

        // Color of each Pie Chart Sections
        ArrayList<String> code = new ArrayList<String>();
        code.add(0, "Tiền lương" + ":" + tienluong);
        code.add(1, "Đòi nợ " + ":" + doino);
        code.add(2, "Bán đồ" + ":" + bando);
        code.add(3, "Đi vay" + ":" + divay);
        code.add(4, "Khác" + ":" + khac);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(0, Color.BLUE);
        colors.add(1, Color.MAGENTA);
        colors.add(2, Color.GREEN);
        colors.add(3, Color.CYAN);
        colors.add(4, Color.RED);

        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries("");
        for (int i = 0; i < distribution.size(); i++) {
            // Adding a slice with its values and name to the Pie Chart
            if (distribution.get(i) == 0.0) {
                distribution.remove(i);
                code.remove(i);
                colors.remove(i);
                i--;
            } else {
                distributionSeries.add(code.get(i), distribution.get(i));
            }
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < distribution.size(); i++) {
            if (distribution.get(i) == 0.0) {
                distribution.remove(i);
                code.remove(i);
                colors.remove(i);
                i--;
            } else {
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
                seriesRenderer.setColor(colors.get(i));
                seriesRenderer.setDisplayChartValues(true);
                defaultRenderer.addSeriesRenderer(seriesRenderer);
            }
        }
        defaultRenderer.setChartTitle(" Biểu Đồ Tròn Thu Nhập");
        defaultRenderer.setLabelsTextSize(40);
        defaultRenderer.setChartTitleTextSize(40);
        defaultRenderer.setLegendTextSize(40);
        defaultRenderer.setZoomButtonsVisible(false);
        defaultRenderer.setApplyBackgroundColor(true);
        defaultRenderer.setBackgroundColor(Color.DKGRAY);
        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getPieChartIntent(getBaseContext(), distributionSeries, defaultRenderer, "Quản Lý Chi Tiêu");
        // Start Activity
        startActivity(intent);

    }

    //BIEU DO COT CHI TIEU
    public void openBarChartChi(double anuong, double quanao, double chovay, double sinhhoat, double dilai, double trano, double khac) {
        //Danh sach cot
        ArrayList<Double> distribution = new ArrayList<Double>();
        distribution.add(anuong);
        distribution.add(quanao);
        distribution.add(chovay);
        distribution.add(sinhhoat);
        distribution.add(dilai);
        distribution.add(trano);
        distribution.add(khac);


        ArrayList<String> code = new ArrayList<String>();
        code.add("Ăn Uống");
        code.add("Quần Áo ");
        code.add("Cho vay");
        code.add("Sinh Hoạt");
        code.add("Đi Lại");
        code.add("Trả Nợ");
        code.add("Khác");
        CategorySeries series = new CategorySeries("");
        for (int i = 0; i < distribution.size(); i++) {
            series.add(code.get(i), distribution.get(i));
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series.toXYSeries());

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setDisplayChartValues(true);
        renderer.setChartValuesTextSize(40);
        renderer.setChartValuesSpacing((float) 15);
        renderer.setFillBelowLine(true);
        renderer.setColor(Color.GREEN);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setChartTitle("Biểu Đồ Cột Chi Tiêu ");
        mRenderer.setChartTitleTextSize(40);
        mRenderer.setBarSpacing(0.5);
        mRenderer.setXLabelsColor(Color.WHITE);
        mRenderer.setLabelsTextSize(40);
        mRenderer.setLegendTextSize(30);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.DKGRAY);
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(0);
        for (int i = 0; i < distribution.size(); i++) {
            mRenderer.addXTextLabel(i + 1, code.get(i));
        }
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, mRenderer, BarChart.Type.DEFAULT);
        startActivity(intent);
    }

    //BIEU DO COT THU NHAP
    public void openBarChartThu(double tienluong, double doino, double bando, double divay, double khac) {
        //Danh sach cot
        ArrayList<Double> distribution = new ArrayList<Double>();
        distribution.add(tienluong);
        distribution.add(doino);
        distribution.add(bando);
        distribution.add(divay);
        distribution.add(khac);

        ArrayList<String> code = new ArrayList<String>();
        code.add("Tiền lương");
        code.add("Đòi nợ");
        code.add("Bán đồ");
        code.add("Đi vay");
        code.add("Khác");

        CategorySeries series = new CategorySeries("");
        for (int i = 0; i < distribution.size(); i++) {
            series.add(code.get(i), distribution.get(i));
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series.toXYSeries());

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setDisplayChartValues(true);
        renderer.setChartValuesTextSize(40);
        renderer.setChartValuesSpacing((float) 15);
        renderer.setFillBelowLine(true);
        renderer.setColor(Color.GREEN);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setChartTitle("Biểu Đồ Cột Thu Nhập ");
        mRenderer.setChartTitleTextSize(40);
        mRenderer.setBarSpacing(0.5);
        mRenderer.setXLabelsColor(Color.WHITE);
        mRenderer.setLabelsTextSize(40);
        mRenderer.setLegendTextSize(30);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.DKGRAY);
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(0);
        for (int i = 0; i < distribution.size(); i++) {
            mRenderer.addXTextLabel(i + 1, code.get(i));
        }
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, mRenderer, BarChart.Type.DEFAULT);
        startActivity(intent);
    }

    @SuppressLint({"ValidFragment", "NewApi"})
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = 0;
            int day = 0;
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onDateSet(DatePicker view, int year, int month, int day) {

            thangnam.setText(String.valueOf(year));
        }
    }

    @SuppressLint({"ValidFragment", "NewApi"})
    public class DatePickerFragment1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = 0;
            int day = 0;
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            edit_quy.setText(String.valueOf(year));
        }
    }

    public void selectChart() {
        final Dialog dialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.select_chart, null, false);
        dialog.setContentView(v);
        dialog.setTitle("Chọn Biểu Đồ");
        btn_thuCot = (Button) v.findViewById(R.id.btn_thuCot);
        btn_thuTron = (Button) v.findViewById(R.id.btn_thuTron);
        btn_chiCot = (Button) v.findViewById(R.id.btn_chiCot);
        btn_chiTron = (Button) v.findViewById(R.id.btn_chiTron);
        btn_thuCot.setOnClickListener(new ButtonEvent());
        btn_thuTron.setOnClickListener(new ButtonEvent());
        btn_chiCot.setOnClickListener(new ButtonEvent());
        btn_chiTron.setOnClickListener(new ButtonEvent());
        dialog.show();
    }

    class ButtonEvent implements OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.btn_thuCot:
                    openBarChartThu(i, j, k, l, m);
                    break;
                case R.id.btn_thuTron:
                    openPieChartThu(i, j, k, l, m);
                    break;
                case R.id.btn_chiCot:
                    openBarChartChi(a, b, c, d, e, f, g);
                    break;
                case R.id.btn_chiTron:
                    openPieChartChi(a, b, c, d, e, f, g);
                    break;
            }
        }
    }

}
