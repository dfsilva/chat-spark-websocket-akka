package br.com.anhanguera.chat.utils;

import java.io.IOException;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class Firebase {

	public static void init() throws IOException {
		InputStream serviceAccount = Firebase.class.getClassLoader().getResourceAsStream("firebase_key.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://distributedchat-eb29e.firebaseio.com").build();

		FirebaseApp.initializeApp(options);
	}

}
