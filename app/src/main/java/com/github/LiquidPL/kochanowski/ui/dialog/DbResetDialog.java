package com.github.LiquidPL.kochanowski.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.ui.SyncActivity;
import com.github.LiquidPL.kochanowski.util.PrefUtils;


public class DbResetDialog extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());

        builder.setTitle (getResources ().getString (R.string.dbreset_dialog_title));
        builder.setMessage (getResources ().getString (R.string.dbreset_dialog_message));

        builder.setPositiveButton (getResources ().getString (R.string.action_yes), new DialogInterface.OnClickListener () {


            @Override
            public void onClick (DialogInterface dialog, int which)
            {
                Intent intent = new Intent (getActivity (), SyncActivity.class);
                startActivity (intent);

                PrefUtils.setDatabaseUpgradeStatus (getActivity (), false);
            }
        });

        builder.setNegativeButton (getResources ().getString (R.string.action_no), new DialogInterface.OnClickListener () {


            @Override
            public void onClick (DialogInterface dialog, int which)
            {
                getActivity ().finish ();
            }
        });

        return builder.create ();
    }
}
