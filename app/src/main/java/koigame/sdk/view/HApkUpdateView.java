package koigame.sdk.view;import android.app.Activity;import android.app.AlertDialog;import android.app.ProgressDialog;import android.content.DialogInterface;import android.content.Intent;import android.net.Uri;import koigame.sdk.bean.version.KVersionChecker;import koigame.sdk.bean.version.KVersionInfo;import koigame.sdk.util.NetUtils;import koigame.sdk.util.RUtils;import koigame.sdk.util.StringUtils;public class HApkUpdateView extends LinearLayoutViewBase<Object> {	static final String TAG = "ApkUpdateView";	public Activity activity;	protected static final int SHOWCHECKING = 0;	protected static final int SHOWUPDATING = 1;	protected static final int PROCESS = 2;	protected static final int HIDE = 3;	protected static final int SHOWLIBCHECKING = 4;	protected static final int SHOWLIBUPDATING = 5;	protected static final int RESLOADING = 8;	protected ProgressDialog downLoadDialog;	protected float clientVersion = 0.0f;	private String title, ok, cancle, updateNow, updateDelay, continueGame, updateForceMsg, updateChooseMsg,			updateNetTip;	// private String forceUpdateMsg;	private AlertDialog apkUpdateDialog;	public HApkUpdateView(Activity context, int layoutId) {		super(context, layoutId, false);		activity = context;		super.init();		title = getContext().getResources().getString(RUtils.getStringId("hl_update_title"));		ok = getContext().getResources().getString(RUtils.getStringId("hl_yes"));		cancle = getContext().getResources().getString(RUtils.getStringId("hl_cancel"));		updateNow = getContext().getResources().getString(RUtils.getStringId("hl_update_now"));		updateDelay = getContext().getResources().getString(RUtils.getStringId("hl_update_delay"));		continueGame = getContext().getResources().getString(RUtils.getStringId("hl_update_continuegame"));		updateForceMsg = getContext().getResources().getString(RUtils.getStringId("hl_update_force_msg"));		updateChooseMsg = getContext().getResources().getString(RUtils.getStringId("hl_update_choose_msg"));		updateNetTip = getContext().getResources().getString(RUtils.getStringId("hl_update_no_wifi_tip"));	}	public void initView() {	}	public void initEvent() {	}	public void checkVersion(final Activity activity, final KVersionInfo version,			final KVersionChecker.CheckVersionCallback checkCallback) {		if (version.isApkNeedUpdate()) { // apk 需要更新			// 如果是强制更新不必给用户选择，直接下载，否则让用户选择何时更新			if (version.isApkUpdateRequired()) {				activity.runOnUiThread(new Runnable() {					public void run() {						String updateMsg = version.getAppVerDesc();						if (StringUtils.isEmpty(updateMsg)) {							updateMsg = updateForceMsg;						}						if (!NetUtils.instance().isWifiConnected(activity)) { // 非wifi连接							updateMsg = updateMsg + updateNetTip;						}						// 游戏维护中，不能进入游戏						apkUpdateDialog = new AlertDialog.Builder(activity).setMessage(updateMsg).setTitle(title)								.setPositiveButton(updateNow, new DialogInterface.OnClickListener() {									public void onClick(DialogInterface dialog, int which) {										dialog.dismiss();										if (apkUpdateDialog != null) {											try {												apkUpdateDialog.dismiss();											} catch (Exception e) {												e.printStackTrace();											}										}										downloadAPK(activity, version);										checkCallback.onUpdate();									}								}).setNegativeButton(updateDelay, new DialogInterface.OnClickListener() {									public void onClick(DialogInterface dialog, int which) {										dialog.dismiss();										if (apkUpdateDialog != null) {											try {												apkUpdateDialog.dismiss();											} catch (Exception e) {												e.printStackTrace();											}										}										checkCallback.onCancleForceUpdate();									}								}).create();						apkUpdateDialog.setCancelable(false);						apkUpdateDialog.show();					}				});			} else { // 非强制更新				String updateMsg = version.getAppVerDesc();				if (StringUtils.isEmpty(updateMsg)) {					updateMsg = updateChooseMsg;				}				if (!NetUtils.instance().isWifiConnected(activity)) { // 非wifi连接					updateMsg = updateMsg + updateNetTip;				}				apkUpdateDialog = new AlertDialog.Builder(activity).setMessage(updateMsg).setTitle(title)						.setPositiveButton(updateNow, new DialogInterface.OnClickListener() {							public void onClick(DialogInterface dialog, int which) {								dialog.dismiss();								if (apkUpdateDialog != null) {									try {										apkUpdateDialog.dismiss();									} catch (Exception e) {										e.printStackTrace();									}								}								downloadAPK(activity, version);								checkCallback.onUpdate();							}						}).setNegativeButton(continueGame, new DialogInterface.OnClickListener() {							public void onClick(DialogInterface dialog, int which) {								dialog.dismiss();								if (apkUpdateDialog != null) {									try {										apkUpdateDialog.dismiss();									} catch (Exception e) {										e.printStackTrace();									}								}								checkCallback.onEnterGame();							}						}).create();				apkUpdateDialog.setCancelable(false);				apkUpdateDialog.show();			}		} else { // 没有更新			checkCallback.onEnterGame();		}	}	/**	 * 下载apk	 * 	 * @param version	 */	private void downloadAPK(Activity activity, KVersionInfo version) {		String updateUrl = version.getApkUpdateConfig().getUpdatePackageUrl();		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));		activity.startActivity(intent);	}}