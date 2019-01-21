package org.maybank.ocr.plugin;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64DataException;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gid.com.gidvisionlib.Common.Application.Application;
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
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        cordova.setActivityResultCallback(this);
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
            
        }else if(action.equals("OCR_KTP")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_KTP);
            return true;
        }else if(action.equals("OCR_NPWP")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_NPWP);
            return true;
        }else if(action.equals("OCR_DEBIT_CARD")){
            this.goToNewIntent(GO_TO_OCR_ACTIVITY_DEBITCARD);
            return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GO_TO_OCR_ACTIVITY_NPWP || requestCode == GO_TO_OCR_ACTIVITY_KTP || requestCode == GO_TO_OCR_ACTIVITY_DEBITCARD){
            if(data.getStringExtra(GIDLibVisionMainActivity.ACTIVITY_STATUS).equals(Application.ACTIVITY_STATUS_OK)){
                String textResult = data.getStringExtra(GIDLibVisionMainActivity.OCR_CAPTURED_TEXT);
                String imagePath = data.getStringExtra(GIDLibVisionMainActivity.OCR_CAPTURED_IMG);
                String responseJson = this.getResponseData("00000","Succeed",textResult,imagePath);
                this.callbackContext.success(responseJson);

            }else if(data.getStringExtra(GIDLibVisionMainActivity.ACTIVITY_STATUS).equals(Application.ACTIVITY_STATUS_CANCELED)){
                Log.d(TAG, "onActivityResult: Canceled");
                String responseJson = this.getResponseData("00001","Activity Canceled","EMPTY","EMPTY");
                this.callbackContext.error(responseJson);
            }else if(data.getStringExtra(GIDLibVisionMainActivity.ACTIVITY_STATUS).equals(Application.ACTIVITY_STATUS_FAILED)){
                Log.d(TAG, "onActivityResult: Failed");
                String responseJson = this.getResponseData("99999","FatalError","EMPTY","EMPTY");
                this.callbackContext.error(responseJson);
            }
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

    private String getResponseData(String errCode, String errMsg,String capturedText,String imagePath){
        String result = "";
        JSONObject responseEnvelope = new JSONObject();
        JSONObject responseBody = new JSONObject();
        try{
            //setup responseBody
            responseBody.put("textResult",capturedText);
            responseBody.put("image",imagePath);
            responseEnvelope.put("ErrCode",errCode);
            responseEnvelope.put("ErrStatus",errMsg);
            responseEnvelope.put("platform","android");
            responseEnvelope.put("ServiceResult",responseBody);
            result = responseEnvelope.toString();
        }
        catch(JSONException je){
            Log.d(TAG, je.getMessage());
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }


        return result;
    }
}
