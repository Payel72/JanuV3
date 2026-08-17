package com.janu.v3;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class JanuAccessibilityService extends AccessibilityService {
    public static JanuAccessibilityService instance;

    @Override public void onServiceConnected() { super.onServiceConnected(); instance = this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
    @Override public boolean onUnbind(android.content.Intent intent) {
        if (instance == this) instance = null;
        return super.onUnbind(intent);
    }

    public void back() { performGlobalAction(GLOBAL_ACTION_BACK); }
    public void home() { performGlobalAction(GLOBAL_ACTION_HOME); }
    public void recents() { performGlobalAction(GLOBAL_ACTION_RECENTS); }
}
