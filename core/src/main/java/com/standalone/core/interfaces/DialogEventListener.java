package com.standalone.core.interfaces;

import android.content.DialogInterface;
import android.os.Bundle;

public interface DialogEventListener {
    void onDialogSubmit(DialogInterface dialog, Bundle bundle);

    void onDialogCancel(DialogInterface dialog);

}
