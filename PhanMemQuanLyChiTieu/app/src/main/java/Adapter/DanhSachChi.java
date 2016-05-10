package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phanmemquanlychitieu.R;

import java.util.ArrayList;

import Objects.TienThuChi;

/**
 * @author tungtx91
 */

public class DanhSachChi extends ArrayAdapter<TienThuChi> {
    Context context;
    int layoutResourceId;
    ArrayList<TienThuChi> listData;

    public DanhSachChi(Context context, int layoutResourceId, ArrayList<TienThuChi> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listData = data;
    }

    @Override
    public int getCount() {
        return this.listData.size();
    }

    @Override
    public TienThuChi getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ItemHolder();
            holder.nhom = (TextView) row.findViewById(R.id.textView_nhomchi);
            holder.ngaythang = (TextView) row.findViewById(R.id.textView_ngaychi);
            holder.tien = (TextView) row.findViewById(R.id.textView_tienchi);
            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }
        TienThuChi item = listData.get(position);
        holder.nhom.setText(item.getNhom());
        holder.ngaythang.setText(item.getNgaythang());
        holder.tien.setText(item.getVND());
        return row;
    }

    static class ItemHolder {
        TextView nhom;
        TextView ngaythang;
        TextView tien;
    }
}