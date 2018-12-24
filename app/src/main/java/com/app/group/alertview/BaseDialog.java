package com.app.group.alertview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;

import com.app.gruop.alertview.AlertView;

public class BaseDialog {

    protected Context context;
    //标题，默认"提示"
    protected String title = "提示";
    //内容，默认空
    protected String msg = null;
    //左按钮，默认空，不显示
    protected String cancel = null;
    //右按钮，默认"确定"
    protected String commit = "确定";
    //监听器
    protected OnHshDialogListener listener;
    //分享框图片
    protected Drawable[] drawables;
    //文字
    protected String[] texts;
    //编辑框hint
    protected String hint;
    //编辑框单位
    protected String unit;
    //编辑框输入类型，默认是TEXT
    protected int inputType = InputType.TYPE_CLASS_TEXT;
    //编辑框文字
    protected String editTextValue;
    //编辑框过滤器
    protected InputFilter[] editTextFilters;
    //View
    protected View view;

    private boolean isCancelable = true;
    //错误id
    protected String errorId;

    protected OnDismissListener onDismissListener;

    /**
     * 获取编辑
     * @return
     */
    public String getEditTextValue() {
        return editTextValue == null ? "" : editTextValue;
    }

    protected AlertView mAlertView;

    public BaseDialog() {
    }

    public void dismiss() {
        if (mAlertView != null) mAlertView.dismiss();
        clean();
    }

    public boolean isShowing() {
        return (mAlertView != null) && mAlertView.isShowing();
    }

    /**
     * 是否可以点击外部消失
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
        if (mAlertView != null) mAlertView.setCancelable(isCancelable);
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;

        mAlertView.setOnDismissListener(new com.app.gruop.alertview.OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                clean();
                if (onDismissListener != null) onDismissListener.onDismiss();
            }
        });
    }

    public interface OnHshDialogListener {
        void onItemClick(int position);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    /**
     * 恢复初始化数据
     * 在Dialog关闭的时候调用
     */
    private void clean() {
        this.context = null;
        this.title = "提示";
        this.msg = null;
        this.cancel = null;
        this.commit = "确定";
        this.listener = null;
        this.drawables = null;
        this.texts = null;
        this.hint = null;
        this.unit = null;
        this.editTextValue = null;
        this.editTextFilters = null;
        this.inputType = InputType.TYPE_CLASS_TEXT;
        this.view = null;
        this.errorId = null;
        onDismissListener = null;
        setCancelable(true);
    }
}
