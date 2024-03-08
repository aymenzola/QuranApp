package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dz.quranapp.databinding.ActivityPdfBinding;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ActivityMatnOnlineViewer extends AppCompatActivity {

	// creating a variable
	// for PDF view.

	// url of our PDF file.
	String pdfurl = "https://www.pdfquran.com/download/english/english-quran.pdf";
	private ActivityPdfBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityPdfBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		new RetrievePDFfromUrl().execute(pdfurl);
	}

	// create an async task class for loading pdf file from URL.
	public class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
		@Override
		protected InputStream doInBackground(String... strings) {
			// we are using inputstream 
			// for getting out PDF.
			Log.e("pdftag","starting ");
			InputStream inputStream = null;
			try {
				URL url = new URL(strings[0]);
				// below is the step where we are 
				// creating our connection.
				HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
				if (urlConnection.getResponseCode() == 200) {
					// response is success.
					// we are getting input stream from url 
					// and storing it in our variable.
					inputStream = new BufferedInputStream(urlConnection.getInputStream());
				}
				
			} catch (IOException e) {
				Log.e("pdftag","starting "+e.getMessage());
				// this is the method
				// to handle errors.
				e.printStackTrace();
				return null;
			}
			return inputStream;
		}

		@Override
		protected void onPostExecute(InputStream inputStream) {
			// after the execution of our async 
			// task we are loading our pdf in our pdf view.
			Log.e("pdftag","finished so loading pdf");
			binding.pdfView.fromStream(inputStream).load();
		}
	}
}
