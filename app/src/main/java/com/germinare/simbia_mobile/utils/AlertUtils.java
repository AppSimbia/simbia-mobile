package com.germinare.simbia_mobile.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.germinare.simbia_mobile.R;

public class AlertUtils {

    public static void showDialog(Context ctx, DialogAlertBuilder builder){
        Dialog alert = new Dialog(ctx);
        alert.setContentView(R.layout.alert_default);
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tittle = alert.findViewById(R.id.tx_tittle);
        TextView description = alert.findViewById(R.id.tx_description);
        Button btnAccept = alert.findViewById(R.id.btn_accept);
        Button btnCancel = alert.findViewById(R.id.btn_cancel);

        tittle.setText(builder.title);
        description.setText(builder.description);
        btnAccept.setText(builder.textAccept);
        btnCancel.setText(builder.textCancel);

        btnAccept.setOnClickListener(v -> {
            if (builder.onAcceptClick != null)
                builder.onAcceptClick.onClick(alert);
            else
                alert.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            if (builder.onCancelClick != null)
                builder.onCancelClick.onClick(alert);
            else
                alert.dismiss();
        });

        alert.show();
    }

    public static class DialogAlertBuilder {

        String title;
        String description;
        String textAccept = "OK";
        String textCancel = "Cancelar";

        OnDialogClickListener onAcceptClick;
        OnDialogClickListener onCancelClick;

        public interface OnDialogClickListener {
            void onClick(Dialog dialog);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTextAccept(String text) {
            this.textAccept = text;
        }

        public void setTextCancel(String text) {
            this.textCancel = text;
        }

        public void onAccept(OnDialogClickListener listener) {
            this.onAcceptClick = listener;
        }

        public void onCancel(OnDialogClickListener listener) {
            this.onCancelClick = listener;
        }
    }

}
