package com.citrusbug.wattswap.activity;


import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.CustomImageGrid;
import com.citrusbug.wattswap.util.SwipeDetector;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

public class DetailedFixtureActivity extends Activity implements OnTouchListener {
	TextView typeName;
	TextView codeName;
	TextView countsText;
	TextView hoursText;
	TextView wattsText;
	TextView totalyealcostsText;
	
	TextView goBackText;
	GridView gridView;
	
	SwipeDetector swipeDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detailed_fixture);
		
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
		
	    swipeDetector = new SwipeDetector() {
			@Override
			public void onActionUp() {
				// TODO Auto-generated method stub
				super.onActionUp();
				if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
					finish();
					activityCloseTransition();
				}
			}
		};
	    
		View contentView = (View) findViewById(R.id.detailedFixtureContent);
		contentView.setOnTouchListener(swipeDetector);
		
		Intent receiveIntent = this.getIntent();

		if(receiveIntent.getStringExtra("typename") != null) {
			typeName = (TextView) findViewById(R.id.typename_detailfixture);
			typeName.setText(receiveIntent.getStringExtra("typename"));
		}
		if(receiveIntent.getStringExtra("codename") != null) {
			codeName = (TextView) findViewById(R.id.codename_detailedfixture);
			codeName.setText(receiveIntent.getStringExtra("codename"));		
		}
		if(receiveIntent.getStringExtra("counts") != null) {
			countsText = (TextView) findViewById(R.id.counttext_detailedfixture);
			countsText.setText(receiveIntent.getStringExtra("counts"));
		}
		if(receiveIntent.getStringExtra("watts") != null) {
			wattsText = (TextView) findViewById(R.id.wattstext_detailedfixture);
			wattsText.setText(receiveIntent.getStringExtra("watts"));
		}
		if(receiveIntent.getStringExtra("hours") != null) {
			hoursText = (TextView) findViewById(R.id.esthourstext_detailedfixture);
			hoursText.setText(receiveIntent.getStringExtra("hours"));
		}
		
		totalyealcostsText = (TextView) findViewById(R.id.totalyearlycost_detailedfixture);
		int watts = 0;
		if(wattsText != null && !wattsText.getText().toString().equals("")) {
			watts = Integer.parseInt(wattsText.getText().toString());
		}
		
		int hours = 0;
		if(hoursText != null && !hoursText.getText().toString().equals("")) {
			hours = Integer.parseInt(hoursText.getText().toString());
		}
		
		float total = watts * hours / 1000.0f;
		
		String totalCost = "$" + String.format("%.2f", total);
		
		totalyealcostsText.setText(totalCost);
		
		goBackText = (TextView) findViewById(R.id.goback_detailedfixture);
		goBackText.setOnTouchListener(this);
		
		CustomImageGrid adapter = new CustomImageGrid(DetailedFixtureActivity.this, FixturesListActivity.imagePaths);
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(adapter);
		
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

	@Override
	public boolean onTouch(View view, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if(view == goBackText) {
			finish();
			activityCloseTransition();
		}
		return false;
	}
	
	public void activityCloseTransition() {
		
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}
}
