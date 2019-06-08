package com.ghl.wuhan.secondhand.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.R;

import java.util.List;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/4/4 17:07
 * 修改人：Liting
 * 修改时间：2019/4/4 17:07
 * 修改备注：
 * 版本：
 */

// 适配器 (这里没有加入速度的考虑,如果想加入请参考书，自已加入。）
public class GoodsAdapter extends  ArrayAdapter<Goods> {
    private int resourceId;

    public GoodsAdapter(Context context, int textViewResourceId, List<Goods> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    // 这里为简单明了，没有加入对速度优化的考虑
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Goods goods = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        //接下来将goods对象和属性和视图控对应起来
        ImageView img_goodsImg = view.findViewById(R.id.img_goodsImg);
        TextView tv_goodsId = view.findViewById(R.id.tv_goodsId);
        TextView tv_goodsName = view.findViewById(R.id.tv_goodsName);
        TextView tv_goodsUnit = view.findViewById(R.id.tv_goodsUnit);
        TextView tv_goodsPrice = view.findViewById(R.id.tv_goodsPrice);
        TextView tv_goodsQuantity = view.findViewById(R.id.tv_goodsQuantity);

        tv_goodsId.setText(goods.getGoodsID());
        tv_goodsName.setText(goods.getGoodsName());
        tv_goodsUnit.setText(goods.getUnit());
        tv_goodsPrice.setText(""+goods.getPrice());
        tv_goodsQuantity.setText(""+goods.getQuality());

        // 图片的操作
        byte[] bytes = goods.getGoodsImg();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        img_goodsImg.setImageBitmap(bitmap);

        return view;
    }
}

