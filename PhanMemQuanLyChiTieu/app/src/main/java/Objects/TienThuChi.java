package Objects;

import java.util.ArrayList;

import Adapter.DoiNgay;

public class TienThuChi {
    private String tien;
    private String nhom;
    private String ghichu;
    private String ngaythang;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTien() {
        return this.tien;
    }

    public void setTien(String tien) {
        this.tien = tien;
    }

    public String getVND() {
        return this.tien + " (VND)";
    }

    public String getNgoaiTe(String string) {
        String m = "";
        ArrayList<ForeignCurrency> moneys = new ArrayList<>();
        try {
            moneys = HTMLParser.parseHTML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ForeignCurrency money : moneys) {
            if (money.symbol.equals(string)) {
                m = money.VNDToAntoher(getTien());
            }
        }
        return m;
    }

    public String getTyGia(String string) {
        String m = "";
        ArrayList<ForeignCurrency> moneys = new ArrayList<>();
        try {
            moneys = HTMLParser.parseHTML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ForeignCurrency money : moneys) {
            if (money.symbol.equals(string)) {
                m = money.rate + " (VND)";
            }
        }
        return m;
    }

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }

    public String getNgaythang() {
        return ngaythang;
    }

    public void setNgaythang(String ngaythang) {
        this.ngaythang = ngaythang;
    }

    public int kqngaychi(String nhom, String ngay, ArrayList<BaoCao> a) {
        int thuthang = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getNgay().equals(ngay) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienchi());
            }
        }
        return thuthang;
    }

    public int kqngaythu(String nhom, String ngay, ArrayList<BaoCao> a) {
        int thuthang = 0;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getNgay().equals(ngay) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienthu());
            }
        }
        return thuthang;
    }

    public int kqthangchi(String nhom, String thang, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doingaychi = new DoiNgay();
        for (int i = 0; i < a.size(); i++) {
            if (doingaychi.doiThang1(a.get(i).getNgay()).equals(thang) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienchi());
            }
        }
        return thuthang;
    }

    public int kqthangthu(String nhom, String thang, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doingaychi = new DoiNgay();
        for (int i = 0; i < a.size(); i++) {
            if (doingaychi.doiThang1(a.get(i).getNgay()).equals(thang) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienthu());
            }
        }
        return thuthang;
    }

    public int kqquychi(String nhom, String thangdau, String thangsau, String nam, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doiquychi = new DoiNgay();
        String dau = nam + thangdau;
        int quydau = Integer.parseInt(dau);
        String cuoi = nam + thangsau;
        int quycuoi = Integer.parseInt(cuoi);
        for (int i = 0; i < a.size(); i++) {
            if (Integer.parseInt(doiquychi.doiThang1(a.get(i).getNgay())) >= quydau && Integer.parseInt(doiquychi.doiThang1(a.get(i).getNgay())) <= quycuoi && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienchi());
            }
        }
        return thuthang;
    }

    public int kqquythu(String nhom, String thangdau, String thangsau, String nam, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doiquychi = new DoiNgay();
        String dau = nam + thangdau;
        int quydau = Integer.parseInt(dau);
        String cuoi = nam + thangsau;
        int quycuoi = Integer.parseInt(cuoi);
        for (int i = 0; i < a.size(); i++) {
            if (Integer.parseInt(doiquychi.doiThang1(a.get(i).getNgay())) >= quydau && Integer.parseInt(doiquychi.doiThang1(a.get(i).getNgay())) <= quycuoi && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienthu());
            }
        }
        return thuthang;
    }

    public int kqnamchi(String nhom, String nam, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doingaychi = new DoiNgay();
        for (int i = 0; i < a.size(); i++) {
            if (doingaychi.doiNam1(a.get(i).getNgay()).equals(nam) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienchi());
            }
        }
        return thuthang;
    }

    public int kqnamthu(String nhom, String nam, ArrayList<BaoCao> a) {
        int thuthang = 0;
        DoiNgay doingaychi = new DoiNgay();
        for (int i = 0; i < a.size(); i++) {
            if (doingaychi.doiNam1(a.get(i).getNgay()).equals(nam) && a.get(i).getNhom().equals(nhom)) {
                thuthang += Integer.parseInt(a.get(i).getTienthu());
            }
        }
        return thuthang;
    }
}





