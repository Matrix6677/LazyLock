package com.ikras.lazylock;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*¼ì²â½âËø×´Ì¬*/
public class LockReceiver extends DeviceAdminReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		Log.e("LockReceiver", action);
		if ("android.intent.action.USER_PRESENT".equals(action))
		{
			context.stopService(new Intent(context, LightUpService.class));
		}
	}
}