package com.germinare.simbia_mobile.ui.features;

public class DrawerItem {
    private int iconResId;
    private String label;

    public DrawerItem(int iconResId, String label) {
        this.iconResId = iconResId;
        this.label = label;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getLabel() {
        return label;
    }
}
