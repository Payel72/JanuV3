package com.janu.v3;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int REQ_VOICE = 100;
    private static final int REQ_PERMS = 101;
    private TextView status;
    private TextToSpeech tts;
    private boolean torchOn = false;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);

        tts = new TextToSpeech(this, s -> {
            if (s == TextToSpeech.SUCCESS) tts.setLanguage(new Locale("bn", "IN"));
        });

        findViewById(R.id.voiceButton).setOnClickListener(v -> listen());
        findViewById(R.id.appsButton).setOnClickListener(v -> showApps());
        findViewById(R.id.torchButton).setOnClickListener(v -> toggleTorch());
        findViewById(R.id.accessibilityButton).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        findViewById(R.id.settingsButton).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_SETTINGS)));

        requestNeededPermissions();
        say("জানু ভার্সন থ্রি প্রস্তুত।");
    }

    private void requestNeededPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> p = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) p.add(Manifest.permission.RECORD_AUDIO);
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) p.add(Manifest.permission.CALL_PHONE);
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) p.add(Manifest.permission.SEND_SMS);
            if (!p.isEmpty()) requestPermissions(p.toArray(new String[0]), REQ_PERMS);
        }
    }

    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "জানুকে বলুন...");
        try { startActivityForResult(i, REQ_VOICE); }
        catch (Exception e) { say("ভয়েস রিকগনিশন পাওয়া যাচ্ছে না।"); }
    }

    @Override protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r,c,d);
        if (r == REQ_VOICE && c == RESULT_OK && d != null) {
            ArrayList<String> a = d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (a != null && !a.isEmpty()) execute(a.get(0));
        }
    }

    private void execute(String raw) {
        String x = raw.toLowerCase(new Locale("bn","IN")).trim();
        status.setText("আপনি বলেছেন: " + raw);

        if (x.contains("টর্চ") || x.contains("ফ্ল্যাশ")) { toggleTorch(); return; }
        if (x.contains("সেটিং")) { startActivity(new Intent(Settings.ACTION_SETTINGS)); return; }
        if (x.contains("ক্যামেরা")) { openPackage("com.android.camera"); return; }
        if (x.contains("ইউটিউব")) { openPackage("com.google.android.youtube"); return; }
        if (x.contains("হোয়াটসঅ্যাপ") || x.contains("হোয়াটস অ্যাপ")) { openPackage("com.whatsapp"); return; }
        if (x.contains("ক্রোম")) { openPackage("com.android.chrome"); return; }
        if (x.contains("গুগল")) {
            String q = raw.replace("গুগল","").trim();
            if (q.isEmpty()) q = "Google";
            Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(q)));
            startActivity(in); return;
        }
        if (x.contains("ভলিউম বাড়াও")) { changeVolume(1); return; }
        if (x.contains("ভলিউম কমাও")) { changeVolume(-1); return; }
        if (x.matches(".*(কল|ফোন).*")) {
            String digits = raw.replaceAll("[^0-9+]", "");
            if (digits.length() >= 5) call(digits);
            else say("নম্বরসহ বলুন, যেমন ৯৮৭৬৫৪৩২১০-এ কল করো।");
            return;
        }
        if (x.contains("ব্যাক")) {
            if (JanuAccessibilityService.instance != null) JanuAccessibilityService.instance.back();
            else say("ফোন কন্ট্রোলের Accessibility চালু করুন।");
            return;
        }
        if (x.contains("হোম")) {
            if (JanuAccessibilityService.instance != null) JanuAccessibilityService.instance.home();
            else say("ফোন কন্ট্রোলের Accessibility চালু করুন।");
            return;
        }
        say("এই কমান্ডটি এখনো শেখানো হয়নি।");
    }

    private void call(String number) {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQ_PERMS);
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
        } catch (Exception e) { say("কল করা যায়নি।"); }
    }

    private void openPackage(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) startActivity(i);
        else say("অ্যাপটি ফোনে ইনস্টল নেই।");
    }

    private void changeVolume(int delta) {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                delta > 0 ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        say(delta > 0 ? "ভলিউম বাড়িয়েছি।" : "ভলিউম কমিয়েছি।");
    }

    private void toggleTorch() {
        if (Build.VERSION.SDK_INT < 23) return;
        CameraManager cm = (CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            String id = cm.getCameraIdList()[0];
            torchOn = !torchOn;
            cm.setTorchMode(id, torchOn);
            say(torchOn ? "টর্চ চালু।" : "টর্চ বন্ধ।");
        } catch (CameraAccessException | SecurityException e) { say("টর্চ চালু করা যায়নি।"); }
    }

    private void showApps() {
        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(main, 0);
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(getPackageManager()).toString().compareToIgnoreCase(b.loadLabel(getPackageManager()).toString());
            }
        });
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(30,20,30,20);
        for (ResolveInfo ri : apps) {
            Button b = new Button(this);
            b.setText(ri.loadLabel(getPackageManager()));
            b.setOnClickListener(v -> {
                Intent i = getPackageManager().getLaunchIntentForPackage(ri.activityInfo.packageName);
                if (i != null) startActivity(i);
            });
            box.addView(b);
        }
        ScrollView sv = new ScrollView(this); sv.addView(box);
        new AlertDialog.Builder(this).setTitle("অ্যাপ লিস্ট").setView(sv).setPositiveButton("বন্ধ", null).show();
    }

    private void say(String s) {
        status.setText(s);
        if (tts != null) tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, "janu");
    }

    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
