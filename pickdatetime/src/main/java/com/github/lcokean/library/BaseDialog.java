package com.github.lcokean.library;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * author：魔鬼
 * E-mail：iwhs@qq.com
 * createTime：2013年11月25日 上午9:25:20
 */

public abstract class BaseDialog extends Dialog {

    protected Context mContext;
    protected View mView;
    private OnForceDismissListener onForceDismissListener;
    private Window mWindow;

    /**
     * @param context    上下文
     * @param resourceId 布局资源文件id
     */
    public BaseDialog(Context context, int resourceId) {
        super(context, R.style.Dialog);
        mContext = context;
        mView = View.inflate(mContext, resourceId, null);
        mWindow = getWindow();
        init();
    }

    public void resetContent(int resourceId) {
        mView = View.inflate(mContext, resourceId, null);
        setContentView(mView);
    }

    public interface OnForceDismissListener {
        void onForceDismiss();
    }

    public void setOnForceDismissListener(OnForceDismissListener onForceDismissListener) {
        this.onForceDismissListener = onForceDismissListener;
    }

    @Override
    public void show() {
        setDialogSize();
        //显示之前刷新view，防止view变形
        mView.postInvalidate();
        super.show();
    }

    /**
     * 是否允许取消
     *
     * @return
     */
    protected boolean dialogCancelable() {
        return true;
    }

    /**
     * 是否允许点击dialog范围之外取消
     *
     * @return
     */
    protected boolean dialogCanceledOnTouchOutside() {
        return true;
    }


    protected void setAnimation(int resId) {
        mWindow.setWindowAnimations(resId);
    }


    protected void setGravity(int gravity) {
        mWindow.setGravity(gravity);
    }

    /**
     * 设置窗口布局宽度，默认适应填充数据之后的View
     *
     * @return
     */
    protected int dialogWidth() {
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return mView.getMeasuredWidth();
    }

    /**
     * 设置dialog高度
     */
    protected int dialogHeight() {
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return mView.getMeasuredHeight();
    }

    //初始化设置
    private void init() {
        setContentView(mView);
        setCanceledOnTouchOutside(dialogCanceledOnTouchOutside());
        setCancelable(dialogCancelable());
        setAnimation(android.R.style.Animation_Dialog);
        setGravity(Gravity.CENTER);
    }

    //设置对话框宽、高属性
    private void setDialogSize() {
        WindowManager.LayoutParams wlp = mWindow.getAttributes();
        wlp.width = dialogWidth();
        wlp.height = dialogHeight();
        mWindow.setAttributes(wlp);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            //event.getAction()!=KeyEvent.ACTION_UP 不响应抬起动作，可防止执行两次
            if (onForceDismissListener != null) {
                onForceDismissListener.onForceDismiss();
            }
            this.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
