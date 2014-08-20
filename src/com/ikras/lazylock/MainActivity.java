package com.ikras.lazylock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity
{
	private DevicePolicyManager policyManager;
	private ComponentName componentName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);// 界面初始化
		// 1、获取设备管理器对象
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		// 2、注册广播接收器为Admin设备
		componentName = new ComponentName(this, LockReceiver.class);
		if (!policyManager.isAdminActive(componentName))
		{// 3、激活设备管理器并获得权限
			activeManager();
		} else
		{
			finish();// 关闭Activity
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case 0:
			if (resultCode == RESULT_OK)
			{
				finish();
			} else
			{
				android.os.Process.killProcess(android.os.Process.myPid());// 杀死进程
				System.exit(0);// 退出整个程序
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		startService(new Intent(this, LightUpService.class));// 启动服务
		policyManager.lockNow();// 锁定屏幕
	}

	/** 激活设备管理器 */
	private void activeManager()
	{
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "懒人锁屏");
		startActivityForResult(intent, 0);
	}
}
