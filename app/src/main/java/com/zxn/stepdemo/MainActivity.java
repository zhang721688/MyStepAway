package com.zxn.stepdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zcommon.lib.ZToastUtils;
import com.ztime.lib.ZTimeUtils;
import com.zxn.steplib.ISportStepInterface;
import com.zxn.steplib.TodayStepManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by zxn on 2019-1-28 10:08:19.
 */
public class MainActivity extends AppCompatActivity implements ServiceConnection {

    @BindView(R.id.btn_all_step)
    Button btnAllStep;
    @BindView(R.id.step_from_time)
    Button stepFromTime;
    @BindView(R.id.step_from_time_vs_day)
    Button stepFromTimeVsDay;
    @BindView(R.id.tv_step_text)
    TextView tvStepText;

    @BindView(R.id.stepArrayTextView)
    TextView mStepArrayTextView;
    @BindView(R.id.btn_calorie)
    Button btnCalorie;
    @BindView(R.id.btn_km)
    Button btnKm;
    @BindView(R.id.btn_time)
    Button btnTime;
    @BindView(R.id.btn_seven)
    Button btnSeven;

    private ISportStepInterface iSportStepInterface;
    private int mStepSum;
    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //todo:将服务器步数同步到本地.

        //todo:绑定服务开始计算步数.
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    bind();
                }else {
                    ZToastUtils.showToast(MainActivity.this,"获取权限失败了!");
                }
            }
        });
    }

    private void bind() {
        TodayStepManager.onPermissionsInit(this);
        TodayStepManager.init(this);

        //开启计步Service，同时绑定Activity进行aidl通信
        //Intent intent = new Intent(this, TodayStepService.class);
        //intent.putExtra(TodayStepService.INTENT_NAME_SERVER_STEP, 3000);
        //startService(intent);

        TodayStepManager.bindService(this, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                try {
                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                    updateStepCount();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        });


        //TodayStepManager.startTodayStepService(this);

//        Intent intent = new Intent(this, TodayStepService.class);
//        startService(intent);
//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                //Activity和Service通过aidl进行通信
//                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
//                try {
//                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
//                    updateStepCount();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        }, Context.BIND_AUTO_CREATE);
    }


    @OnClick({R.id.btn_all_step, R.id.step_from_time, R.id.step_from_time_vs_day, R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                bind();
                break;
            case R.id.btn_stop:
                Log.i(TAG, "onViewClicked: btn_stop");
                TodayStepManager.stopTodayStepService(getApplication());
                break;
            case R.id.btn_all_step:
                //获取所有步数列表
                if (null != iSportStepInterface) {
                    try {
                        String stepArray = iSportStepInterface.getTodaySportStepArray();
                        mStepArrayTextView.setText(stepArray);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.step_from_time:
                //根据时间来获取步数列表
                if (null != iSportStepInterface) {
                    try {
                        String stepArray = iSportStepInterface.getTodaySportStepArrayByDate("2019-02-18");
                        mStepArrayTextView.setText(stepArray);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.step_from_time_vs_day:
                //获取多天步数列表
                if (null != iSportStepInterface) {
                    try {
                        String stepArray = iSportStepInterface.getTodaySportStepArrayByStartDateAndDays("2019-02-18", 6);
                        mStepArrayTextView.setText(stepArray);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void updateStepCount() {
        Log.e(TAG, "updateStepCount : " + mStepSum);
        //TextView stepTextView = (TextView) findViewById(R.id.stepTextView);
        tvStepText.setText(mStepSum + "步");

    }

    private static String TAG = "MainActivity";
    private static final int REFRESH_STEP_WHAT = 0;
    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    @OnClick({R.id.btn_calorie, R.id.btn_km, R.id.btn_time, R.id.btn_today, R.id.btn_seven})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_seven:
                if (null != iSportStepInterface) {
                    try {
                        String data = ZTimeUtils.getCurrentYearMonthDayTime();
                        String text = iSportStepInterface.getTodaySportStepArrayByEndDateAndDays(data, 7);
                        mStepArrayTextView.setText(text);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_today:
                if (null != iSportStepInterface) {
                    try {
                        String data = ZTimeUtils.getCurrentYearMonthDayTime();
                        String text = iSportStepInterface.getTodaySportStepArrayByDate(data);
                        mStepArrayTextView.setText(text);
                        Log.i(TAG, "onClick: ---->" + text);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_calorie:
                if (null != iSportStepInterface) {
                    try {
                        String calorie = iSportStepInterface.getCurrentCalorie();
                        btnCalorie.setText("卡路里:" + calorie);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_km:
                if (null != iSportStepInterface) {
                    try {
                        String distance = iSportStepInterface.getCurrentDistance();
                        btnKm.setText("km:" + distance);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_time:
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //Activity和Service通过aidl进行通信
        iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
        try {
            mStepSum = iSportStepInterface.getCurrentTimeSportStep();
            updateStepCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
                            updateStepCount();
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
                    break;
                }
            }
            return false;
        }
    }

}
