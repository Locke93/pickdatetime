package com.github.lcokean.library;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
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
 * 时间选择对话框
 * 
 * @author pengjian
 *
 */
public class TimePickerDialog extends BaseDialog implements
		View.OnClickListener {

	private int MIN_YEAR = 2000;
	private int MAX_YEAR = 2020;

	public static final int DIALOG_MODE_CENTER = 0;
	public static final int DIALOG_MODE_BOTTOM = 1;

	private Context context;
	private WheelView wvDate;
	private WheelView wvHour;
	private WheelView wvMinute;

	private View vDialog;
	private View vDialogChild;
	private ViewGroup VDialogPicker;
	private TextView tvTitle;
	private TextView btnSure;
	private TextView btnCancel;

	private ArrayList<String> arry_dates = new ArrayList<String>();
	private ArrayList<String> arry_hours = new ArrayList<String>();
	private ArrayList<String> arry_minutes = new ArrayList<String>();
	private CalendarTextAdapter mDateAdapter;
	private CalendarTextAdapter mHourAdapter;
	private CalendarTextAdapter mMinuteAdapter;

	private int hour;
	private int minute;

	private int currentDate = 0;
	private int currentHour = 0;
	private int currentMinute = 0;

	private int maxTextSize = 24;
	private int minTextSize = 14;

	private boolean issetdata = false;

	private int selectDate;
	private String selectHour;
	private String selectMinute;

	private String strTitle = "选择时间";

	private OnTimePickListener onTimePickListener;

	public TimePickerDialog(Context context) {
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

		wvDate = new WheelView(context);
		wvDate.setLayoutParams(lp);
		VDialogPicker.addView(wvDate);

		wvHour = new WheelView(context);
		wvHour.setLayoutParams(lp);
		wvHour.setCyclic(true);
		VDialogPicker.addView(wvHour);

		wvMinute = new WheelView(context);
		wvMinute.setLayoutParams(lp);
		wvMinute.setCyclic(true);
		VDialogPicker.addView(wvMinute);

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
			initTime();
		}
		initDates();
		mDateAdapter = new CalendarTextAdapter(context, arry_dates,
				currentDate, maxTextSize, minTextSize);
		wvDate.setVisibleItems(5);
		wvDate.setViewAdapter(mDateAdapter);
		wvDate.setCurrentItem(currentDate);

		initHours(hour);
		mHourAdapter = new CalendarTextAdapter(context, arry_hours,
				currentHour, maxTextSize, minTextSize);
		wvHour.setVisibleItems(5);
		wvHour.setViewAdapter(mHourAdapter);
		wvHour.setCurrentItem(currentHour);

		initMinutes(minute);
		mMinuteAdapter = new CalendarTextAdapter(context, arry_minutes,
				currentMinute, maxTextSize, minTextSize);
		wvMinute.setVisibleItems(5);
		wvMinute.setViewAdapter(mMinuteAdapter);
		wvMinute.setCurrentItem(currentMinute);

		wvDate.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mDateAdapter.getItemText(wheel
						.getCurrentItem());
				selectDate = wheel.getCurrentItem();
				setTextviewSize(currentText, mDateAdapter);
				//动态添加
				if(oldValue>newValue){
					if(newValue<100){
						//需要添加前面一年的数据
						MIN_YEAR -= 1;
						arry_dates.addAll(0, getYaerDate(MIN_YEAR));
						selectDate += calDaysOfYear(MIN_YEAR);
						mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate,
								maxTextSize, minTextSize);
						wvDate.setVisibleItems(5);
						wvDate.setViewAdapter(mDateAdapter);
						wvDate.setCurrentItem(selectDate);
					}
				}else{
					if(mDateAdapter.getItemsCount()-newValue<100){
						//需要添加后面一年的数据
						MAX_YEAR += 1;
						arry_dates.addAll(mDateAdapter.getItemsCount(), getYaerDate(MAX_YEAR));
						mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate,
								maxTextSize, minTextSize);
						wvDate.setVisibleItems(5);
						wvDate.setViewAdapter(mDateAdapter);
						wvDate.setCurrentItem(selectDate);
					}
				}
			}
		});

		wvDate.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mDateAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mDateAdapter);
			}
		});

		wvHour.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mHourAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mHourAdapter);
				selectHour = currentText;
			}
		});

		wvHour.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mHourAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mHourAdapter);
			}
		});

		wvMinute.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) mMinuteAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mMinuteAdapter);
				selectMinute = currentText;
			}
		});

		wvMinute.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) mMinuteAdapter.getItemText(wheel
						.getCurrentItem());
				setTextviewSize(currentText, mMinuteAdapter);
			}
		});

	}

	@SuppressLint("SimpleDateFormat")
	private String getTimeFormat(String format, Date date) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(date);
	}

	@SuppressLint("SimpleDateFormat")
	private Date getDateFromString(String timeStr, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取某一年的String数据
	 * @param year
	 * @return
	 */
	private ArrayList<String> getYaerDate(int year) {
		ArrayList<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, 0, 1);
		while (calendar.get(Calendar.YEAR) <= year) {
			final Date date = calendar.getTime();
			list.add(getTimeFormat("MM月dd日 EEE", date));
			calendar.add(Calendar.DATE, 1);
		}
		return list;
	}

	//设置为当前时间
	public void initTime() {
		Calendar c = Calendar.getInstance();
		setTime(c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH), 
				getCurrHour(), getCurrMinute());
	}

	public void initDates() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(MIN_YEAR, 0, 1);
		while (calendar.get(Calendar.YEAR) <= MAX_YEAR) {
			final Date date = calendar.getTime();
			arry_dates.add(getTimeFormat("MM月dd日 EEE", date));
			calendar.add(Calendar.DATE, 1);
		}
	}

	public void initHours(int hours) {
		arry_hours.clear();
		for (int i = 0; i < hours; i++) {
			if (i < 10) {
				arry_hours.add("0" + i);
			} else {
				arry_hours.add(i + "");
			}
		}
	}

	public void initMinutes(int minutes) {
		arry_minutes.clear();
		for (int i = 0; i < minutes; i++) {
			if (i < 10) {
				arry_minutes.add("0" + i);
			} else {
				arry_minutes.add(i + "");
			}
		}
	}

	@Override
	public void onClick(View v) {

		if (v == btnSure) {
			if (onTimePickListener != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(MIN_YEAR, 0, 1);
				calendar.add(Calendar.DATE, selectDate);
				onTimePickListener.onClick(
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1,
						calendar.get(Calendar.DAY_OF_MONTH),
						selectHour, selectMinute);
			}
		} else if (v == btnCancel) {

		} else if (v == vDialogChild) {
			return;
		} else {
			dismiss();
		}
		dismiss();

	}

	public interface OnTimePickListener {
		void onClick(int year, int month, int day, String hour, String minute);
	}

	public void setTimePickListener(OnTimePickListener onTimePickListener) {
		this.onTimePickListener = onTimePickListener;
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

	public int getCurrHour() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public int getCurrMinute() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.MINUTE);
	}

	/**
	 * 设置日期-时间
	 * 
	 * @param year
	 * @param month 1-12
	 * @param day 1-31
	 */
	public void setTime(int year, int month, int day, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, day);
		this.currentDate = calendar.get(Calendar.DAY_OF_YEAR) - 1;
		if(month<6){
			MIN_YEAR = year -1;
			MAX_YEAR = year;
			this.currentDate += calDaysOfYear(MIN_YEAR);
		}else{
			MIN_YEAR = year;
			MAX_YEAR = year + 1;
		}
		selectDate = currentDate;
		selectHour = hour + "";
		selectMinute = minute + "";
		issetdata = true;
		this.currentHour = hour;
		this.currentMinute = minute;
		this.hour = 24;
		this.minute = 60;
	}

	/**
	 * 设置日期
	 * 
	 * @param date
	 */
	public int setDate(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.set(MIN_YEAR, 1, 1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days)) + 1;

	}
	
	/**
	 * 计算每年多少天
	 * @return
	 */
	public int calDaysOfYear(int year){
		if (year % 4 == 0 && year % 100 != 0) {
			return 366;
		} else {
			return 365;
		}
	}

	/**
	 * 计算每月多少天
	 * 
	 * @param month
	 * @param leayyear
	 */
	public int calDaysOfMonth(int year, int month) {
		int day = 0;
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
				day = 31;
				break;
			case 2:
				if (leayyear) {
					day = 29;
				} else {
					day = 28;
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = 30;
				break;
			}
		}
		return day;
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

		@Override
		protected void notifyDataChangedEvent() {
			// TODO Auto-generated method stub
			super.notifyDataChangedEvent();
		}
		
		
	}
}