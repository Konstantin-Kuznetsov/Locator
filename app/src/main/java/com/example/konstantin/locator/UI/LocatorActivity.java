package com.example.konstantin.locator.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocatorActivity extends SingleFragmentActivity {

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return LocatorFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // проверяем, установлен ли на устройстве Google Play Services
        GoogleApiAvailability apiAvailabilityInstance = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailabilityInstance.isGooglePlayServicesAvailable(this);


        // Сигнатура: public Dialog getErrorDialog (Activity activity, int errorCode, int requestCode,
        // DialogInterface.OnCancelListener cancelListener)
        //
        // The returned dialog displays a localized message about the error and upon user
        // confirmation (by tapping on dialog) will direct them to the Play Store if Google Play
        // services is out of date or missing, or to system settings if Google Play services
        // is disabled on the device.

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailabilityInstance.getErrorDialog(this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    // если сервис недоступен - выйти
                                    finish();
                                }
                            });
            errorDialog.show();
        }
    }
}
