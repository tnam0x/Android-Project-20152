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
import java.util.List;

import Objects.TienThuChi;

/**
 * @author tungtx91
 */

public class DanhSachThu extends ArrayAdapter<TienThuChi> {
    Context context;
    int layoutResourceId;
    List<TienThuChi> listData;

    public DanhSachThu(Context context, int layoutResourceId, List<TienThuChi> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listData = data;
    }

    @Override
    public int getCount() {

        return listData.size();
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ItemHolder();
            holder.nhom = (TextView) row.findViewById(R.id.textView_nhomkhoanthu_custom);
            holder.ngaythang = (TextView) row.findViewById(R.id.textView_ngaykhoanthu_custom);
            holder.tien = (TextView) row.findViewById(R.id.textView_tienkhoanthu_custom);
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