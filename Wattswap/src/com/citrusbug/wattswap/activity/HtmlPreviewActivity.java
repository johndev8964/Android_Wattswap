package com.citrusbug.wattswap.activity;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.util.Constant;


import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.TextView;


public class HtmlPreviewActivity extends Activity implements OnTouchListener{

	WebView htmlPreviewView;
	TextView backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_htmlpreview);
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
		getUIControlId();
		
		
	}

	public void getUIControlId() {

		htmlPreviewView = (WebView) findViewById(R.id.webViewHtmlPreview);
		backButton = (TextView) findViewById(R.id.htmlPreview_back);
		
		backButton.setOnTouchListener(this);
		
		if (Constant.HTML_URI != null) {
			
			htmlPreviewView.loadUrl(Constant.HTML_URI.toString());
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.htmlPreview_back:
			
			finish();
			break;
		}
		return false;
	}

	public class FontChangeCrawler
	{
	    private Typeface typeface;

	    public FontChangeCrawler(Typeface typeface)
	    {
	        this.typeface = typeface;
	    }

	    public FontChangeCrawler(AssetManager assets, String assetsFontFileName)
	    {
	        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
	    }

	    public void replaceFonts(ViewGroup viewTree)
	    {
	        View child;
	        for(int i = 0; i < viewTree.getChildCount(); ++i)
	        {
	            child = viewTree.getChildAt(i);
	            if(child instanceof ViewGroup)
	            {
	                // recursive call
	                replaceFonts((ViewGroup)child);
	            }
	            else if(child instanceof TextView)
	            {
	                // base case
	                ((TextView) child).setTypeface(typeface);
	            }
	        }
	    }
	}
}
