package com.ikras.lazylock;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class LightUpService extends Service
{
	protected static final String TAG = "LightUpService";
	private WakeLock wl = null;
	private PowerManager pm;
	private SensorManager sm;
	private Sensor mProximity;
	private SensorEventListener proximityListener;
	protected boolean isFirst = true;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		if (null == wl)
		{// ��ȡ��Դ��
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.getClass().getCanonicalName());
			if (null != wl)
			{
				wl.acquire();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mProximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		proximityListener = new SensorEventListener()
		{
			private DevicePolicyManager policyManager;
			private ComponentName componentName;

			@Override
			public void onSensorChanged(SensorEvent event)
			{
				Log.e(TAG, "event.values[0]=>" + event.values[0]);
				Log.e(TAG, "MaximumRange=>" + mProximity.getMaximumRange());
				if (isFirst)
				{// �ж��Ƿ��ǵ�һ�β�ֵ����Ϊ��һ�β����ֵ�Ǵ����������ֵ��������ʵ��ֵ
					isFirst = false;
					return;
				} else if (event.values[0] != mProximity.getMaximumRange())
				{
					policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
					componentName = new ComponentName(LightUpService.this, LockReceiver.class);
					if (policyManager.isAdminActive(componentName))
					{// �ж��Ƿ���Ȩ��(�������豸������)
						policyManager.lockNow();// ֱ������
					}
				} else if (event.values[0] == mProximity.getMaximumRange())
				{
					wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "");
					wl.acquire();// ����
					wl.release();
					wl = null;
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{

			}
		};
		sm.registerListener(proximityListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);// ע�ᴫ����
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (null != sm)
		{
			sm.unregisterListener(proximityListener, mProximity);// �������������
		}
		if (null != wl)
		{// �ͷŵ�Դ��
			wl.release();
			wl = null;
		}
	}
}
