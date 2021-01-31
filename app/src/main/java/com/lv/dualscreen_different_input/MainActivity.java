package com.lv.dualscreen_different_input;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/******
 * create By Arctan 20210128
 ***/

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DualScreenDiffInput";
    // Used to load the 'native-lib' library on application startup.
    private Presentation mPresentation;
    private TextView scree1_text1;
    private Button screen1_but1;
    private Button screen1_but2;
    private Button screen1_but3;
    private Button screen1_but4;


    private static int flag = 0;
    private static int index;
    private static int scree1_click_num = 0;
    private static int scree2_click_num = 0;
    private RelativeLayout frameLayout;
    private Display mDisplay;

    private DisplayManager mDisplayManager;
    private MediaRouter mMediaRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen1_but1 = findViewById(R.id.screen1_but1);
        screen1_but2 = findViewById(R.id.screen1_but2);
        screen1_but3 = findViewById(R.id.screen1_but3);
        screen1_but4 = findViewById(R.id.screen1_but4);
        scree1_text1 = findViewById(R.id.screen1_text1);

        screen1_but1.setOnClickListener(this);
        screen1_but2.setOnClickListener(this);
        screen1_but3.setOnClickListener(this);
        screen1_but4.setOnClickListener(this);

        mMediaRouter = ((MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE));
        mDisplayManager = ((DisplayManager) getSystemService(Context.DISPLAY_SERVICE));
    }

    @SuppressLint({"NewApi"})
    @TargetApi(17)
    protected void onPause() {
        super.onPause();
        this.mMediaRouter.removeCallback(this.mMediaRouterCallback);
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    @TargetApi(17)
    protected void onResume() {
        super.onResume();
        this.mMediaRouter.addCallback(2, this.mMediaRouterCallback);
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
        Show(flag);
    }

    @SuppressLint({"NewApi"})
    protected void onStop() {
        super.onStop();
        if (this.mPresentation != null) {
            Log.i(TAG, "Dismissing presentation because the activity is no longer visible.");
            this.mPresentation.dismiss();
            this.mPresentation = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.screen1_but4:
                if ((this.mPresentation == null) || (flag == 0))
                    break;
                Log.i(TAG, "Dismissing presentation because the activity is no longer visible.");
                mPresentation.dismiss();
                mPresentation = null;
                flag = 0;
                break;
            case R.id.screen1_but3:
                scree1_click_num += 1;

                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("主屏点击次数[");
                localStringBuilder.append(String.valueOf(scree1_click_num));
                localStringBuilder.append("]");
                scree1_text1.setText(localStringBuilder.toString());
                break;
            case R.id.screen1_but2:
                Log.d(TAG, "onClick: 2");
                if (flag == 2)
                    break;
                ShowPresentationByDisplaymanager();
                flag = 2;
                break;
            case R.id.screen1_but1:
                if (flag == 1)
                    break;
                ShowPresentationByMediarouter();
                flag = 1;
                break;

            default:return ;
        }
    }

    private void Show(int paramInt) {
        switch (paramInt) {
            default:
                return;
            case 2:
                ShowPresentationByDisplaymanager();
                return;
            case 1:
        }
        ShowPresentationByMediarouter();
    }

    private void showPresentation(Display paramDisplay)
    {
        if ((this.mPresentation != null) && (this.mPresentation.getDisplay() != paramDisplay))
        {
            Log.i(TAG, "Dismissing presentation because the current route no longer has a presentation display.");
            mPresentation.dismiss();
            mPresentation = null;
        }
        if ((this.mPresentation == null) && (paramDisplay != null))
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Showing presentation on display: ");
            localStringBuilder.append(paramDisplay);
            Log.i(TAG, localStringBuilder.toString());
            mPresentation = new MyPresentation(this, paramDisplay);
            try
            {
                mPresentation.show();
                return;
            }
            catch (android.view.WindowManager.InvalidDisplayException paramDisplay2)
            {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in the meantime.", paramDisplay2);
                mPresentation = null;
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void ShowPresentationByDisplaymanager() {
        Display[] arrayOfDisplay = mDisplayManager.getDisplays("android.hardware.display.category.PRESENTATION");
        if (arrayOfDisplay.length > 0)
            showPresentation(arrayOfDisplay[0]);
    }

    @SuppressLint({"NewApi"})
    @TargetApi(17)
    private void ShowPresentationByMediarouter() {
        MediaRouter.RouteInfo localRouteInfo = mMediaRouter.getSelectedRoute(2);
        if (localRouteInfo != null) {
            mDisplay = localRouteInfo.getPresentationDisplay();
            showPresentation(this.mDisplay);
        }
    }



    @SuppressLint({"NewApi"})
    private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() {
        public void onRoutePresentationDisplayChanged(MediaRouter paramMediaRouter, MediaRouter.RouteInfo paramRouteInfo) {
            StringBuilder paramStringBuilder = new StringBuilder();
            paramStringBuilder.append("onRoutePresentationDisplayChanged: info=");
            paramStringBuilder.append(paramRouteInfo);
            Log.d(TAG, paramStringBuilder.toString());
            Show(MainActivity.flag);
        }

        public void onRouteSelected(MediaRouter paramMediaRouter, int paramInt, MediaRouter.RouteInfo paramRouteInfo) {
            StringBuilder paramStringBuilder = new StringBuilder();
            paramStringBuilder.append("onRouteSelected: type=");
            paramStringBuilder.append(paramInt);
            paramStringBuilder.append(", info=");
            paramStringBuilder.append(paramRouteInfo);
            Log.d(TAG, paramStringBuilder.toString());
            Show(MainActivity.flag);
        }

        public void onRouteUnselected(MediaRouter paramMediaRouter,
                                      int paramInt,
                                      MediaRouter.RouteInfo paramRouteInfo) {
            StringBuilder paramStringBuilder = new StringBuilder();
            paramStringBuilder.append("onRouteUnselected: type=");
            paramStringBuilder.append(paramInt);
            paramStringBuilder.append(", info=");
            paramStringBuilder.append(paramRouteInfo);
            Log.d(TAG, paramStringBuilder.toString());
            Show(MainActivity.flag);
        }
    };

    @SuppressLint({"NewApi"})
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
    {
        public void onDisplayAdded(int paramInt)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Display #");
            localStringBuilder.append(paramInt);
            localStringBuilder.append(" added.");
            Log.d(TAG, localStringBuilder.toString());
            Show(MainActivity.flag);
        }

        public void onDisplayChanged(int paramInt)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Display #");
            localStringBuilder.append(paramInt);
            localStringBuilder.append(" changed.");
            Log.d(TAG, localStringBuilder.toString());
            Show(MainActivity.flag);
        }

        @SuppressLint({"NewApi"})
        public void onDisplayRemoved(int paramInt)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("Display #");
            localStringBuilder.append(paramInt);
            localStringBuilder.append(" removed.");
            Log.d(TAG, localStringBuilder.toString());
            Show(MainActivity.flag);
        }
    };

    private final class MyPresentation extends Presentation{
        private Button btn_bottom_left;
        private Button btn_bottom_right;
        private Button btn_left_up;
        private Button btn_right_up;
        private Button screen2_btn_test;
        private TextView screen2_text;
        private RelativeLayout relativeLayout;
        public MyPresentation(Context paramDisplay, Display display)
        {
            super(paramDisplay ,display);
            Log.d(TAG, "MyPresentation: ");
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);
            relativeLayout = findViewById(R.id.frameLayout);
            btn_bottom_left = (Button)findViewById(R.id.btn_bottom_left);
            btn_bottom_right = (Button)findViewById(R.id.btn_bottom_right);
            btn_left_up = (Button)findViewById(R.id.btn_left_up);
            btn_right_up = (Button)findViewById(R.id.btn_right_up);
            screen2_text = (TextView) findViewById(R.id.screen2_text);
            screen2_btn_test = findViewById(R.id.screen2_btn_test);


            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    Toast.makeText(MainActivity.this,R.string.click_external_screen, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "点击了副屏！！");
                }
            });

            btn_bottom_left.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    Toast.makeText(MainActivity.this, R.string.click_btn_bottom_left, Toast.LENGTH_SHORT).show();
                }
            });
            btn_bottom_right.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    Toast.makeText(MainActivity.this, R.string.click_btn_bottom_right, Toast.LENGTH_SHORT).show();
                }
            });
            btn_left_up.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    Toast.makeText(MainActivity.this, R.string.click_btn_up_left, Toast.LENGTH_SHORT).show();
                }
            });
            btn_right_up.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    Toast.makeText(MainActivity.this, R.string.click_btn_up_right, Toast.LENGTH_SHORT).show();
                }
            });
            screen2_btn_test.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramView)
                {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Dialog").setCancelable(true).setMessage("Prsentation Click Test").setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramDialogInterface, int paramInt)
                        {
                            Toast.makeText(MainActivity.this, R.string.click_ok, Toast.LENGTH_LONG).show();
                        }
                    }).create().show();

                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append(getString(R.string.external_click_screen_num)+"[");
                    localStringBuilder.append(String.valueOf(MainActivity.scree2_click_num));
                    localStringBuilder.append("]");
                    screen2_text.setText(localStringBuilder.toString());

                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append(getString(R.string.external_click_screen_num)+"[");
                    localStringBuilder.append(String.valueOf(MainActivity.scree2_click_num));
                    localStringBuilder.append("]");
                    Toast.makeText( MainActivity.this, localStringBuilder.toString(), Toast.LENGTH_SHORT).show();
                    scree2_click_num++;
                }
            });
        }
        public void setPreferredDisplayMode(int paramInt)
        {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.preferredDisplayModeId = paramInt;
            getWindow().setAttributes(localLayoutParams);
        }
    }

}