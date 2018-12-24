package com.app.group.alertview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.app.gruop.alertview.AlertView;
import com.app.gruop.alertview.OnItemClickListener;

public class AppDialog extends BaseDialog implements OnItemClickListener {

    public static class Instance {
        private final static AppDialog instance = new AppDialog();
    }

    public static AppDialog getInstance() {return Instance.instance;}

    /**
     * 显示默认样式
     */
    public void show() {
        if (TextUtils.isEmpty(cancel) && listener != null) cancel = "取消";//自动添加取消按钮
        if (!TextUtils.isEmpty(cancel) && cancel.equals("-1")) cancel = null;
        mAlertView = new AlertView(title, msg, cancel, new String[]{commit}, null, !TextUtils.isEmpty(errorId), context, AlertView.Style.Alert, this);
        if (view != null) mAlertView.addExtView(view);
        mAlertView.setCancelable(isCancelable());
        setOnDismissListener(onDismissListener);
        mAlertView.show();
    }

    /**
     * 显示分享样式
     */
    public void showShare() {
        mAlertView = new AlertView(title, cancel, drawables, texts, context, this);
        mAlertView.setCancelable(isCancelable());
        setOnDismissListener(onDismissListener);
        mAlertView.show();
    }

    /**
     * 显示编辑框样式
     */
    public void showEdit() {
        if (TextUtils.isEmpty(cancel) && listener != null) cancel = "取消";//自动添加取消按钮
        if (!TextUtils.isEmpty(cancel) && cancel.equals("-1")) cancel = null;

        AlertView.Builder builder = new AlertView.Builder();
        builder.setContext(context).setStyle(AlertView.Style.Alert).setCancelText(cancel).setDestructive(commit).setContext(context).setTitle(title).setHint(hint).setValue(editTextValue).setInputType(inputType).setUnit(unit).setFilters(editTextFilters).setOnItemClickListener(this);
        mAlertView = builder.build();
        mAlertView.setCancelable(isCancelable());
        setOnDismissListener(onDismissListener);
        mAlertView.showEditView();
        if (view != null) mAlertView.addExtView(view);
    }

    @Override
    public void onItemClick(Object o, int position) {
        editTextValue = mAlertView.getEditTextValue();
        if (listener == null) {
            dismiss();
        } else {
            if (position == -2) {
                Toast.makeText(context, "调接口查询详细的报错信息：" + errorId, Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onItemClick(position);
        }
    }

    public AppDialog setContext(Context context) {
        this.context = context;
        return this;
    }

    public AppDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public AppDialog setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public AppDialog setCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public AppDialog setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    public AppDialog setListener(BaseDialog.OnHshDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public AppDialog setDrawables(Drawable[] drawables) {
        this.drawables = drawables;
        return this;
    }

    public AppDialog setTexts(String[] texts) {
        this.texts = texts;
        return this;
    }

    public AppDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public AppDialog setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public AppDialog setValue(String value) {
        this.editTextValue = value;
        return this;
    }

    public AppDialog setInputType(int inputType) {
        this.inputType = inputType;
        return this;
    }

    public AppDialog setFilters(InputFilter[] filters) {
        this.editTextFilters = filters;
        return this;
    }

    public AppDialog setView(View view) {
        this.view = view;
        return this;
    }

    public AppDialog setErrorId(String errorId) {
        this.errorId = errorId;
        return this;
    }

}
