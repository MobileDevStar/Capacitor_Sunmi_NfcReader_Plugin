package com.rebelity.plugins.sunminfc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
        name = "SunmiNfc",
        permissions = {
                @Permission(
                        alias = "nfc",
                        strings = {Manifest.permission.NFC}
                )
        }
)
public class SunmiNfcPlugin extends Plugin {

    //Intialize attributes
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    AppCompatActivity activity;

    protected static final int REQUEST_NFC = 1993;
    private static final String TAG = "NFC card plugin";

    /**
     * Called when the plugin has been connected to the bridge
     * and is ready to start initializing.
     */
    @Override
    public void load() {

        activity = getActivity();
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
    }

    @PluginMethod()
    public void discoverNfcCard(PluginCall call) {
        if (getPermissionState("nfc") != PermissionState.GRANTED) {
            requestPermissionForAlias("nfc", call, "nfcPermsCallback");
        } else {
            initNdfAdapter(call);
        }
    }

    @PermissionCallback
    private void nfcPermsCallback(PluginCall call) {
        if (getPermissionState("nfc") == PermissionState.GRANTED) {
            initNdfAdapter(call);
        } else {
            call.reject("Permission is required to read nfc card");
        }
    }

    private void initNdfAdapter(PluginCall call) {
        saveCall(call);

        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null){
            Toast.makeText(activity,"NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();

            call.reject("NO NFC Capabilities");
        } else {
            //Create a PendingIntent object so the Android system can
            //populate it with the details of the tag when it is scanned.
            //PendingIntent.getActivity(Context,requestcode(identifier for
            //                           intent),intent,int)
            pendingIntent = PendingIntent.getActivity(activity,0,new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        }
    }

    /**
     * Handle onResume
     */
    @Override
    protected void handleOnResume() {
        assert nfcAdapter != null;
        //nfcAdapter.enableForegroundDispatch(context,pendingIntent,
        //                                    intentFilterArray,
        //                                    techListsArray)
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent,null,null);
    }

    /**
     * Handle onPause
     */
    @Override
    protected void handleOnPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    /**
     * Handle onNewIntent
     * @param intent
     */
    @Override
    protected void handleOnNewIntent(Intent intent) {
        activity.setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;

            PluginCall savedCall = getSavedCall();
            if (savedCall == null) {
                Log.d(TAG, "No stored plugin call for permissions request result");
                return;
            }

            //byte[] payload = detectTagData(tag).getBytes();
            NfcCard payload = detectTagData(tag);

            JSObject ret = new JSObject();
            ret.put("results", payload);
            savedCall.resolve(ret);
        }
    }

    private NfcCard detectTagData(Tag tag) {
        byte[] id = tag.getId();

        String decId = new StringBuilder().append(toDec(id)).toString();
        String reversedDecId = new StringBuilder().append(toReversedDec(id)).toString();
        String hexId = new StringBuilder().append(toHex(id)).toString();
        String reversedHexId = new StringBuilder().append(toReversedHex(id)).toString();

        NfcCard nfcCard = new NfcCard(decId, reversedDecId, hexId, reversedHexId);

        return nfcCard;


        /*
        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }
        Log.v("test",sb.toString());
        return sb.toString();*/
    }
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
}
