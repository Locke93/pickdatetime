package com.github.lcokean.library;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.wheelview.adapters.AbstractWheelTextAdapter;
import com.github.wheelview.views.OnWheelChangedListener;
import com.github.wheelview.views.OnWheelScrollListener;
import com.github.wheelview.views.WheelView;

/**
 * 日期选择对话框
 * 
 * @author pengjian
 *
 */
public class DatePickerDialog extends BaseDialog implements
		View.OnClickListener {

	private static final int MIN_YEAR = 1970;
	private static final int MAX_YEAR = 2100;

	public static final int DIALOG_MODE_CENTER = 0;
	public static final int DIALOG_MODE_BOTTOM = 1;

	private Context context;
	private WheelView wvYear;
	private WheelView wvMonth;
	private WheelView wvDay;

	private View vDialog;
	private View vDialogChild;
	private ViewGroup VDialogPicker;
	private TextView tvTitle;
	private TextView btnSure;
	private TextView btnCancel;

	private ArrayList<String> arry_years = new ArrayList<String>();
	private ArrayList<String> arry_months = new ArrayList<String>();
	private ArrayList<String> arry_days = new ArrayList<String>();
	private CalendarTextAdapter mYearAdapter;
	private CalendarTextAdapter mMonthAdapter;
	private CalendarTextAdapter mDaydapter;

	private int month;
	private int day;

	private int currentYear = getYear();
	private int currentMonth = getMonth();
	private int currentDay = getDay();

	private int maxTextSize = 24;
	private int minTextSize = 18;

	private boolean issetdata = false;

	private String selectYear;
	private String selectMonth;
	private String selectDay;

	private String strTitle = "选择日期";

	private OnDatePickListener onDatePickListener;

	public DatePickerDialog(Context context) {
		super(context, R.layout.dialog_picker_center);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		VDialogPicker = (ViewGroup) findViewById(R.id.ly_dialog_picker);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
		// 此处相当于布局文件中的Android:layout_gravity属性
		lp.gravity = Gravity.CENTER_VERTICAL;

		wvYear = new WheelView(context);
		wvYear.setLayoutParams(lp);
		VDialogPicker.addView(wvYear);

		wvMonth = new WheelView(context);
		wvMonth.setLayoutParams(lp);
		VDialogPicker.addView(wvMonth);

		wvDay = new WheelView(context);
		wvDay.setLayoutParams(lp);
		VDialogPicker.addView(wvDay);

		vDialog = findViewById(R.id.ly_dialog);
		vDialogChild = findViewById(R.id.ly_dialog_child);
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		btnSure = (TextView) findViewById(R.id.btn_dialog_sure);
		btnCancel = (TextView) findViewById(R.id.btn_dialog_cancel);

		tvTitle.setText(strTitle);
		vDialog.setOnClickListener(this);
		vDialogChild.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		if (null != btnCancel) {
			btnCancel.setOnClickListener(this);
		}

		if (!issetdata) {
			initData();
		}
		initYears();
		mYearAdapter = new CalendarTextAdapter(context, arry_years,
				setYear(currentYear), maxTextSize, minTextSize);
		wvYear.setVisibleItems(5);
		wvYear.setViewAdapter(mYearAdapter);
		wvYear.setCurrentItem(setYear(currentYear));

		initMonths(month);
		mMonthAdapter = new CalendarTextAdapter(context, arry_months,
				setMonth(currentMonth), maxTextSize, minTextSize);
		wvMonth.setVisibleItems(5);
		wvMonth.setCyclic(true);
		wvMonth.setViewAdapter(mMonthAdapter);
		wvMonth.setCurrentItem(setMonth(currentMonth));

		initDays(day);
		mDaydapter = new CalendarTextAdapter(context, arry_days,
				currentDay - 1, maxTextSize, minTextSize);
		wvDay.setVisibleItems(5);
		wvDay.setCyclic(true);
		wvDay.setViewAdapter(mDaydapter);
		wvDay.setCurrentItem(currentDay - 1);

		wvYear.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mYearAdapter.getItemText(wheel
						.getCurrentItem());
				selectYear = currentText;
				setTextviewSize(currentText, mYearAdapter);
				currentYear = Integer.parseInt(currentText);
				setYear(currentYear);
				initMonths(month);
				mMonthAdapter = new CalendarTextAdapter(context, arry_months,
						0, maxTextSize, minTextSize);
				wvMonth.setVisibleItems(5);
				wvMonth.setViewAdapter(mMonthAdapter);
				wvMonth.setCurrentItem(0);
			}
		});

		wvYear.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mYearAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mYearAdapter);
			}
		});

		wvMonth.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mMonthAdapter.getItemText(wheel
						.getCurrentItem());
				selectMonth = currentText;
				setTextviewSize(currentText, mMonthAdapter);
				setMonth(Integer.parseInt(currentText));
				initDays(day);
				mDaydapter = new CalendarTextAdapter(context, arry_days, 0,
						maxTextSize, minTextSize);
				wvDay.setVisibleItems(5);
				wvDay.setViewAdapter(mDaydapter);
				wvDay.setCurrentItem(0);
			}
		});

		wvMonth.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mMonthAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mMonthAdapter);
			}
		});

		wvDay.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mDaydapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mDaydapter);
				selectDay = currentText;
			}
		});

		wvDay.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mDaydapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mDaydapter);
			}
		});

	}

	public void initData() {
		setDate(getYear(), getMonth(), getDay());
	}

	public void initYears() {
		for (int i = MIN_YEAR; i < MAX_YEAR; i++) {
			arry_years.add(i + "");
		}
	}

	public void initMonths(int months) {
		arry_months.clear();
		for (int i = 1; i <= months; i++) {
			arry_months.add(i + "");
		}
	}

	public void initDays(int days) {
		arry_days.clear();
		for (int i = 1; i <= days; i++) {
			arry_days.add(i + "");
		}
	}

	/**
	 * 设置dialog弹出框模式
	 * 
	 * @param dialogMode
	 * @param DIALOG_MODE_CENTER
	 *            从屏幕中间弹出
	 * @param DIALOG_MODE_BOTTOM
	 *            从屏幕底部弹出
	 */
	public void setDialogMode(int dialogMode) {
		if (dialogMode == DIALOG_MODE_BOTTOM) {
			resetContent(R.layout.dialog_picker_bottom);
			setAnimation(R.style.AnimationBottomDialog);
			setGravity(Gravity.BOTTOM);
		}
	}

	public void setTitle(String title) {
		this.strTitle = title;
	}

	@Override
	protected int dialogWidth() {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		return metric.widthPixels;
	}

	public void setDatePickListener(OnDatePickListener onDatePickListener) {
		this.onDatePickListener = onDatePickListener;
	}

	@Override
	public void onClick(View v) {

		if (v == btnSure) {
			if (onDatePickListener != null) {
				onDatePickListener.onClick(selectYear, selectMonth, selectDay);
			}
		} else if (v == btnCancel) {

		} else if (v == vDialogChild) {
			return;
		} else {
			dismiss();
		}
		dismiss();
	}

	public interface OnDatePickListener {
		void onClick(String year, String month, String day);
	}

	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText,
			CalendarTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(maxTextSize);
			} else {
				textvew.setTextSize(minTextSize);
			}
		}
	}

	public int getYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}

	public int getMonth() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MONTH) + 1;
	}

	public int getDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DATE);
	}

	/**
	 * 设置年月日
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setDate(int year, int month, int day) {
		selectYear = year + "";
		selectMonth = month + "";
		selectDay = day + "";
		issetdata = true;
		this.currentYear = year;
		this.currentMonth = month;
		this.currentDay = day;
		this.month = 12;
		calDays(year, month);
	}

	/**
	 * 设置年份
	 * 
	 * @param year
	 */
	private int setYear(int year) {
		int yearIndex = 0;
		for (int i = MIN_YEAR; i < MAX_YEAR; i++) {
			if (i == year) {
				return yearIndex;
			}
			yearIndex++;
		}
		return yearIndex;
	}

	/**
	 * 设置月份
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int setMonth(int month) {
		int monthIndex = 0;
		calDays(currentYear, month);
		for (int i = 1; i < this.month; i++) {
			if (month == i) {
				return monthIndex;
			} else {
				monthIndex++;
			}
		}
		return monthIndex;
	}

	/**
	 * 计算每月多少天
	 * 
	 * @param month
	 * @param leayyear
	 */
	public void calDays(int year, int month) {
		boolean leayyear = false;
		leayyear = year % 4 == 0 && year % 100 != 0;
		for (int i = 1; i <= 12; i++) {
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				this.day = 31;
				break;
			case 2:
				if (leayyear) {
					this.day = 29;
				} else {
					this.day = 28;
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				this.day = 30;
				break;
			}
		}
		if (year == getYear() && month == getMonth()) {
			this.day = getDay();
		}
	}

	private class CalendarTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected CalendarTextAdapter(Context context, ArrayList<String> list,
				int currentItem, int maxsize, int minsize) {
			super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem,
					maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}
	}

}