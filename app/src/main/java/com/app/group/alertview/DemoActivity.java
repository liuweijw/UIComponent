package com.app.group.alertview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.app.group.R;

/**
 * 精仿iOSAlertViewController控件Demo
 */
public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertview_demo);
    }

    public void alertShow1(View view) {

        getDialog().setMsg("服务器开小差，请稍后重试，或联系地市的保障服务台").show();

    }

    public void alertShow2(View view) {
        getDialog().setTitle("自定义标题").setMsg("服务器开小差，请稍后重试，或联系地市的保障服务台").setListener(new AppDialog.OnHshDialogListener() {
            @Override
            public void onItemClick(int position) {
                if (position == -1) {
                    Toast.makeText(DemoActivity.this, "点击了取消" , Toast.LENGTH_SHORT).show();
                } else if (position == 0) {
                    Toast.makeText(DemoActivity.this, "点击了确认" , Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();//关闭弹框
            }

        }).setErrorId("111").show();
    }

    public void alertShow3(View view) {
        getDialog().setTitle(null).setMsg("服务器开小差，请稍后重试，或联系地市的保障服务台").show();
    }

    public void alertShow4(View view) {
        getDialog().setTitle(null).setCancel("取消").setCommit("确认").setMsg("这是内容").setListener(new AppDialog.OnHshDialogListener() {
            @Override
            public void onItemClick(int position) {
                if (position == -1) {
                    Toast.makeText(DemoActivity.this, "点击了取消" , Toast.LENGTH_SHORT).show();
                } else if (position == 0) {
                    Toast.makeText(DemoActivity.this, "点击了确认" , Toast.LENGTH_SHORT).show();
                }
            }

        }).show();
    }

    public void alertShow5(View view) {
        getDialog().setTitle("输入名字").setHint("请输入名字").setListener(new AppDialog.OnHshDialogListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    String text = getDialog().getEditTextValue();
                    Toast.makeText(DemoActivity.this, "输入内容为：" + text, Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();//关闭弹框
            }
        }).showEdit();//显示编辑框弹框
    }


    public void alertShow6(View view) {
        Drawable[] shareImages = new Drawable[]{getResources().getDrawable(R.mipmap.icon_share_wx), getResources().getDrawable(R.mipmap.icon_share_peng), getResources().getDrawable(R.mipmap.icon_share_wb), getResources().getDrawable(R.mipmap.icon_share_tx)};
        final String[] shareTexts = new String[]{"微信","朋友圈","新浪微博","腾讯微博"};
        getDialog().setTitle("分享到")
                .setDrawables(shareImages)
                .setTexts(shareTexts)
                .setListener(new AppDialog.OnHshDialogListener() {
            @Override
            public void onItemClick(int position) {
                getDialog().dismiss();
                if (position == -1) {
                    return;
                }
                Toast.makeText(DemoActivity.this, "点击" + shareTexts[position] , Toast.LENGTH_SHORT).show();
            }

        }).showShare();
    }


    public void alertShowExt(View view) {
        View viewSelf = getLayoutInflater().inflate(R.layout.view_alertview_self,null);//自定义的布局
        getDialog().setView(viewSelf).show();
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(AppDialog.getInstance() != null && !AppDialog.getInstance().isCancelable()) {
                return false;
            }
            if(AppDialog.getInstance() != null && AppDialog.getInstance().isShowing()) {
                AppDialog.getInstance().dismiss();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public AppDialog getDialog() {
        return AppDialog.getInstance().setContext(this);
    }
}
