package com.example.text2speechdemo;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SpeechSynthesizer mTts;
	private EditText editText;
	private Button button_start;
	private Button button_stop;
	private SharedPreferences mSharedPreferences;
	public static String SPEAKER = "speaker";
	private boolean hasPlayed = false;
	private boolean isPlaying = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		editText = (EditText)findViewById(R.id.tts_content);
		button_start = (Button)findViewById(R.id.btn_start);
		button_stop = (Button)findViewById(R.id.btn_stop);
		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		
		button_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(hasPlayed==false){
					String text = editText.getText().toString();
					setParam();
					int code = mTts.startSpeaking(text, mTtsListener);
					if(code==0){
						button_start.setText("取消播报");
						hasPlayed = true;
					}else{
						Toast.makeText(MainActivity.this, "播报异常，请检查！", Toast.LENGTH_SHORT).show();
					}
				}else{
					mTts.stopSpeaking(mTtsListener);
					button_start.setText("开始播报");
					hasPlayed = false;
				}
			}
		});
		button_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isPlaying==false){
					int code = mTts.pauseSpeaking(mTtsListener);
					button_stop.setText("继续播报");
					isPlaying = true;
				}else{
					mTts.resumeSpeaking(mTtsListener);
					button_stop.setText("暂停播报");
					isPlaying = false;
				}
			}
		});
		// 初始化合成对象
		mTts = new SpeechSynthesizer(this, mTtsInitListener);
		
	}

	/**
     * 初期化监听。
     */
	private InitListener mTtsInitListener = new InitListener(){

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			if(code==ErrorCode.SUCCESS){
				button_start.setEnabled(true);
			}
			
		}
		
	};

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setParam() {
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, mSharedPreferences.getString("engine_preference", "local"));
		if(mSharedPreferences.getString("engine_preference", "local").equalsIgnoreCase("local")){
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME,
					mSharedPreferences.getString("role_cn_preference", "xiaoyan"));
		}else{
			mTts.setParameter(SpeechSynthesizer.VOICE_NAME,
					mSharedPreferences.getString("role_cn_preference", "xiaoyan")); 
		}
		mTts.setParameter(SpeechSynthesizer.SPEED,
				mSharedPreferences.getString("speed_preference", "60"));
		
		mTts.setParameter(SpeechSynthesizer.PITCH,
				mSharedPreferences.getString("pitch_preference", "55"));
		
		mTts.setParameter(SpeechSynthesizer.VOLUME,
				mSharedPreferences.getString("volume_preference", "100"));
	}
	
	  /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {

		@Override
		public void onBufferProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCompleted(int arg0) throws RemoteException {
			button_start.setText("开始播报");
			hasPlayed = false;
			button_stop.setText("暂停播报");
			isPlaying = false;
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSpeakProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
			// TODO Auto-generated method stub
			
		}};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.stopSpeaking(mTtsListener);
        // 退出时释放连接
        mTts.destory();
    }
}
