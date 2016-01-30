package com.appodeal.test;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.appodeal.test", "com.appodeal.test.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.appodeal.test", "com.appodeal.test.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.appodeal.test.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public com.appodeal.basic4android.AppodealB4A _appodeal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _init = null;
public anywheresoftware.b4a.objects.ButtonWrapper _showinterstitial = null;
public anywheresoftware.b4a.objects.ButtonWrapper _showvideo = null;
public anywheresoftware.b4a.objects.ButtonWrapper _rewardedvideo = null;
public anywheresoftware.b4a.objects.ButtonWrapper _showvideoorinterstitial = null;
public anywheresoftware.b4a.objects.ButtonWrapper _showbanner = null;
public anywheresoftware.b4a.objects.ButtonWrapper _hidebanner = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checklogging = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checktesting = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkconfirm = null;
public static String _appkey = "";

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 48;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 49;BA.debugLine="Activity.Color = Colors.Red";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 51;BA.debugLine="checkLogging.Text = \"Log\"";
mostCurrent._checklogging.setText((Object)("Log"));
 //BA.debugLineNum = 52;BA.debugLine="checkLogging.TextColor = Colors.White";
mostCurrent._checklogging.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 53;BA.debugLine="checkLogging.Color = Colors.Red";
mostCurrent._checklogging.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 55;BA.debugLine="checkTesting.Text = \"Testing\"";
mostCurrent._checktesting.setText((Object)("Testing"));
 //BA.debugLineNum = 56;BA.debugLine="checkTesting.TextColor = Colors.White";
mostCurrent._checktesting.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 57;BA.debugLine="checkTesting.Color = Colors.Red";
mostCurrent._checktesting.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 59;BA.debugLine="checkConfirm.Text = \"Confirm\"";
mostCurrent._checkconfirm.setText((Object)("Confirm"));
 //BA.debugLineNum = 60;BA.debugLine="checkConfirm.TextColor = Colors.White";
mostCurrent._checkconfirm.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 61;BA.debugLine="checkConfirm.Color = Colors.Red";
mostCurrent._checkconfirm.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 63;BA.debugLine="init.Text = \"INITIALIZE\"";
mostCurrent._init.setText((Object)("INITIALIZE"));
 //BA.debugLineNum = 64;BA.debugLine="init.TextColor = Colors.Red";
mostCurrent._init.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 65;BA.debugLine="init.Color = Colors.White";
mostCurrent._init.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 67;BA.debugLine="showInterstitial.Text = \"INTERSTITIAL\"";
mostCurrent._showinterstitial.setText((Object)("INTERSTITIAL"));
 //BA.debugLineNum = 68;BA.debugLine="showInterstitial.TextColor = Colors.Red";
mostCurrent._showinterstitial.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 69;BA.debugLine="showInterstitial.Color = Colors.White";
mostCurrent._showinterstitial.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 71;BA.debugLine="showVideo.Text = \"SKIPPABLE VIDEO\"";
mostCurrent._showvideo.setText((Object)("SKIPPABLE VIDEO"));
 //BA.debugLineNum = 72;BA.debugLine="showVideo.TextColor = Colors.Red";
mostCurrent._showvideo.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 73;BA.debugLine="showVideo.Color = Colors.White";
mostCurrent._showvideo.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 75;BA.debugLine="rewardedVideo.Text = \"REWARDED VIDEO\"";
mostCurrent._rewardedvideo.setText((Object)("REWARDED VIDEO"));
 //BA.debugLineNum = 76;BA.debugLine="rewardedVideo.TextColor = Colors.Red";
mostCurrent._rewardedvideo.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 77;BA.debugLine="rewardedVideo.Color = Colors.White";
mostCurrent._rewardedvideo.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 79;BA.debugLine="showVideoOrInterstitial.Text = \"SKIPPABLE VIDEO |";
mostCurrent._showvideoorinterstitial.setText((Object)("SKIPPABLE VIDEO | INTERSTITIAL"));
 //BA.debugLineNum = 80;BA.debugLine="showVideoOrInterstitial.TextColor = Colors.Red";
mostCurrent._showvideoorinterstitial.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 81;BA.debugLine="showVideoOrInterstitial.Color = Colors.White";
mostCurrent._showvideoorinterstitial.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 83;BA.debugLine="showBanner.Text = \"BANNER\"";
mostCurrent._showbanner.setText((Object)("BANNER"));
 //BA.debugLineNum = 84;BA.debugLine="showBanner.TextColor = Colors.Red";
mostCurrent._showbanner.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 85;BA.debugLine="showBanner.Color = Colors.White";
mostCurrent._showbanner.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 87;BA.debugLine="hideBanner.Text = \"HIDE BANNER\"";
mostCurrent._hidebanner.setText((Object)("HIDE BANNER"));
 //BA.debugLineNum = 88;BA.debugLine="hideBanner.TextColor = Colors.Red";
mostCurrent._hidebanner.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 89;BA.debugLine="hideBanner.Color = Colors.White";
mostCurrent._hidebanner.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 93;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 33;BA.debugLine="Dim Appodeal As AppodealB4A";
mostCurrent._appodeal = new com.appodeal.basic4android.AppodealB4A();
 //BA.debugLineNum = 34;BA.debugLine="Dim init As Button";
mostCurrent._init = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim showInterstitial As Button";
mostCurrent._showinterstitial = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim showVideo As Button";
mostCurrent._showvideo = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Dim rewardedVideo As Button";
mostCurrent._rewardedvideo = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim showVideoOrInterstitial As Button";
mostCurrent._showvideoorinterstitial = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Dim showBanner As Button";
mostCurrent._showbanner = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Dim hideBanner As Button";
mostCurrent._hidebanner = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Dim checkLogging As CheckBox";
mostCurrent._checklogging = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Dim checkTesting As CheckBox";
mostCurrent._checktesting = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Dim checkConfirm As CheckBox";
mostCurrent._checkconfirm = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Dim appKey As String = \"fee50c333ff3825fd6ad6d38c";
mostCurrent._appkey = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _handler_bannerclicked() throws Exception{
 //BA.debugLineNum = 191;BA.debugLine="Sub Handler_BannerClicked()";
 //BA.debugLineNum = 192;BA.debugLine="Log(\"banner clicked\")";
anywheresoftware.b4a.keywords.Common.Log("banner clicked");
 //BA.debugLineNum = 193;BA.debugLine="End Sub";
return "";
}
public static String  _handler_bannerfailedtoload() throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Sub Handler_BannerFailedToLoad()";
 //BA.debugLineNum = 184;BA.debugLine="Log(\"banner failed to load\")";
anywheresoftware.b4a.keywords.Common.Log("banner failed to load");
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
return "";
}
public static String  _handler_bannerloaded() throws Exception{
 //BA.debugLineNum = 179;BA.debugLine="Sub Handler_BannerLoaded()";
 //BA.debugLineNum = 180;BA.debugLine="Log(\"banner loaded\")";
anywheresoftware.b4a.keywords.Common.Log("banner loaded");
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _handler_bannershown() throws Exception{
 //BA.debugLineNum = 187;BA.debugLine="Sub Handler_BannerShown()";
 //BA.debugLineNum = 188;BA.debugLine="Log(\"banner shown\")";
anywheresoftware.b4a.keywords.Common.Log("banner shown");
 //BA.debugLineNum = 189;BA.debugLine="End Sub";
return "";
}
public static String  _handler_interstitialclicked() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Sub Handler_InterstitialClicked()";
 //BA.debugLineNum = 171;BA.debugLine="Log(\"interstitial clicked\")";
anywheresoftware.b4a.keywords.Common.Log("interstitial clicked");
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _handler_interstitialclosed() throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub Handler_InterstitialClosed()";
 //BA.debugLineNum = 175;BA.debugLine="Log(\"interstitial closed\")";
anywheresoftware.b4a.keywords.Common.Log("interstitial closed");
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _handler_interstitialfailedtoload() throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub Handler_InterstitialFailedToLoad()";
 //BA.debugLineNum = 163;BA.debugLine="Log(\"interstitial failed to load\")";
anywheresoftware.b4a.keywords.Common.Log("interstitial failed to load");
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return "";
}
public static String  _handler_interstitialloaded() throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub Handler_InterstitialLoaded()";
 //BA.debugLineNum = 159;BA.debugLine="Log(\"interstitial loaded\")";
anywheresoftware.b4a.keywords.Common.Log("interstitial loaded");
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
public static String  _handler_interstitialshown() throws Exception{
 //BA.debugLineNum = 166;BA.debugLine="Sub Handler_InterstitialShown()";
 //BA.debugLineNum = 167;BA.debugLine="Log(\"interstitial shown\")";
anywheresoftware.b4a.keywords.Common.Log("interstitial shown");
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _handler_nonskippablefailedtoload() throws Exception{
 //BA.debugLineNum = 222;BA.debugLine="Sub Handler_NonSkippableFailedToLoad()";
 //BA.debugLineNum = 223;BA.debugLine="Log(\"non skippable video failed to load\")";
anywheresoftware.b4a.keywords.Common.Log("non skippable video failed to load");
 //BA.debugLineNum = 224;BA.debugLine="End Sub";
return "";
}
public static String  _handler_nonskippablevideoclosed() throws Exception{
 //BA.debugLineNum = 230;BA.debugLine="Sub Handler_NonSkippableVideoClosed()";
 //BA.debugLineNum = 231;BA.debugLine="Log(\"non skippable video closed\")";
anywheresoftware.b4a.keywords.Common.Log("non skippable video closed");
 //BA.debugLineNum = 232;BA.debugLine="End Sub";
return "";
}
public static String  _handler_nonskippablevideofinished() throws Exception{
 //BA.debugLineNum = 234;BA.debugLine="Sub Handler_NonSkippableVideoFinished()";
 //BA.debugLineNum = 235;BA.debugLine="Log(\"non skippable video shown\")";
anywheresoftware.b4a.keywords.Common.Log("non skippable video shown");
 //BA.debugLineNum = 236;BA.debugLine="End Sub";
return "";
}
public static String  _handler_nonskippablevideoloaded() throws Exception{
 //BA.debugLineNum = 218;BA.debugLine="Sub Handler_NonSkippableVideoLoaded()";
 //BA.debugLineNum = 219;BA.debugLine="Log(\"non skippable video loaded\")";
anywheresoftware.b4a.keywords.Common.Log("non skippable video loaded");
 //BA.debugLineNum = 220;BA.debugLine="End Sub";
return "";
}
public static String  _handler_nonskippablevideoshown() throws Exception{
 //BA.debugLineNum = 226;BA.debugLine="Sub Handler_NonSkippableVideoShown()";
 //BA.debugLineNum = 227;BA.debugLine="Log(\"non skippable video shown\")";
anywheresoftware.b4a.keywords.Common.Log("non skippable video shown");
 //BA.debugLineNum = 228;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideoclosed() throws Exception{
 //BA.debugLineNum = 208;BA.debugLine="Sub Handler_RewardedVideoClosed()";
 //BA.debugLineNum = 209;BA.debugLine="Log(\"rewarded video closed\")";
anywheresoftware.b4a.keywords.Common.Log("rewarded video closed");
 //BA.debugLineNum = 210;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideofailedtoload() throws Exception{
 //BA.debugLineNum = 200;BA.debugLine="Sub Handler_RewardedVideoFailedToLoad()";
 //BA.debugLineNum = 201;BA.debugLine="Log(\"rewarded video failed to load\")";
anywheresoftware.b4a.keywords.Common.Log("rewarded video failed to load");
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideofinished(int _amount,String _name) throws Exception{
 //BA.debugLineNum = 212;BA.debugLine="Sub Handler_RewardedVideoFinished(amount As Int, n";
 //BA.debugLineNum = 213;BA.debugLine="rewardedVideo.Text = \"REWARD \" & amount & \" \" & n";
mostCurrent._rewardedvideo.setText((Object)("REWARD "+BA.NumberToString(_amount)+" "+_name));
 //BA.debugLineNum = 214;BA.debugLine="Log(\"rewarded video shown\" & amount & \" \" & name)";
anywheresoftware.b4a.keywords.Common.Log("rewarded video shown"+BA.NumberToString(_amount)+" "+_name);
 //BA.debugLineNum = 215;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideoloaded() throws Exception{
 //BA.debugLineNum = 196;BA.debugLine="Sub Handler_RewardedVideoLoaded()";
 //BA.debugLineNum = 197;BA.debugLine="Log(\"rewarded video loaded\")";
anywheresoftware.b4a.keywords.Common.Log("rewarded video loaded");
 //BA.debugLineNum = 198;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideoshown() throws Exception{
 //BA.debugLineNum = 204;BA.debugLine="Sub Handler_RewardedVideoShown()";
 //BA.debugLineNum = 205;BA.debugLine="Log(\"rewarded video shown\")";
anywheresoftware.b4a.keywords.Common.Log("rewarded video shown");
 //BA.debugLineNum = 206;BA.debugLine="End Sub";
return "";
}
public static String  _handler_skippablefailedtoload() throws Exception{
 //BA.debugLineNum = 244;BA.debugLine="Sub Handler_SkippableFailedToLoad()";
 //BA.debugLineNum = 245;BA.debugLine="Log(\"skippable video failed to load\")";
anywheresoftware.b4a.keywords.Common.Log("skippable video failed to load");
 //BA.debugLineNum = 246;BA.debugLine="End Sub";
return "";
}
public static String  _handler_skippablevideoclosed() throws Exception{
 //BA.debugLineNum = 252;BA.debugLine="Sub Handler_SkippableVideoClosed()";
 //BA.debugLineNum = 253;BA.debugLine="Log(\"skippable video closed\")";
anywheresoftware.b4a.keywords.Common.Log("skippable video closed");
 //BA.debugLineNum = 254;BA.debugLine="End Sub";
return "";
}
public static String  _handler_skippablevideofinished() throws Exception{
 //BA.debugLineNum = 256;BA.debugLine="Sub Handler_SkippableVideoFinished()";
 //BA.debugLineNum = 257;BA.debugLine="Log(\"skippable video shown\")";
anywheresoftware.b4a.keywords.Common.Log("skippable video shown");
 //BA.debugLineNum = 258;BA.debugLine="End Sub";
return "";
}
public static String  _handler_skippablevideoloaded() throws Exception{
 //BA.debugLineNum = 239;BA.debugLine="Sub Handler_SkippableVideoLoaded()";
 //BA.debugLineNum = 240;BA.debugLine="Log(\"skippable video loaded\")";
anywheresoftware.b4a.keywords.Common.Log("skippable video loaded");
 //BA.debugLineNum = 242;BA.debugLine="End Sub";
return "";
}
public static String  _handler_skippablevideoshown() throws Exception{
 //BA.debugLineNum = 248;BA.debugLine="Sub Handler_SkippableVideoShown()";
 //BA.debugLineNum = 249;BA.debugLine="Log(\"skippable video shown\")";
anywheresoftware.b4a.keywords.Common.Log("skippable video shown");
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
public static String  _hidebanner_click() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub hideBanner_Click";
 //BA.debugLineNum = 154;BA.debugLine="Appodeal.hide(Appodeal.BANNER)";
mostCurrent._appodeal.hide(mostCurrent.activityBA,mostCurrent._appodeal.BANNER);
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _init_click() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub init_Click";
 //BA.debugLineNum = 103;BA.debugLine="Appodeal.setEventHandler(\"Handler\")";
mostCurrent._appodeal.setEventHandler(mostCurrent.activityBA,"Handler");
 //BA.debugLineNum = 104;BA.debugLine="Appodeal.setRewardedVideoCallbacks()";
mostCurrent._appodeal.setRewardedVideoCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 105;BA.debugLine="Appodeal.setBannerCallbacks()";
mostCurrent._appodeal.setBannerCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 106;BA.debugLine="Appodeal.setSkippableVideoCallbacks()";
mostCurrent._appodeal.setSkippableVideoCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 107;BA.debugLine="Appodeal.setInterstitialCallbacks()";
mostCurrent._appodeal.setInterstitialCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 109;BA.debugLine="If(checkConfirm.Checked) Then";
if ((mostCurrent._checkconfirm.getChecked())) { 
 //BA.debugLineNum = 110;BA.debugLine="Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO)";
mostCurrent._appodeal.confirm(mostCurrent._appodeal.SKIPPABLE_VIDEO);
 };
 //BA.debugLineNum = 112;BA.debugLine="If(checkTesting.Checked) Then";
if ((mostCurrent._checktesting.getChecked())) { 
 //BA.debugLineNum = 113;BA.debugLine="Appodeal.setTesting(True)";
mostCurrent._appodeal.setTesting(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 115;BA.debugLine="If(checkLogging.Checked) Then";
if ((mostCurrent._checklogging.getChecked())) { 
 //BA.debugLineNum = 116;BA.debugLine="Appodeal.setLogging(True)";
mostCurrent._appodeal.setLogging(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 118;BA.debugLine="Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO)";
mostCurrent._appodeal.confirm(mostCurrent._appodeal.SKIPPABLE_VIDEO);
 //BA.debugLineNum = 119;BA.debugLine="Appodeal.initialize(appKey, Bit.Or(Appodeal.REWAR";
mostCurrent._appodeal.initialize(mostCurrent.activityBA,mostCurrent._appkey,anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._appodeal.REWARDED_VIDEO,mostCurrent._appodeal.SKIPPABLE_VIDEO));
 //BA.debugLineNum = 120;BA.debugLine="Appodeal.initialize(appKey, Bit.Or(Appodeal.INTER";
mostCurrent._appodeal.initialize(mostCurrent.activityBA,mostCurrent._appkey,anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._appodeal.INTERSTITIAL,mostCurrent._appodeal.BANNER));
 //BA.debugLineNum = 121;BA.debugLine="init.Enabled = False";
mostCurrent._init.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 122;BA.debugLine="init.Text = \"INITIALIZED v\" & Appodeal.getVersion";
mostCurrent._init.setText((Object)("INITIALIZED v"+mostCurrent._appodeal.getVersion()));
 //BA.debugLineNum = 124;BA.debugLine="checkLogging.Enabled = False";
mostCurrent._checklogging.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 125;BA.debugLine="checkTesting.Enabled = False";
mostCurrent._checktesting.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 126;BA.debugLine="checkConfirm.Enabled = False";
mostCurrent._checkconfirm.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _rewardedvideo_click() throws Exception{
 //BA.debugLineNum = 139;BA.debugLine="Sub rewardedVideo_Click";
 //BA.debugLineNum = 140;BA.debugLine="If(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) Th";
if ((mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.REWARDED_VIDEO))) { 
 //BA.debugLineNum = 141;BA.debugLine="Appodeal.show(Appodeal.REWARDED_VIDEO)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.REWARDED_VIDEO);
 };
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _showbanner_click() throws Exception{
 //BA.debugLineNum = 149;BA.debugLine="Sub showBanner_Click";
 //BA.debugLineNum = 150;BA.debugLine="Appodeal.show(Appodeal.BANNER_BOTTOM)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.BANNER_BOTTOM);
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _showinterstitial_click() throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Sub showInterstitial_Click";
 //BA.debugLineNum = 130;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static String  _showvideo_click() throws Exception{
 //BA.debugLineNum = 133;BA.debugLine="Sub showVideo_Click";
 //BA.debugLineNum = 134;BA.debugLine="If(Appodeal.isLoaded(Appodeal.SKIPPABLE_VIDEO)) T";
if ((mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.SKIPPABLE_VIDEO))) { 
 //BA.debugLineNum = 135;BA.debugLine="Appodeal.show(Appodeal.SKIPPABLE_VIDEO)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.SKIPPABLE_VIDEO);
 };
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return "";
}
public static String  _showvideoorinterstitial_click() throws Exception{
 //BA.debugLineNum = 145;BA.debugLine="Sub showVideoOrInterstitial_Click";
 //BA.debugLineNum = 146;BA.debugLine="Appodeal.show(Bit.Or(Appodeal.INTERSTITIAL, Appod";
mostCurrent._appodeal.show(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._appodeal.INTERSTITIAL,mostCurrent._appodeal.SKIPPABLE_VIDEO));
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
}
