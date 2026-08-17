android.os.Bundle;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.app.AlertDialog;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(main, 0);
        
        if (apps != null) {
            Collections.sort(apps, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo a, ResolveInfo b) {
                    return a.loadLabel(getPackageManager()).toString()
                            .compareToIgnoreCase(b.loadLabel(getPackageManager()).toString());
                }
            });
        }

        View sv = null;
        try {
            new AlertDialog.Builder(this)
                    .setTitle("সাহায্য")
                    .setView(sv)
                    .setPositiveButton("বন্ধ", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
