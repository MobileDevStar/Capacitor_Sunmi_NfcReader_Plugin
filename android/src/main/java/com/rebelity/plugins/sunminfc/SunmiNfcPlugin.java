package com.rebelity.plugins.sunminfc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

@CapacitorPlugin(
    name = "SunmiNfc",
    permissions = {
        @Permission(strings = { Manifest.permission.NFC }, alias = "nfc")
    }
)
public class SunmiNfcPlugin extends Plugin {

    protected final String TAG = "SunmiNfcPlugin";

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    AppCompatActivity activity;

    PluginCall      pCall;

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
        Log.d(TAG, "echo: " + value);

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
    }

    @PluginMethod()
    public void discoverNfcCard(PluginCall call) {
        pCall = call;
        pCall.setKeepAlive(true);
        initNfcAdapter(pCall);
    }
/*
    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d(TAG, "No stored plugin call for permissions request result");
            return;
        }

        for(int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.reject("User denied permission");
                Log.d(TAG, "User denied permission");
                return;
            }
        }

        if (requestCode == REQUEST_NFC) {
            // We got the permission!
            initNfcAdapter(savedCall);
        }
    }
*/
    @SuppressLint("UnspecifiedImmutableFlag")
    private void initNfcAdapter(PluginCall call) {
        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null){
            Log.d(TAG, "NO NFC Capabilities");
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
        //assert nfcAdapter != null;
        //nfcAdapter.enableForegroundDispatch(context,pendingIntent,
        //                                    intentFilterArray,
        //                                    techListsArray)
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent,null,null);
        }

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
     * Handle onDestroy
     */
    @Override
    protected void handleOnDestroy() {
        nfcAdapter = null;
    }

    /**
     * Handle onNewIntent
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

            if (pCall == null) {
                Log.d(TAG, "No stored plugin call for permissions request result");
                return;
            }

            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag == null) {
                Log.d(TAG, "NO NFC Tag");
                pCall.reject("NO NFC Tag");
                return;
            }

            //byte[] payload = detectTagData(tag).getBytes();
            NfcCard payload = detectTagData(tag);

            Log.d(TAG, "Card Dec ID: " + payload.getDecId());

            JSObject ret = new JSObject();
            ret.put("decId", payload.getDecId());
            ret.put("reversedDecId", payload.getReversedDecId());
            ret.put("hexId", payload.getHexId());
            ret.put("reversedHexId", payload.getReversedHexId());
            pCall.resolve(ret);
        }
    }

    private NfcCard detectTagData(Tag tag) {
        byte[] id = tag.getId();

        String decId = String.valueOf(toDec(id));
        String reversedDecId = String.valueOf(toReversedDec(id));
        String hexId = toHex(id);
        String reversedHexId = toReversedHex(id);

        return new NfcCard(decId, reversedDecId, hexId, reversedHexId);
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
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }
}
