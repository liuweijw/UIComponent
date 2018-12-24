package com.app.gruop.alertview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.alertview.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 精仿iOSAlertViewController控件
 * 点击取消按钮返回 －1，其他按钮从0开始算
 */
public class AlertView {
    public enum Style{
        ActionSheet,//底部弹出
        Alert,//中间弹出
        AlertShare//add--底部弹出分享框
    }
    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );
    public static final int HORIZONTAL_BUTTONS_MAXCOUNT = 2;
    public static final int CANCELPOSITION = -1;//点击取消按钮返回 －1，其他按钮从0开始算
    public static final int ERRORPOSITION = -2;//点击错误按钮返回 －2，其他按钮从0开始算

    private String title;
    private String msg;
    private String[] destructive;
    private String[] others;
    private List<String> mDestructive;
    private List<String> mOthers;
    private String cancel;
    private ArrayList<String> mDatas = new ArrayList<String>();

    private ArrayList<Drawable> mGridDatas = new ArrayList<Drawable>();//add--网格文字（分享框）
    private String editHint;//add--编辑框hint文字
    private String editTextValue;//add--编辑框文字
    private String editTextUnit;//add--编辑框单位
    private int editTextInputType = InputType.TYPE_CLASS_TEXT;//add--编辑框类型
    private InputFilter[] filters;//add--编辑过滤器
    //add--获取编辑框内容
    public String getEditTextValue() {
        return editTextValue;
    }

    private WeakReference<Context> contextWeak;
    private ViewGroup contentContainer;
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;//AlertView 的 根View
    private ViewGroup loAlertHeader;//窗口headerView

    private Style style = Style.Alert;

    private OnDismissListener onDismissListener;
    private OnItemClickListener onItemClickListener;
    private boolean isShowing;

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.CENTER;

    private boolean isShowError;//是否显示错误按钮

    public AlertView(Builder builder) {
        this.contextWeak = new WeakReference<>(builder.context);
        this.style = builder.style;
        this.title = builder.title;
        this.msg = builder.msg;
        this.cancel = builder.cancel;
        this.destructive = builder.destructive;
        this.others = builder.others;
        this.editHint = builder.hint;//add--编辑框hint文字
        this.editTextUnit = builder.unit;//add--编辑框单位
        this.editTextValue = builder.value;//add--编辑框单位
        this.filters = builder.filters;//add--编辑框单位
        this.editTextInputType = builder.inputType;//add--编辑框输入类型
        this.mGridDatas = builder.gridDatas;//add--网格文字（分享框）
        this.onItemClickListener = builder.onItemClickListener;
        this.isShowError = builder.isShowError;//add--//是否显示错误按钮

        initData(title, msg, cancel, destructive, others);
        initViews();
        init();
        initEvents();
    }

    /**
     * //add--网格样式（分享框）
     */
    public AlertView(String title, String cancel, Drawable[] drawables, String[] others, Context context, OnItemClickListener onItemClickListener){
        this.contextWeak = new WeakReference<>(context);
        this.style = Style.AlertShare;
        this.onItemClickListener = onItemClickListener;

        initGridData(title, msg, cancel, drawables, others);
        initViews();
        init();
        initEvents();
    }

    public AlertView(String title, String msg, String cancel, String[] destructive, String[] others, boolean isShowError, Context context, Style style,OnItemClickListener onItemClickListener){
        this.contextWeak = new WeakReference<>(context);
        if(style != null)this.style = style;
        this.onItemClickListener = onItemClickListener;
        this.isShowError = isShowError;

        initData(title, msg, cancel, destructive, others);
        initViews();
        init();
        initEvents();
    }

    /**
     * //add--编辑框样式
     */
    public void showEditView() {
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this.contextWeak.get()).inflate(R.layout.alertview_layout_alertview_edit,null);
        final EditText etAlert = (EditText) extView.findViewById(R.id.etAlert);
        final ImageView ivAlertClean = (ImageView) extView.findViewById(R.id.ivAlertClean);
        final TextView tvAlertUnit = (TextView) extView.findViewById(R.id.tvAlertUnit);
        ivAlertClean.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { etAlert.setText(null); }
        });
        etAlert.setHint(editHint);
        etAlert.setInputType(editTextInputType);
        if (TextUtils.isEmpty(editTextUnit)) {
            tvAlertUnit.setVisibility(View.GONE);
        } else {
            tvAlertUnit.setVisibility(View.VISIBLE);
            tvAlertUnit.setText(editTextUnit);
        }
        if (TextUtils.isEmpty(editTextValue)) {
            ivAlertClean.setVisibility(View.GONE);
        } else  {
            ivAlertClean.setVisibility(View.VISIBLE);
            etAlert.setText(editTextValue);
            etAlert.setSelection(editTextValue.length());
        }
        if (filters != null) {
            etAlert.setFilters(filters);
        }
        etAlert.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                editTextValue = etAlert.getText().toString();
                if (TextUtils.isEmpty(editTextValue)) {
                    ivAlertClean.setVisibility(View.GONE);
                } else {
                    ivAlertClean.setVisibility(View.VISIBLE);
                }
            }
        });
        addExtView(extView);
        show();
    }

    /**
     * 获取数据
     */
    protected void initData(String title, String msg, String cancel, String[] destructive, String[] others) {

        this.title = title;
        this.msg = msg;
        if (destructive != null){
            this.mDestructive = Arrays.asList(destructive);
            this.mDatas.addAll(mDestructive);
        }
        if (others != null){
            this.mOthers = Arrays.asList(others);
            this.mDatas.addAll(mOthers);
        }
        if (cancel != null){
            this.cancel = cancel;
            if(style == Style.Alert && mDatas.size() < HORIZONTAL_BUTTONS_MAXCOUNT){
                this.mDatas.add(0,cancel);
            }
        }

    }

    /**
     * 获取数据
     * //add--编辑框样式
     */
    protected void initGridData(String title, String msg, String cancel, Drawable[] drawables, String[] others) {

        this.title = title;
        this.msg = msg;
        if (drawables != null){
            List<Drawable> list = Arrays.asList(drawables);
            this.mGridDatas.addAll(list);
        }
        if (others != null){
            this.mOthers = Arrays.asList(others);
            this.mDatas.addAll(mOthers);
        }
        if (cancel != null){
            this.cancel = cancel;
        }

    }

    protected void initViews(){
        Context context = contextWeak.get();
        if(context == null) return;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = (ViewGroup) ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.alertview_layout_alertview, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        int margin_alert_left_right = 0;
        switch (style){
            case ActionSheet:
                params.gravity = Gravity.BOTTOM;
                margin_alert_left_right = context.getResources().getDimensionPixelSize(R.dimen.alertview_margin_actionsheet_left_right);
                params.setMargins(margin_alert_left_right,0,margin_alert_left_right,margin_alert_left_right);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.BOTTOM;
                initActionSheetViews(layoutInflater);
                break;
            case AlertShare://add--分享样式
                params.gravity = Gravity.BOTTOM;
                params.setMargins(0,0,0,0);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.BOTTOM;
                initShareViews(layoutInflater);
                break;
            case Alert:
                params.gravity = Gravity.CENTER;
                margin_alert_left_right = context.getResources().getDimensionPixelSize(R.dimen.alertview_margin_alert_left_right);
                params.setMargins(margin_alert_left_right,0,margin_alert_left_right,0);
                contentContainer.setLayoutParams(params);
                gravity = Gravity.CENTER;
                initAlertViews(layoutInflater);
                break;
        }
    }
    protected void initHeaderView(ViewGroup viewGroup){
        loAlertHeader = (ViewGroup) viewGroup.findViewById(R.id.loAlertHeader);
        //标题和消息
        TextView tvAlertTitle = (TextView) viewGroup.findViewById(R.id.tvAlertTitle);
        TextView tvAlertMsg = (TextView) viewGroup.findViewById(R.id.tvAlertMsg);
        ImageView ivAlertError = (ImageView) viewGroup.findViewById(R.id.ivAlertError);
        if(title != null) {
            tvAlertTitle.setText(title);
            tvAlertMsg.setTextColor(Color.parseColor("#666666"));//add--
            tvAlertMsg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);//add--
        }else{
            tvAlertTitle.setVisibility(View.GONE);
            tvAlertMsg.setTextColor(Color.parseColor("#333333"));//add--
            tvAlertMsg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);//add--
        }
        if(msg != null) {
            tvAlertMsg.setText(msg);
        }else{
            tvAlertMsg.setVisibility(View.GONE);
        }
        //只有默认样式才有错误图标
        if (ivAlertError != null){
            ivAlertError.setVisibility(this.isShowError ? View.VISIBLE : View.GONE);
            ivAlertError.setOnClickListener(new OnTextClickListener(ERRORPOSITION));
        }
    }
    protected void initListView(){
        Context context = contextWeak.get();
        if(context == null) return;

        ListView alertButtonListView = (ListView) contentContainer.findViewById(R.id.alertButtonListView);
        //把cancel作为footerView
        if(cancel != null && style == Style.Alert){
            View itemView = LayoutInflater.from(context).inflate(R.layout.alertview_item_alertbutton, null);
            TextView tvAlert = (TextView) itemView.findViewById(R.id.tvAlert);
            tvAlert.setText(cancel);
            tvAlert.setClickable(true);
            tvAlert.setTypeface(Typeface.DEFAULT_BOLD);
            tvAlert.setTextColor(context.getResources().getColor(R.color.alertview_textColor_alert_button_cancel));
            tvAlert.setBackgroundResource(R.drawable.alertview_bg_alertbutton_bottom);
            tvAlert.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
            alertButtonListView.addFooterView(itemView);
        }
        AlertViewAdapter adapter = new AlertViewAdapter(mDatas,mDestructive);
        alertButtonListView.setAdapter(adapter);
        alertButtonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(onItemClickListener != null)onItemClickListener.onItemClick(AlertView.this,position);
//                dismiss();
            }
        });
    }

    /**
     * 初始化数据
     * //add--网格样式
     */
    protected void initGridListView(){
        Context context = contextWeak.get();
        if(context == null) return;

        GridView alertButtonListView = (GridView) contentContainer.findViewById(R.id.gvAlert);
        int numColumns = mGridDatas.size();
        if (numColumns < 4) alertButtonListView.setNumColumns(3); // 小于四个时候先生Grid为3
        else alertButtonListView.setNumColumns(4);
        AlertGridViewAdapter adapter = new AlertGridViewAdapter(mGridDatas, mOthers);
        alertButtonListView.setAdapter(adapter);
        alertButtonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(onItemClickListener != null)onItemClickListener.onItemClick(AlertView.this,position);
//                dismiss();
            }
        });
    }

    protected void initActionSheetViews(LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.alertview_layout_alertview_actionsheet,contentContainer);
        initHeaderView(viewGroup);

        initListView();
        TextView tvAlertCancel = (TextView) contentContainer.findViewById(R.id.tvAlertCancel);
        if(cancel != null){
            tvAlertCancel.setVisibility(View.VISIBLE);
            tvAlertCancel.setText(cancel);
        }
        tvAlertCancel.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
    }

    /**
     * 初始化数据
     * //add--网格样式
     */
    protected void initShareViews(LayoutInflater layoutInflater) {
        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.alertview_layout_alertview_share,contentContainer);
        initHeaderView(viewGroup);

        initGridListView();
        TextView tvAlertCancel = (TextView) contentContainer.findViewById(R.id.tvAlertCancel);
        if(cancel != null){
            tvAlertCancel.setVisibility(View.VISIBLE);
            tvAlertCancel.setText(cancel);
        }
        tvAlertCancel.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
    }

    /**
     * 初始化默认弹框样式
     */
    protected void initAlertViews(LayoutInflater layoutInflater) {
        Context context = contextWeak.get();
        if(context == null) return;

        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.alertview_layout_alertview_alert, contentContainer);
        initHeaderView(viewGroup);

        int position = 0;
        //如果总数据小于等于HORIZONTAL_BUTTONS_MAXCOUNT，则是横向button
        if(mDatas.size()<=HORIZONTAL_BUTTONS_MAXCOUNT){
            ViewStub viewStub = (ViewStub) contentContainer.findViewById(R.id.viewStubHorizontal);
            viewStub.inflate();
            LinearLayout loAlertButtons = (LinearLayout) contentContainer.findViewById(R.id.loAlertButtons);
            for (int i = 0; i < mDatas.size(); i ++) {
                //如果不是第一个按钮
                if (i != 0){
                    //添加上按钮之间的分割线
                    View divier = new View(context);
                    divier.setBackgroundColor(context.getResources().getColor(R.color.alertview_bgColor_divier));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.alertview_size_divier), LinearLayout.LayoutParams.MATCH_PARENT);
                    loAlertButtons.addView(divier,params);
                }
                View itemView = LayoutInflater.from(context).inflate(R.layout.alertview_item_alertbutton, null);
                TextView tvAlert = (TextView) itemView.findViewById(R.id.tvAlert);
                tvAlert.setClickable(true);

                //设置点击效果
                if(mDatas.size() == 1){
                    tvAlert.setBackgroundResource(R.drawable.alertview_bg_alertbutton_bottom);
                }
                else if(i == 0){//设置最左边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.alertview_bg_alertbutton_left);
                }
                else if(i == mDatas.size() - 1){//设置最右边的按钮效果
                    tvAlert.setBackgroundResource(R.drawable.alertview_bg_alertbutton_right);
                }
                String data = mDatas.get(i);
                tvAlert.setText(data);

                //取消按钮的样式
                if (data == cancel){
                    tvAlert.setTextColor(Color.parseColor("#666666"));//modify--
                    tvAlert.setOnClickListener(new OnTextClickListener(CANCELPOSITION));
                    position = position - 1;
                }
                //高亮按钮的样式
                else if (mDestructive!= null && mDestructive.contains(data)){
                    tvAlert.setTextColor(Color.parseColor("#0085D0"));//modify--
                }

                tvAlert.setOnClickListener(new OnTextClickListener(position));
                position++;
                loAlertButtons.addView(itemView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            }
        }
        else{
            ViewStub viewStub = (ViewStub) contentContainer.findViewById(R.id.viewStubVertical);
            viewStub.inflate();
            initListView();
        }
    }
    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }
    protected void initEvents() {
    }
    public AlertView addExtView(View extView){
        loAlertHeader.addView(extView);
        return this;
    }
    /**
     * show的时候调用
     *
     * @param view 这个View
     */
    private void onAttached(View view) {
        isShowing = true;
        decorView.addView(view);
        contentContainer.startAnimation(inAnim);
    }
    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }
    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return rootView.getParent() != null && isShowing;
    }

    public void dismiss() {
        //消失动画
        isShowing = false;
        outAnim.setAnimationListener(outAnimListener);
        contentContainer.startAnimation(outAnim);
    }

    public void dismissImmediately() {
        decorView.removeView(rootView);
        isShowing = false;
        if(onDismissListener != null){
            onDismissListener.onDismiss(this);
        }

    }

    public Animation getInAnimation() {
        Context context = contextWeak.get();
        if(context == null) return null;

        int res = AlertAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        Context context = contextWeak.get();
        if(context == null) return null;

        int res = AlertAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public AlertView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    class OnTextClickListener implements View.OnClickListener{

        private int position;
        public OnTextClickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if(onItemClickListener != null)onItemClickListener.onItemClick(AlertView.this,position);
//            dismiss();
        }
    }
    private Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissImmediately();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 主要用于拓展View的时候有输入框，键盘弹出则设置MarginBottom往上顶，避免输入法挡住界面
     */
    public void setMarginBottom(int marginBottom){
        Context context = contextWeak.get();
        if(context == null) return;

        int margin_alert_left_right = context.getResources().getDimensionPixelSize(R.dimen.alertview_margin_alert_left_right);
        params.setMargins(margin_alert_left_right,0,margin_alert_left_right,marginBottom);
        contentContainer.setLayoutParams(params);
    }
    public AlertView setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(R.id.outmost_container);

        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        }
        else{
            view.setOnTouchListener(null);
        }
        return this;
    }
    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    /**
     * Builder for arguments
     */
    public static class Builder {
        private Context context;
        private Style style;
        private String title;
        private String msg;
        private String cancel;
        private String[] destructive;
        private String[] others;
        private ArrayList<Drawable> gridDatas = new ArrayList<Drawable>();//add--
        private String hint;//add--
        private String unit;//add--
        private String value;//add--
        private InputFilter[] filters;//add--编辑过滤器
        private int inputType;//add--
        private OnItemClickListener onItemClickListener;
        private boolean isShowError;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setStyle(Style style) {
            if(style != null) {
                this.style = style;
            }
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setCancelText(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public Builder setDestructive(String... destructive) {
            this.destructive = destructive;
            return this;
        }

        public Builder setOthers(String[] others) {
            this.others = others;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }
        //add--
        public Builder setGridDatas(ArrayList<Drawable> gridDatas) {
            this.gridDatas = gridDatas;
            return this;
        }
        //add--
        public Builder setHint(String hint) {
            this.hint = hint;
            return this;
        }
        //add--
        public Builder setUnit(String unit) {
            this.unit = unit;
            return this;
        }
        //add--
        public Builder setInputType(int inputType) {
            this.inputType = inputType;
            return this;
        }
        //add--
        public Builder setValue(String value) {
            this.value = value;
            return this;
        }
        //add--
        public Builder setFilters(InputFilter[] filters) {
            this.filters = filters;
            return this;
        }
        //add--
        public Builder setShowError(boolean showError) {
            isShowError = showError;
            return this;
        }

        public AlertView build() {
            return new AlertView(this);
        }
    }
}
