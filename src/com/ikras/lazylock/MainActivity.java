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
		setContentView(R.layout.activity_main);// �����ʼ��
		// 1����ȡ�豸����������
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		// 2��ע��㲥������ΪAdmin�豸
		componentName = new ComponentName(this, LockReceiver.class);
		if (!policyManager.isAdminActive(componentName))
		{// 3�������豸�����������Ȩ��
			activeManager();
		} else
		{
			finish();// �ر�Activity
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
				android.os.Process.killProcess(android.os.Process.myPid());// ɱ������
				System.exit(0);// �˳���������
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
		startService(new Intent(this, LightUpService.class));// ��������
		policyManager.lockNow();// ������Ļ
	}

	/** �����豸������ */
	private void activeManager()
	{
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "��������");
		startActivityForResult(intent, 0);
	}
}
