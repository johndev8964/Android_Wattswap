package com.citrusbug.wattswap.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrusbug.wattswap.R;
import com.citrusbug.wattswap.activity.HomeActivity.FontChangeCrawler;
import com.citrusbug.wattswap.bean.Survey;
import com.citrusbug.wattswap.util.Constant;
import com.citrusbug.wattswap.util.DatabaseHandler;
import com.citrusbug.wattswap.util.DropboxHelper;
import com.citrusbug.wattswap.util.ImageUtil;

public class SurveyDetailActivity extends Activity implements OnTouchListener {

	TextView homeTextView;
	TextView editTextView;
	TextView titleTextView;
	TextView surveyNameTextView;
	TextView discriptionTextView;
	TextView mapTextView;
	TextView scheduledTextView;
	TextView contactTextView;
	TextView phoneTextView;
	TextView emailTextView;
	TextView floorTextView;
	TextView startSurveyTextView;
	TextView areaTextView;
	TextView fixtureTypeTextView;
	TextView ftCountTextView;
	TextView emailSurveyTextView;
	TextView removeImageTextView;
	TextView previewHtml;

	CheckBox imageIncludeinEmail;

	ImageView attachmentImageView;

	Survey surveyItem = null;
	DatabaseHandler dbHandler;
	Context mContext;

	HashMap<String, String> countHashMap;
	String sid;
	String address;
	String finaleImagePath = null;

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private Uri mImageCaptureUri;
	ArrayAdapter<String> imageOptionAdapter;
	Bitmap thumbnail;
	File dirFile;

	// private DbxAccountManager mDbxAcctMgr;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_surveydetail);
		mContext = this;
		
		FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "gothic.ttf");
	    fontChanger.replaceFonts ((ViewGroup) this.findViewById(android.R.id.content));
		
		Constant.activity = SurveyDetailActivity.this;
		Constant.CONTEXT = mContext;
		sid = getIntent().getStringExtra("sid");
		Constant.SURVEY_ID = sid;
		dbHandler = new DatabaseHandler(mContext);
		getUIControlId();

		// mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
		// "o7l7sg73xzgvkfq", "13x1ump0fd8lf1h");
		// mDbxAcctMgr.startLink((Activity)this, 0);

		String dirNamePath = Constant.SD_CARD_PATH + "/"
				+ Constant.SURVEY_NAME_PATH + "_" + Constant.SURVEY_ID;
		dirFile = new File(dirNamePath);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

	}

	public void getUIControlId() {
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		homeTextView = (TextView) findViewById(R.id.home_TextView_SurveyDetail);
		homeTextView.setTypeface(font);
		editTextView = (TextView) findViewById(R.id.edit_TextView_SurveyDetail);
		titleTextView = (TextView) findViewById(R.id.titleTextViewDetail);
		surveyNameTextView = (TextView) findViewById(R.id.surveyNameTextViewDetail);
		discriptionTextView = (TextView) findViewById(R.id.surveyDiscriptionTextViewDetail);
		mapTextView = (TextView) findViewById(R.id.mapTextViewDetail);
		scheduledTextView = (TextView) findViewById(R.id.scheduledTextViewDetail);
		contactTextView = (TextView) findViewById(R.id.contactTextViewDetail);
		phoneTextView = (TextView) findViewById(R.id.phoneTextViewDetail);
		emailTextView = (TextView) findViewById(R.id.emailTextViewDetail);
		floorTextView = (TextView) findViewById(R.id.floorCountTextViewDetail);
		startSurveyTextView = (TextView) findViewById(R.id.startSurveyTextViewDetail);
		areaTextView = (TextView) findViewById(R.id.areaTextViewDetail);
		fixtureTypeTextView = (TextView) findViewById(R.id.fixtureTypetTextViewDetail);
		ftCountTextView = (TextView) findViewById(R.id.totalFixtureCountTextViewDetail);
		emailSurveyTextView = (TextView) findViewById(R.id.emailSurvey_TextViewDetail);
		removeImageTextView = (TextView) findViewById(R.id.removeImageTextView);

		attachmentImageView = (ImageView) findViewById(R.id.attachment_ImageViewDetail);
		previewHtml = (TextView) findViewById(R.id.html_TextViewDetail);
		imageIncludeinEmail = (CheckBox) findViewById(R.id.checkincludeimages);

		homeTextView.setOnTouchListener(this);
		editTextView.setOnTouchListener(this);
		mapTextView.setOnTouchListener(this);
		startSurveyTextView.setOnTouchListener(this);
		emailSurveyTextView.setOnTouchListener(this);
		attachmentImageView.setOnTouchListener(this);
		removeImageTextView.setOnTouchListener(this);
		previewHtml.setOnTouchListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Constant.ISSURVEYDELETED) {
			Constant.ISSURVEYDELETED = false;
			finish();
		}

		if (Constant.ISFINISH)
			Constant.ISFINISH = false;

		surveyItem = dbHandler.getSurveyInfo(sid);

		if (surveyItem != null) {

			titleTextView.setText(Constant.SURVEY_NAME);
			surveyNameTextView.setText(surveyItem.surveyName);
			discriptionTextView.setText(surveyItem.address1 + " "
					+ surveyItem.address2 + " " + surveyItem.city);
			scheduledTextView.setText(surveyItem.scheduledDate);
			contactTextView.setText(surveyItem.contanctName);
			emailTextView.setText(surveyItem.email);
			phoneTextView.setText(surveyItem.phone);

			address = surveyItem.address1 + " " + surveyItem.address2 + " "
					+ surveyItem.city + " " + surveyItem.state;

			String imagePath = surveyItem.path;
			if (imagePath != null && !imagePath.equals("")) {
				thumbnail = ImageUtil.decodeFile(new File(imagePath), 60, 80);
				attachmentImageView.setImageBitmap(thumbnail);
				removeImageTextView.setVisibility(View.VISIBLE);
			} else {
				removeImageTextView.setVisibility(View.GONE);
				attachmentImageView.setImageResource(R.drawable.icon_camera);
			}

			countHashMap = dbHandler.GetCount(sid);

			if (countHashMap != null) {
				floorTextView.setText(countHashMap.get("floor"));
				areaTextView.setText(countHashMap.get("area"));
				fixtureTypeTextView.setText(countHashMap.get("fixture"));
				ftCountTextView.setText(countHashMap.get("totalfixcount"));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.home_TextView_SurveyDetail:
			finish();
			activityCloseTransition();
			break;
		case R.id.edit_TextView_SurveyDetail:
			Constant.ISUPDATEMODE = true;
			Intent updateIntent = new Intent(mContext,
					AddNewSurveyActivity.class);
			updateIntent.putExtra("survey", surveyItem);
			startActivity(updateIntent);
			break;
		case R.id.mapTextViewDetail:
			Intent mapIntent = new Intent(mContext, MapActivity.class);
			mapIntent.putExtra("address", address);
			startActivity(mapIntent);
			activityOpenTransition();
			break;
		case R.id.startSurveyTextViewDetail:
			Intent intentFloorList = new Intent(mContext, FloorList.class);
			startActivity(intentFloorList);
			activityOpenTransition();
			break;
		case R.id.emailSurvey_TextViewDetail:
			ContentValues values = new ContentValues();
			values.put(DatabaseHandler.KEY_STATUS, "0");
			int updateRow = dbHandler.updateSurvey(values, sid);
			Log.d("Survey Updated: ", "Yes row" + updateRow);
			sendSurveyEmail(surveyItem.email);
			break;
		case R.id.removeImageTextView:
			attachmentImageView.setImageResource(R.drawable.icon_camera);
			removeImageTextView.setVisibility(View.GONE);
			finaleImagePath = "";
			updateImage();

			break;
		case R.id.attachment_ImageViewDetail:
			PreperImageSelectionDailog();
			break;
		case R.id.html_TextViewDetail:
			ShowHtmlPreview();
			break;
		}
		return false;
	}

	public void activityCloseTransition() {
		finish();
		overridePendingTransition(R.animator.in_from_left,
				R.animator.out_to_right);
	}

	public void activityOpenTransition() {
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}

	/**
	 * This method used to send email of Survey Detail with HTML, CSV and Image
	 * attachment.
	 * 
	 * @param string
	 *            email address
	 */
	public void sendSurveyEmail(String email) {

		String body = dbHandler.getSurveyStringTemplet(sid).toString();
		String subject = "Survey Report: " + surveyNameTextView.getText()
				+ " Address: " + discriptionTextView.getText();

		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/html");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				Html.fromHtml(body));

		ArrayList<Uri> uris = new ArrayList<Uri>();
		ArrayList<File> attachments = new ArrayList<File>();
		if (imageIncludeinEmail.isChecked()) {
			String imagePath = surveyItem.path;
			if (imagePath != null && !imagePath.equals("")) {
				File fileIn = new File(imagePath);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}

			if (Constant.FIXTURE_IMAGE_LIST.size() > 0) {
				for (int i = 0; i < Constant.FIXTURE_IMAGE_LIST.size(); i++) {
					File fileIn = new File(Constant.FIXTURE_IMAGE_LIST.get(i));
					Uri u = Uri.fromFile(fileIn);
					uris.add(u);
				}
			}
		}

		if (Constant.CSV_URI != null) {
			// Add CSV file to the intent
			uris.add(Constant.CSV_URI);
		}

		if (Constant.HTML_URI != null) {
			uris.add(Constant.HTML_URI);
		}

		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(Intent.createChooser(emailIntent, "Email:"));

		Session session = createSessionObject();

		Message message;
		try {

			for (int i = 0; i < uris.size(); i++) {
				Uri u = uris.get(i);
				attachments.add(new File(u.getPath()));
			}

			message = createMessage("lightingsurveys@gmail.com", subject, Html
					.fromHtml(body).toString(), attachments, session);
			new SendMailTask().execute(message);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ShowHtmlPreview() {
		dbHandler.getSurveyStringTemplet(sid);

		if (Constant.HTML_URI != null) {

			Intent htmlPreviewIntent = new Intent(getApplicationContext(),
					HtmlPreviewActivity.class);

			startActivity(htmlPreviewIntent);
			activityTransition();
		}
	}
	
	public void activityTransition() {
		overridePendingTransition(R.animator.in_from_right,
				R.animator.out_to_left);
	}

	public void PreperImageSelectionDailog() {

		imageOptionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, getResources()
						.getStringArray(R.array.image_select_option));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Image");
		builder.setAdapter(imageOptionAdapter,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						// pick from camera
						if (item == 0) {

							String fileName = dirFile.getAbsolutePath() + "/"
									+ Constant.SURVEY_NAME_PATH + "_"
									+ Constant.SURVEY_ID + "_"
									+ System.currentTimeMillis() + ".jpg";

							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							mImageCaptureUri = Uri.fromFile(new File(fileName));
							intent.putExtra(
									android.provider.MediaStore.EXTRA_OUTPUT,
									mImageCaptureUri);
							try {
								intent.putExtra("return-data", true);
								startActivityForResult(intent, PICK_FROM_CAMERA);
							} catch (ActivityNotFoundException e) {
								e.printStackTrace();
							}
						} else if (item == 1) {
							// pick from file
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(i, PICK_FROM_FILE);
						} else if (item == 2) {
							dialog.dismiss();
						}
					}
				});

		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			try {
				finaleImagePath = mImageCaptureUri.getPath();
				thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60,
						80);
				attachmentImageView.setImageBitmap(thumbnail);
				removeImageTextView.setVisibility(View.VISIBLE);

				if (Constant.IS_RESIZE_ENABLE)
					ImageUtil.saveResizedImage(finaleImagePath, true);

				updateImage();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				e.printStackTrace();
			}
			break;
		case PICK_FROM_FILE:
			try {
				mImageCaptureUri = data.getData();
				String[] filePathColumn = { MediaColumns.DATA };
				Cursor cursor = getContentResolver().query(mImageCaptureUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				finaleImagePath = cursor.getString(columnIndex);
				cursor.close();
				thumbnail = ImageUtil.decodeFile(new File(finaleImagePath), 60,
						80);
				attachmentImageView.setImageBitmap(thumbnail);
				removeImageTextView.setVisibility(View.VISIBLE);

				String destinationName = dirFile.getAbsolutePath() + "/"
						+ Constant.SURVEY_NAME_PATH + "_" + Constant.SURVEY_ID
						+ "_" + System.currentTimeMillis() + ".jpg";

				File source = new File(finaleImagePath);

				File destination = new File(destinationName);

				if (source.exists()) {
					@SuppressWarnings("resource")
					FileChannel src = new FileInputStream(source).getChannel();
					@SuppressWarnings("resource")
					FileChannel dst = new FileOutputStream(destination)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();

					if (Constant.IS_RESIZE_ENABLE) {
						ImageUtil.saveResizedImage(destinationName, false);
						finaleImagePath = destinationName;
					}
					updateImage();
				}

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
				e.printStackTrace();
			}
			break;
		}
	}

	public void updateImage() {
		ContentValues values1 = new ContentValues();
		values1.put(DatabaseHandler.KEY_PATH, finaleImagePath);
		dbHandler.updateSurvey(values1, surveyItem.surveyId);

		DropboxHelper.SaveToDropbox(finaleImagePath);

	}

	private Session createSessionObject() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("lightingsurveysinfo",
						"surveyapp123");
			}
		});
	}

	private Message createMessage(String email, String subject,
			String messageBody, ArrayList<File> attachments, Session session)
			throws MessagingException, UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("lightingsurveysinfo@gmail.com",
				"Survey App"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email, email));
		message.setSubject(subject);

		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(messageBody);
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);

		if (attachments != null && attachments.size() > 0) {

			for (int i = 0; i < attachments.size(); i++) {
				MimeBodyPart mbp2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(attachments.get(i));
				mbp2.setDataHandler(new DataHandler(fds));
				mbp2.setFileName(fds.getName());

				mp.addBodyPart(mbp2);
			}
		}

		message.setContent(mp);

		return message;
	}

	private class SendMailTask extends AsyncTask<Message, Void, Void> {
		// private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressDialog = ProgressDialog.show(SurveyDetailActivity.this,
			// "Please wait", "Sending mail", true, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			// progressDialog.dismiss();
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Transport.send(messages[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}
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