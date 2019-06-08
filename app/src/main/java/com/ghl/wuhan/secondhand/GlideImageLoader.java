package com.ghl.wuhan.secondhand;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * 项目名称：com.ghl.wuhan.secondhand
 * 类描述：
 * 创建人：Liting
 * 创建时间：2018/10/21 20:43
 * 修改人：Liting
 * 修改时间：2018/10/21 20:43
 * 修改备注：
 * 版本：
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide 加载图片简单用法
       Glide.with(context).load(path).into(imageView);
        //Glide.with(context).load(R.drawable.p1).into(imageView);
        //Glide.with(context).load(R.drawable.p2).into(imageView);
        //Glide.with(context).load(R.drawable.p3).into(imageView);
        //Glide.with(context).load(R.drawable.p4).into(imageView);
        //Picasso 加载图片简单用法
        //        Picasso.with(context).load(path).into(imageView);

//        //用fresco加载图片简单用法，记得要写下面的createImageView方法
//        Uri uri = Uri.parse((String) path);
//        imageView.setImageURI(uri);
    }
    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
//    @Override
//    public ImageView createImageView(Context context) {
//        //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
//        SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
//        return simpleDraweeView;
//    }
}
