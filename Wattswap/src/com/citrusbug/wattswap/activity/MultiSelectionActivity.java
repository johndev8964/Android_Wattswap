package com.citrusbug.wattswap.activity;

import java.util.ArrayList;

import com.citrusbug.wattswap.R;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;



public class MultiSelectionActivity  extends Activity {
	ListView selectionListView;
	String arrayName;
	String type;

	String[] strArray;
	Resources rs;
	Intent receivedIntent;
	
	EditText otherOption;
	ArrayList seletedItems;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addnewfixturemulti);
		rs = getResources();
		receivedIntent = getIntent();
		
		if (receivedIntent != null) {
			type = receivedIntent.getStringExtra("type");
			setArrayValueToList();
		}
		
//		((Button)findViewById(R.id.findSelected1)).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ArrayList<Integer> values=adapter.getSelectedPosition();
//				if(values.size()==0){
//					Toast.makeText(MultiSelectionActivity.this, "Please select any one", Toast.LENGTH_SHORT).show();
//				}else{
//					//save all fixtures and get code from EditFixuresActivity activity
//					String data="";
//					for(Integer i:values){
//						if(data.equals("")){
//							data=strArray[(int)i];
//						}else{
//							data=data+","+strArray[(int)i];
//						}
//					}
//					Intent intent = new Intent();
//					intent.putExtra("name", data);
//					if (type.equalsIgnoreCase("code")) {
//						setResult(1, intent);
//					} else if (type.equalsIgnoreCase("style")) {
//						setResult(2, intent);
//					} else if (type.equalsIgnoreCase("mounting")) {
//						setResult(3, intent);
//					} else if (type.equalsIgnoreCase("controlled")) {
//						setResult(4, intent);
//					} else if (type.equalsIgnoreCase("option")) {
//						setResult(5, intent);
//					} else if (type.equalsIgnoreCase("height")) {
//						setResult(6, intent);
//						}
//					else if (type.equalsIgnoreCase("Ballast_type")) {
//						setResult(7, intent);
//						}
//					else if (type.equalsIgnoreCase("Ballast_factor")) {
//						setResult(8, intent);
//						}
//					finish();
//				}
//			}
//		});
		
//		selectionListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				adapter.setSelected(position);	
//			}
//		});
	}

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
		adapter = new SelectionAdapter(
					getApplicationContext(), strArray);
		CharSequence[] items = strArray;
		  
		ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.AppTheme);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		
		seletedItems = new ArrayList();
		Typeface font = Typeface.createFromAsset(getAssets(),
					"gothic.ttf");
			
		TextView dialogTitle = new TextView(this);
		dialogTitle.setText("Select"+" "+ Character.toUpperCase(type.charAt(0)) + type.substring(1));
		dialogTitle.setTextSize(26);
		dialogTitle.setTextColor(getResources().getColor(R.color.blue_top_bar));
		dialogTitle.setPadding(40, 40, 40, 40);
		dialogTitle.setTypeface(font);
		builder.setCustomTitle(dialogTitle);
		
		builder.setAdapter(adapter, null)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which) {
	                //do something  
	            	   ArrayList<Integer> values = seletedItems;
	            	   if(values.size() == 0 && otherOption.getText().toString().length() == 0){
	   						Toast.makeText(MultiSelectionActivity.this, "Please select any one", Toast.LENGTH_SHORT).show();
	   				   } else{
	   				   //save all fixtures and get code from EditFixuresActivity activity
	   				   String data = "";
	   					for(Integer i:values){
	   						if(data.equals("")){
	   							data=strArray[i];
	   						}else{
	   							data=data + "," + strArray[i];
	   						}
	   					}
	   					
	   					if(data.equals("")) data = otherOption.getText().toString();
	   					else data = data + "," + otherOption.getText().toString();
	   					
	   					Intent intent = new Intent();
	   					intent.putExtra("name", data);
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
	   					finish();
	   					overridePendingTransition(R.animator.in_from_left,
		   						R.animator.out_to_right);
	   					
	   				}
	            	finish();
	            }  
			});
		   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.dismiss();
	            	   finish();
	            	   overridePendingTransition(R.animator.in_from_left,
		   						R.animator.out_to_right);
	            	   
	               }
	           });
		   builder.create();
		
		   final AlertDialog dialog = builder.create();
		   otherOption = new EditText(this);
		   otherOption.setHint("Other");
		   otherOption.setTextColor(this.getResources().getColor(R.color.gray));
		   otherOption.setTextSize(24);
		   otherOption.setTypeface(font);
		   otherOption.setSingleLine(true);
		   otherOption.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		    
		   otherOption.clearFocus();
		   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		   imm.hideSoftInputFromWindow(otherOption.getWindowToken(), 0);
		    
		   final float scale = getResources().getDisplayMetrics().density;
		   int padding_45dp = (int) (45 * scale + 0.5f);
		   int padding_40dp = (int) (40 * scale + 0.5f);
		   int padding_10dp = (int) (10 * scale + 0.5f);
		    
		   otherOption.setPadding(padding_40dp, padding_10dp, padding_10dp, padding_10dp);
		   otherOption.setHeight(padding_45dp);
		    
		   dialog.getListView().addFooterView(otherOption);
		   dialog.getListView().setDivider(null);
		   dialog.show();
		
		   dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
		   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	
	SelectionAdapter adapter;
	class SelectionAdapter extends BaseAdapter {
		protected Object chkBox;
		private Context mContext;
		private String[] fixureTypeList;
		private boolean[] checkList;
		
		public SelectionAdapter(Context mContext, String[] fixureTypeList) {
			this.mContext = mContext;
			this.fixureTypeList = fixureTypeList;
			checkList=new boolean[fixureTypeList.length];
		}

		@Override
		public int getCount() {
			return fixureTypeList.length;
		}

		@Override
		public Object getItem(int position) {
			return fixureTypeList[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.multiselection_row_layout, null);
				holder = new ViewHolder();
				holder.itemTextView = (TextView) convertView
						.findViewById(R.id.listItemTextView);
				Typeface font = Typeface.createFromAsset(getAssets(), "gothic.ttf");
				holder.itemTextView.setTypeface(font);
				
				holder.chkBox = (CheckBox)convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.itemTextView.setText(fixureTypeList[position]);
			if(checkList[position]){
	        	holder.chkBox.setChecked(true);
	        }else{
	        	holder.chkBox.setChecked(false);
	        }
	        holder.chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked){
						seletedItems.add(position);
						//checkList[position]=true;
					}else{
						seletedItems.remove(Integer.valueOf(position));
						//checkList[position]=false;
					}
					//SelectionAdapter.this.notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
		public ArrayList<Integer> getSelectedPosition(){
			ArrayList<Integer> selectedpos=new ArrayList<Integer>(); 			
			for(int pos=0;pos<checkList.length;pos++){
				if(checkList[pos]){
					selectedpos.add(pos);
				}
			}
			
			return selectedpos;
		}
		
		public void setSelected(int pos){
			if(checkList[pos])
				checkList[pos]=false;
			else
				checkList[pos]=true;
			SelectionAdapter.this.notifyDataSetChanged();
		}

	}

	 static class ViewHolder {
		TextView itemTextView;
		CheckBox chkBox;
	}
	
}