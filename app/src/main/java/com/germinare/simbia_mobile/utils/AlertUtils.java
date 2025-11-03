package com.germinare.simbia_mobile.utils;

import static android.view.View.GONE;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; // ⬅️ NOVO IMPORT NECESSÁRIO
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.germinare.simbia_mobile.R;

import java.util.Objects;

public class AlertUtils {

    public static void showDialogDefault(Context ctx, DialogAlertBuilder builder){
        Dialog alert = new Dialog(ctx);
        alert.setContentView(R.layout.alert_default);
        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

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

    public static void showDialogError(Context ctx, String error){
        Dialog alert = new Dialog(ctx);
        alert.setContentView(R.layout.alert_error);
        Objects.requireNonNull(alert.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = alert.findViewById(R.id.tx_message_error);
        Button btnBack = alert.findViewById(R.id.btn_back_error);

        message.setText(error);
        btnBack.setOnClickListener(V -> alert.dismiss());

        alert.show();
    }

    public static AlertDialog showLoadingDialog(Context ctx, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View view = LayoutInflater.from(ctx).inflate(R.layout.alert_loading, null);
        builder.setView(view);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvMessage = view.findViewById(R.id.tv_loading_message);
        tvMessage.setText(message);
        if (message.isEmpty()) tvMessage.setVisibility(GONE);

        alertDialog.show();
        return alertDialog;
    }

    public static Dialog showDialogCustom(Context ctx, int layoutResId, DialogAlertBuilder builder){
        Dialog alert = new Dialog(ctx);
        alert.setContentView(layoutResId);

        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alert.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        if (builder.onCustomViewCreated != null) {
            builder.onCustomViewCreated.onViewCreated(alert.findViewById(android.R.id.content), alert);
        }

        TextView tittle = alert.findViewById(R.id.alert_title);
        if (tittle != null) {
            tittle.setText(builder.title);
        }

        TextView description = alert.findViewById(R.id.alert_subtitle);
        if (description != null) {
            description.setText(builder.description);
        }

        Button btnAccept = alert.findViewById(R.id.btn_accept);
        if (btnAccept != null) {
            btnAccept.setText(builder.textAccept);
            btnAccept.setOnClickListener(v -> {
                if (builder.onAcceptClick != null)
                    builder.onAcceptClick.onClick(alert);
                else
                    alert.dismiss();
            });
        }

        Button btnCancel = alert.findViewById(R.id.btn_cancel);
        if (btnCancel != null) {
            btnCancel.setText(builder.textCancel);
            btnCancel.setOnClickListener(v -> {
                if (builder.onCancelClick != null)
                    builder.onCancelClick.onClick(alert);
                else
                    alert.dismiss();
            });
        }

        alert.show();
        return alert;
    }

    public static void hideDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static class DialogAlertBuilder {

        String title;
        String description;
        String textAccept = "OK";
        String textCancel = "Cancelar";

        OnDialogClickListener onAcceptClick;
        OnDialogClickListener onCancelClick;
        OnCustomViewCreatedListener onCustomViewCreated;

        public interface OnDialogClickListener {
            void onClick(Dialog dialog);
        }

        public interface OnCustomViewCreatedListener {
            void onViewCreated(View customView, Dialog dialog);
        }

        public DialogAlertBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public DialogAlertBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public DialogAlertBuilder setTextAccept(String text) {
            this.textAccept = text;
            return this;
        }

        public DialogAlertBuilder setTextCancel(String text) {
            this.textCancel = text;
            return this;
        }

        public DialogAlertBuilder onAccept(OnDialogClickListener listener) {
            this.onAcceptClick = listener;
            return this;
        }

        public DialogAlertBuilder onCancel(OnDialogClickListener listener) {
            this.onCancelClick = listener;
            return this;
        }

        public DialogAlertBuilder onCustomViewCreated(OnCustomViewCreatedListener listener) {
            this.onCustomViewCreated = listener;
            return this;
        }
    }

}