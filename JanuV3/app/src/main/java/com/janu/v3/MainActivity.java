package com.janu.v3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Switch mainSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainSwitch = findViewById(R.id.mainSwitch);

        // সুইচটি অন বা অফ করলে কী হবে তার কোড
        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // সুইচ অন করলে এই মেসেজ দেখাবে
                    Toast.makeText(MainActivity.this, "অ্যাসিস্ট্যান্ট চালু হয়েছে", Toast.LENGTH_SHORT).show();
                    // ভবিষ্যতে আপনার কথা শোনার কোড এখানে থাকবে
                } else {
                    // সুইচ অফ করলে এই মেসেজ দেখাবে
                    Toast.makeText(MainActivity.this, "অ্যাসিস্ট্যান্ট বন্ধ হয়েছে", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
