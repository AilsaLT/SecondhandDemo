package com.ghl.wuhan.secondhand.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ghl.wuhan.secondhand.DO.Goods;
import com.ghl.wuhan.secondhand.R;
import com.ghl.wuhan.secondhand.goods_message.GoodsItemActivity;

import java.util.List;

import static com.ghl.wuhan.secondhand.R.id.img_goodsImg;

/**
 * 项目名称：com.ghl.wuhan.secondhand.find_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/4/7 22:40
 * 修改人：Liting
 * 修改时间：2019/4/7 22:40
 * 修改备注：
 * 版本：
 */

public class GoodsItemAdapter extends RecyclerView.Adapter<GoodsItemAdapter.ViewHolder> {
    private List<Goods> mgoodsList;
    private Context mcontext;


    //内部类ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        View goodsView;
        ImageView goods_img;
        TextView goodsID;
        TextView goodsName;
        TextView goods_price;
        TextView goods_quality;
        TextView goods_unit;
        TextView goods_qq;

        //获取布局中的实例，  这里的view是指RecyclerView的子项的最外层布局
        public ViewHolder(View view) {
            super( view );
            goodsView = view;
            goods_img = (ImageView) view.findViewById( img_goodsImg );
            goodsID = (TextView) view.findViewById( R.id.tv_goodsId );
            goodsName = (TextView)view.findViewById( R.id.tv_goodsName );
            goods_price = (TextView)view.findViewById( R.id.tv_goodsPrice );
            goods_quality = (TextView)view.findViewById( R.id.tv_goodsQuantity );
            goods_unit = (TextView)view.findViewById( R.id.tv_goodsUnit );
            goods_qq = (TextView) view.findViewById(R.id.tv_goodsQq);
        }
    }

    //构造函数
    //这个方法用于把眼展示的数据源传进来，并赋值给一份全局变量mgoodsList,后续的操作都基于这个数据源
    public GoodsItemAdapter(Context context,List<Goods> goodsList) {
        mgoodsList = goodsList;
        mcontext = context;
    }

    //此方法用于创建ViewHolder的实例
    //由于继承自RecyclerView.Adapter，需要重写下面的三个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //动态加载布局 ，   首先将goods_item布局加载进来，
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.goods_item,parent,false );
        //再创建ViewHolder的实例，并将加载的布局传入到构造函数中，
        final ViewHolder holder = new ViewHolder( view );

        //RecyclerView的点击事件，点击后进入商品详情
        holder.goodsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position  =  holder.getAdapterPosition();
                Goods goods   =  mgoodsList.get(position);
//                Toast.makeText(v.getContext(),"你点击了goods_item!!!"+goods.getGoodsName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(),GoodsItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("goods",goods);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        //最后将ViewHolder的实例返回
        return holder;
    }

    @Override
    //此方法适用于对RecyclerView子项的数据进行赋值的，会在每个子项被滚到屏幕内的时候执行
    public void onBindViewHolder(ViewHolder holder, int position) {
        //通过position参数得到当前的子项实例
        Goods goods = mgoodsList.get( position );
        //设置数据
//        byte[] img = goods.getGoodsImg();
        String pictureUrl = goods.getPictureUrl();


        //若图片Url为空
        if(pictureUrl == null){

            holder.goods_img.setImageResource(R.drawable.loading);

        }else{
            Glide.with(mcontext).load(pictureUrl).into(holder.goods_img);
        }
        holder.goodsID.setText( goods.getGoodsID() );
        holder.goodsName.setText( goods.getGoodsName() );
        holder.goods_price.setText( ""+goods.getPrice() );
        holder.goods_quality.setText( ""+ goods.getQuality() );
        holder.goods_unit.setText( goods.getUnit() );
        holder.goods_qq.setText(goods.getQq());
    }

    @Override
    //得到RecyclerView子项的数目
    public int getItemCount() {
        return mgoodsList.size();
    }

}
