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
public static boolean _showrw = false;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 42;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 44;BA.debugLine="init.Text = \"INITIALIZE\"";
mostCurrent._init.setText((Object)("INITIALIZE"));
 //BA.debugLineNum = 45;BA.debugLine="showInterstitial.Text = \"INTERSTITIAL\"";
mostCurrent._showinterstitial.setText((Object)("INTERSTITIAL"));
 //BA.debugLineNum = 46;BA.debugLine="showVideo.Text = \"VIDEO\"";
mostCurrent._showvideo.setText((Object)("VIDEO"));
 //BA.debugLineNum = 47;BA.debugLine="rewardedVideo.Text = \"REWARDED VIDEO\"";
mostCurrent._rewardedvideo.setText((Object)("REWARDED VIDEO"));
 //BA.debugLineNum = 48;BA.debugLine="showVideoOrInterstitial.Text = \"VIDEO | INTERSTIT";
mostCurrent._showvideoorinterstitial.setText((Object)("VIDEO | INTERSTITIAL"));
 //BA.debugLineNum = 49;BA.debugLine="showBanner.Text = \"BANNER\"";
mostCurrent._showbanner.setText((Object)("BANNER"));
 //BA.debugLineNum = 50;BA.debugLine="hideBanner.Text = \"HIDE BANNER\"";
mostCurrent._hidebanner.setText((Object)("HIDE BANNER"));
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 29;BA.debugLine="Dim Appodeal As AppodealB4A";
mostCurrent._appodeal = new com.appodeal.basic4android.AppodealB4A();
 //BA.debugLineNum = 30;BA.debugLine="Dim init As Button";
mostCurrent._init = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim showInterstitial As Button";
mostCurrent._showinterstitial = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim showVideo As Button";
mostCurrent._showvideo = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim rewardedVideo As Button";
mostCurrent._rewardedvideo = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim showVideoOrInterstitial As Button";
mostCurrent._showvideoorinterstitial = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim showBanner As Button";
mostCurrent._showbanner = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim hideBanner As Button";
mostCurrent._hidebanner = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim showRW As Boolean = False";
_showrw = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _handler_bannerloaded() throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub Handler_BannerLoaded";
 //BA.debugLineNum = 114;BA.debugLine="Appodeal.show(Appodeal.BANNER_BOTTOM)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.BANNER_BOTTOM);
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideofinished(int _amount,String _name) throws Exception{
 //BA.debugLineNum = 109;BA.debugLine="Sub Handler_RewardedVideoFinished(amount As Int, n";
 //BA.debugLineNum = 110;BA.debugLine="rewardedVideo.Text = \"REWARD \" & amount & \" \" & n";
mostCurrent._rewardedvideo.setText((Object)("REWARD "+BA.NumberToString(_amount)+" "+_name));
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}
public static String  _handler_rewardedvideoloaded() throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Sub Handler_RewardedVideoLoaded";
 //BA.debugLineNum = 103;BA.debugLine="If (showRW) Then";
if ((_showrw)) { 
 //BA.debugLineNum = 104;BA.debugLine="Appodeal.show(Appodeal.REWARDED_VIDEO)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.REWARDED_VIDEO);
 //BA.debugLineNum = 105;BA.debugLine="showRW = False";
_showrw = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _hidebanner_click() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub hideBanner_Click";
 //BA.debugLineNum = 99;BA.debugLine="Appodeal.hide(Appodeal.BANNER)";
mostCurrent._appodeal.hide(mostCurrent.activityBA,mostCurrent._appodeal.BANNER);
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _init_click() throws Exception{
String _appkey = "";
 //BA.debugLineNum = 61;BA.debugLine="Sub init_Click";
 //BA.debugLineNum = 62;BA.debugLine="Dim appKey As String = \"fee50c333ff3825fd6ad6d38c";
_appkey = "fee50c333ff3825fd6ad6d38cff78154de3025546d47a84f";
 //BA.debugLineNum = 65;BA.debugLine="Appodeal.initialize(appKey, Appodeal.ALL) 'use th";
mostCurrent._appodeal.initialize(mostCurrent.activityBA,_appkey,mostCurrent._appodeal.ALL);
 //BA.debugLineNum = 66;BA.debugLine="Appodeal.setRewardedVideoCallbacks()";
mostCurrent._appodeal.setRewardedVideoCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 67;BA.debugLine="Appodeal.setBannerCallbacks()";
mostCurrent._appodeal.setBannerCallbacks(mostCurrent.activityBA);
 //BA.debugLineNum = 68;BA.debugLine="Appodeal.setEventHandler(\"Handler\")";
mostCurrent._appodeal.setEventHandler(mostCurrent.activityBA,"Handler");
 //BA.debugLineNum = 69;BA.debugLine="showRW = True";
_showrw = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 70;BA.debugLine="init.Text = \"INITIALIZED v\" & Appodeal.getVersion";
mostCurrent._init.setText((Object)("INITIALIZED v"+mostCurrent._appodeal.getVersion()));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 24;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _rewardedvideo_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub rewardedVideo_Click";
 //BA.debugLineNum = 85;BA.debugLine="If(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) The";
if ((mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.REWARDED_VIDEO))) { 
 //BA.debugLineNum = 86;BA.debugLine="Appodeal.show(Appodeal.REWARDED_VIDEO)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.REWARDED_VIDEO);
 };
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _showbanner_click() throws Exception{
 //BA.debugLineNum = 94;BA.debugLine="Sub showBanner_Click";
 //BA.debugLineNum = 95;BA.debugLine="Appodeal.show(Appodeal.BANNER_BOTTOM)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.BANNER_BOTTOM);
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _showinterstitial_click() throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Sub showInterstitial_Click";
 //BA.debugLineNum = 75;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _showvideo_click() throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub showVideo_Click";
 //BA.debugLineNum = 79;BA.debugLine="If(Appodeal.isLoaded(Appodeal.VIDEO)) Then";
if ((mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.VIDEO))) { 
 //BA.debugLineNum = 80;BA.debugLine="Appodeal.show(Appodeal.VIDEO)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.VIDEO);
 };
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _showvideoorinterstitial_click() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub showVideoOrInterstitial_Click";
 //BA.debugLineNum = 91;BA.debugLine="Appodeal.show(Bit.Or(Appodeal.INTERSTITIAL, Appod";
mostCurrent._appodeal.show(mostCurrent.activityBA,anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._appodeal.INTERSTITIAL,mostCurrent._appodeal.VIDEO));
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
}
