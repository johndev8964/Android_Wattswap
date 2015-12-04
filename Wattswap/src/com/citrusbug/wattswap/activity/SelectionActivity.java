package com.citrusbug.wattswap.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.adapter.FixtureTypeAdapter;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.ImageUtil;

public class SelectionActivity extends Activity implements OnFocusChangeListener, OnItemClickListener {
	ListView selectionListView;
	String arrayName;
	String type;

	String[] strArray;
	Resources rs;
	Intent receivedIntent;
	FixtureTypeAdapter adapter;
	String newFixtureItem;
	EditText otherOption;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addnewfixure);
		rs = getResources();
//		selectionListView = (ListView) findViewById(R.id.selectFixtureList);
	   receivedIntent = getIntent();

		if (receivedIntent != null) {
			type = receivedIntent.getStringExtra("type");
			setArrayValueToList();
		}
		
	}

//		selectionListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				Intent intent = new Intent();
//				intent.putExtra("name", strArray[position]);
//				if (type.equalsIgnoreCase("code")) {
//					setResult(1, intent);
//				} else if (type.equalsIgnoreCase("style")) {
//					setResult(2, intent);
//				} else if (type.equalsIgnoreCase("mounting")) {
//					setResult(3, intent);
//				} else if (type.equalsIgnoreCase("controlled")) {
//					setResult(4, intent);
//				} else if (type.equalsIgnoreCase("option")) {
//					setResult(5, intent);
//				} else if (type.equalsIgnoreCase("height")) {
//					setResult(6, intent);
//				}
//				else if (type.equalsIgnoreCase("Ballast_type")) {
//					setResult(7, intent);
//				}
//				else if (type.equalsIgnoreCase("Ballast_factor")) {
//					setResult(8, intent);
//				}
//					
//				finish();
//			}
//		});
//	}

	public void setArrayValueToList() {
		if (type.equalsIgnoreCase("code")) {
			arrayName = receivedIntent.getStringExtra("name");
			if (arrayName.equalsIgnoreCase("Linear T-12")) {
				strArray = rs.getStringArray(R.array.CodeLinearT12);
			} else if (arrayName.equalsIgnoreCase("Linear T-8")) {
				strArray = rs.getStringArray(R.array.CodeLinearT8);
			} else if (arrayName.equalsIgnoreCase("Linear T-5")) {
				strArray = rs.getStringArray(R.array.CodeLinearT5);
			} else if (arrayName.equalsIgnoreCase("Circline")) {
				strArray = rs.getStringArray(R.array.CodeCircline);
			} else if (arrayName.equalsIgnoreCase("Inc")) {
				strArray = rs.getStringArray(R.array.CodeInc);
			} else if (arrayName.equalsIgnoreCase("Inc - Hal")) {
				strArray = rs.getStringArray(R.array.CodeIncHal);
			} else if (arrayName.equalsIgnoreCase("Exit Inc")) {
				strArray = rs.getStringArray(R.array.CodeExitInc);
			} else if (arrayName.equalsIgnoreCase("Exit CFL")) {
				strArray = rs.getStringArray(R.array.CodeExitCFL);
			} else if (arrayName.equalsIgnoreCase("Exit LED")) {
				strArray = rs.getStringArray(R.array.CodeExitLED);
			} else if (arrayName.equalsIgnoreCase("CFL")) {
				strArray = rs.getStringArray(R.array.CodeCFL);
			} else if (arrayName.equalsIgnoreCase("MH")) {
				strArray = rs.getStringArray(R.array.CodeMH);
			} else if (arrayName.equalsIgnoreCase("HPS")) {
				strArray = rs.getStringArray(R.array.CodeHPS);
			} else {
				strArray = rs.getStringArray(R.array.CodeOther);
			}
			
		} else if (type.equalsIgnoreCase("style")) {
			arrayName = receivedIntent.getStringExtra("name");
			if (arrayName.equalsIgnoreCase("Linear T-12")) {
				strArray = rs.getStringArray(R.array.StyleLinearT12);
			} else if (arrayName.equalsIgnoreCase("Linear T-8")) {
				strArray = rs.getStringArray(R.array.StyleLinearT8_T5);
			} else if (arrayName.equalsIgnoreCase("Linear T-5")) {
				strArray = rs.getStringArray(R.array.StyleLinearT8_T5);
			} else if (arrayName.equalsIgnoreCase("Circline")) {
				strArray = rs.getStringArray(R.array.StyleCircline);
			} else if (arrayName.equalsIgnoreCase("Inc")) {
				strArray = rs.getStringArray(R.array.StyleInc);
			} else if (arrayName.equalsIgnoreCase("Inc - Hal")) {
				strArray = rs.getStringArray(R.array.StyleIncHal);
			} else if (arrayName.equalsIgnoreCase("Exit Inc")
					|| arrayName.equalsIgnoreCase("Exit CFL")
					|| arrayName.equalsIgnoreCase("Exit LED")) {
				strArray = rs.getStringArray(R.array.StyleExit);
			} else if (arrayName.equalsIgnoreCase("CFL")) {
				strArray = rs.getStringArray(R.array.StyleCFL);
			} else if (arrayName.equalsIgnoreCase("MH")
					|| arrayName.equalsIgnoreCase("HPS")) {
				strArray = rs.getStringArray(R.array.StyleMH_HPS);
			} else {
				strArray = rs.getStringArray(R.array.StyleOther);
			}
			
		} else if (type.equalsIgnoreCase("mounting")) {
			strArray = getResources().getStringArray(R.array.Mounting);
		} else if (type.equalsIgnoreCase("controlled")) {
			strArray = getResources().getStringArray(R.array.Controlled);
		} else if (type.equalsIgnoreCase("option")) {
			strArray = getResources().getStringArray(R.array.Options);
		} else if (type.equalsIgnoreCase("height")) {
			strArray = getResources().getStringArray(R.array.Height);
		}
		else if (type.equalsIgnoreCase("Ballast_type")) {
			strArray = getResources().getStringArray(R.array.Ballast_type);
		}
		else if (type.equalsIgnoreCase("Ballast_factor")) {
			strArray = getResources().getStringArray(R.array.Ballast_factor);
		}
		
		adapter = new FixtureTypeAdapter(
				getApplicationContext(), strArray);
		//strArray = getResources().getStringArray(R.array.Typefixures);
		
		//ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.AppTheme);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		    //AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    // Set the dialog title
		Typeface font = Typeface.createFromAsset(getAssets(),
				"gothic.ttf");
		
		TextView dialogTitle = new TextView(this);
		dialogTitle.setText("Select"+" "+ Character.toUpperCase(type.charAt(0)) + type.substring(1));
		dialogTitle.setTextSize(26);
		dialogTitle.setTextColor(getResources().getColor(R.color.blue_top_bar));
		dialogTitle.setPadding(40, 40, 40, 40);
		dialogTitle.setTypeface(font);
		builder.setCustomTitle(dialogTitle);
	
		newFixtureItem = null;
		builder.setAdapter(adapter, null)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int position) {
            	   if(adapter.mSelected == -1) {
        			   if(otherOption.getText().toString().length() > 0) {
        				   newFixtureItem = Character.toUpperCase(otherOption
   								.getText().toString().charAt(0)) + otherOption
   								.getText().toString().substring(1);
        				   Intent intent = new Intent();
	       	   				intent.putExtra("name", newFixtureItem);
	       	   				if (type.equalsIgnoreCase("code")) {
	       	   					setResult(1, intent);
	       	   				} else if (type.equalsIgnoreCase("style")) {
	       	   					setResult(2, intent);
	       	   				} else if (type.equalsIgnoreCase("mounting")) {
	       	   					setResult(3, intent);
	       	   				} else if (type.equalsIgnoreCase("controlled")) {
	       	   					setResult(4, intent);
	       	   				} else if (type.equalsIgnoreCase("option")) {
	       	   					setResult(5, intent);
	       	   				} else if (type.equalsIgnoreCase("height")) {
	       	   					setResult(6, intent);
	       	   				}
	       	   				else if (type.equalsIgnoreCase("Ballast_type")) {
	       	   					setResult(7, intent);
	       	   				}
	       	   				else if (type.equalsIgnoreCase("Ballast_factor")) {
	       	   					setResult(8, intent);
	       	   				}
       	   					
       	   				
       	   				overridePendingTransition(R.animator.in_from_left,
       	   						R.animator.out_to_right);
       	   				finish();
        			   }
        			   else {
        				   dialog.dismiss();
        			   }
        		   }
        		   else {
        			   Intent intent = new Intent();
      	   				intent.putExtra("name", newFixtureItem);
      	   				if (type.equalsIgnoreCase("code")) {
      	   					setResult(1, intent);
      	   				} else if (type.equalsIgnoreCase("style")) {
      	   					setResult(2, intent);
      	   				} else if (type.equalsIgnoreCase("mounting")) {
      	   					setResult(3, intent);
      	   				} else if (type.equalsIgnoreCase("controlled")) {
      	   					setResult(4, intent);
      	   				} else if (type.equalsIgnoreCase("option")) {
      	   					setResult(5, intent);
      	   				} else if (type.equalsIgnoreCase("height")) {
      	   					setResult(6, intent);
      	   				}
      	   				else if (type.equalsIgnoreCase("Ballast_type")) {
      	   					setResult(7, intent);
      	   				}
      	   				else if (type.equalsIgnoreCase("Ballast_factor")) {
      	   					setResult(8, intent);
      	   				}
      	   				overridePendingTransition(R.animator.in_from_left,
      	   						R.animator.out_to_right);
      	   				finish();
        		   }
            	}
           })    
       
       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int id) {
        	   dialog.dismiss();
        	   overridePendingTransition(R.animator.in_from_left,
  						R.animator.out_to_right);
        	   finish();
           }
       });
	   
		final AlertDialog dialog = builder.create();

	    otherOption = new EditText(this);
	    otherOption.setHint("Other");
	    otherOption.setTextColor(this.getResources().getColor(R.color.gray));
	    otherOption.setTextSize(26);
	    otherOption.setTypeface(font);
	    otherOption.setSingleLine(true);
	    otherOption.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
	    
	    otherOption.clearFocus();
	    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(otherOption.getWindowToken(), 0);
	    otherOption.setOnFocusChangeListener(this);
	    
	    final float scale = getResources().getDisplayMetrics().density;
	    int padding_60dp = (int) (60 * scale + 0.5f);
	    int padding_10dp = (int) (10 * scale + 0.5f);
	    
	    otherOption.setPadding(padding_60dp, 0, 10, padding_10dp);
	    otherOption.setHeight(padding_60dp);
	    
	    dialog.getListView().addFooterView(otherOption);
		
	    dialog.getListView().setDivider(null);
	    dialog.getListView().setDividerHeight(0);
	    dialog.getListView().setOnItemClickListener(this);
	    dialog.show();
	    
	    dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
	    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		newFixtureItem = strArray[position];
		adapter.mSelected = position;
		adapter.notifyDataSetChanged();
		otherOption.clearFocus();
		otherOption.clearComposingText();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(otherOption.getWindowToken(), 0);
	}

	@Override
	public void onFocusChange(View view, boolean focus) {
		// TODO Auto-generated method stub
		if(view.isFocused()) {
			adapter.mSelected = -1;
			adapter.notifyDataSetChanged();
		}
	}
}