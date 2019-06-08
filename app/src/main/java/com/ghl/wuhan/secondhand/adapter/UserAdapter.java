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

import com.ghl.wuhan.secondhand.DO.User;
import com.ghl.wuhan.secondhand.R;

import java.util.List;

/**
 * 项目名称：com.ghl.wuhan.secondhand.home_activity
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/4/5 20:10
 * 修改人：Liting
 * 修改时间：2019/4/5 20:10
 * 修改备注：
 * 版本：
 */

public class UserAdapter extends ArrayAdapter<User> {
    private int resourceId;
    public UserAdapter(Context context, int textViewResourceId, List<User> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    // 这里为简单明了，没有加入对速度优化的考虑
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User userVO = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        //接下来将goods对象和属性和视图控对应起来
        ImageView img_userImg = view.findViewById(R.id.img_userImg);
        TextView tv_uname = view.findViewById(R.id.tv_uname);
        TextView tv_upassword = view.findViewById(R.id.tv_upassword);
        TextView tv_uphone = view.findViewById(R.id.tv_uphone);
        TextView tv_sex = view.findViewById(R.id.tv_sex);
        TextView tv_qq = view.findViewById(R.id.tv_qq);

        tv_uname.setText(userVO.getUname());
        tv_upassword.setText(userVO.getUpassword());
        tv_uphone.setText(userVO.getUphone());
        tv_sex.setText(""+userVO.getSex());
        tv_qq.setText(""+userVO.getQq());

        // 图片的操作
        byte[] bytes = userVO.getUimage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        img_userImg.setImageBitmap(bitmap);

        return view;
    }

}
