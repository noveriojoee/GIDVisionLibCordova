package maybank.ocr.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.content.Intent;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import gid.com.gidvisionlib.ViewActivity.GIDLibVisionMainActivity;


/**
 * This class echoes a string called from JavaScript.
 */
public class GidLibVision extends CordovaPlugin {

    private static final int GO_TO_OCR_ACTIVITY_KTP = 1;
    private static final int GO_TO_OCR_ACTIVITY_NPWP = 2;
    private static final int GO_TO_OCR_ACTIVITY_DEBITCARD = 3;
    private static final String TAG ="GidLibVision";
    private Intent i;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
            
        }else if(action.equals("OCR_KTP")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_KTP);
        }else if(action.equals("OCR_NPWP")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_NPWP);
        }else if(action.equals("OCR_DEBIT_CARD")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_DEBITCARD);
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void goToNewIntent(int id) {
        if(id == GO_TO_OCR_ACTIVITY_KTP){
            this.i = new Intent(this.cordova.getActivity(),GIDLibVisionMainActivity.class);
            this.i.putExtra("OCR_MODE","KTP");
            this.cordova.getActivity().startActivityForResult(i,GO_TO_OCR_ACTIVITY_KTP);
        }else if(id == GO_TO_OCR_ACTIVITY_DEBITCARD){
            this.i = new Intent(this.cordova.getActivity(),GIDLibVisionMainActivity.class);
            this.i.putExtra("OCR_MODE","DEBIT_CARD");
            this.cordova.getActivity().startActivityForResult(i,GO_TO_OCR_ACTIVITY_DEBITCARD);
        }else if(id == GO_TO_OCR_ACTIVITY_NPWP){
            this.i = new Intent(this.cordova.getActivity(),GIDLibVisionMainActivity.class);
            this.i.putExtra("OCR_MODE","NPWP");
            this.cordova.getActivity().startActivityForResult(i,GO_TO_OCR_ACTIVITY_NPWP);
        }else{
            Log.d(TAG, "intent not found: ");
        }
    }
}
