/********* GidLibVision.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <GIDVisionLib/GIDVisionLib.h>

@interface GidLibVision : CDVPlugin<GIDOcrProtocol> {
  // Member variables go here.
}

@property CDVInvokedUrlCommand* commandHolder;
- (void)coolMethod:(CDVInvokedUrlCommand*)command;
- (void)OCR_KTP:(CDVInvokedUrlCommand*)command;
- (void)OCR_NPWP:(CDVInvokedUrlCommand*)command;
- (void)OCR_DEBIT_CARD:(CDVInvokedUrlCommand*)command;
@end

@implementation GidLibVision

- (void)coolMethod:(CDVInvokedUrlCommand*)command{
    self.commandHolder = command;
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)OCR_KTP:(CDVInvokedUrlCommand*)command{
    self.commandHolder = command;
    OCRViewController* nextVc = [[OCRViewController alloc] initWithNibName:@"OCRViewController" bundle:[NSBundle bundleWithIdentifier:@"com.gid.GIDVisionLib"]];
    nextVc.delegate = self;
    nextVc.viewModel = [[OCRViewModel alloc] initWithOcrMode:@"KTP"];
    [self pressentViewController:nextVc];
}

- (void)OCR_NPWP:(CDVInvokedUrlCommand*)command{
    self.commandHolder = command;
    OCRViewController* nextVc = [[OCRViewController alloc] initWithNibName:@"OCRViewController" bundle:[NSBundle bundleWithIdentifier:@"com.gid.GIDVisionLib"]];
    nextVc.delegate = self;
    nextVc.viewModel = [[OCRViewModel alloc] initWithOcrMode:@"NPWP"];
    [self pressentViewController:nextVc];
    
}

- (void)OCR_DEBIT_CARD:(CDVInvokedUrlCommand*)command{
    self.commandHolder = command;
    OCRViewController* nextVc = [[OCRViewController alloc] initWithNibName:@"OCRViewController" bundle:[NSBundle bundleWithIdentifier:@"com.gid.GIDVisionLib"]];
    nextVc.delegate = self;
    nextVc.viewModel = [[OCRViewModel alloc] initWithOcrMode:@"DEBIT_CARD"];
    [self pressentViewController:nextVc];
}
    


#pragma delegate protocol gid vision lib
-(void)onCompletedWithResult:(NSString*)capturedText image:(NSString*)imageBase64 viewController:(UIViewController*) ocrViewController{
    //noverio debug begin
    dispatch_async(dispatch_get_main_queue(), ^{
        [ocrViewController dismissViewControllerAnimated:YES completion:nil];
        CDVPluginResult* pluginResult = nil;
        NSString* jsonResult = [self GetResponseStringJSONWithText:capturedText imageCaptured:imageBase64];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonResult];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.commandHolder.callbackId];
    });
    //noverio debug end
}
    
    
#pragma getRootViewController for hybrid, its getting cordova web view controller
- (UIViewController *)currentTopViewController {
    UIViewController *topVC = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}


- (void)onCancel {
    CDVPluginResult* pluginResult = nil;
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.commandHolder.callbackId];
}


-(NSString*) GetResponseStringJSONWithText : (NSString*) textCaptured imageCaptured : (NSString*)base64String{
    
    NSString* rsJSONModel =@"{\"ErrCode\" : \"[ErrCodeVal]\",\"ErrStatus\" : \"[ErrStatusVal]\",\"ServiceResult\" : {\"platform\" : \"[platformVal]\",\"textResult\" : \"[text]\",\"image\" : \"[imageStringBase64]\"}}";
    
    rsJSONModel = [rsJSONModel stringByReplacingOccurrencesOfString:@"[ErrCodeVal]" withString:@"0000"];
    rsJSONModel = [rsJSONModel stringByReplacingOccurrencesOfString:@"[ErrStatusVal]" withString:@"OK"];
    rsJSONModel = [rsJSONModel stringByReplacingOccurrencesOfString:@"[text]" withString:textCaptured];
    rsJSONModel = [rsJSONModel stringByReplacingOccurrencesOfString:@"[imageStringBase64]" withString:base64String];
    rsJSONModel = [rsJSONModel stringByReplacingOccurrencesOfString:@"[platformVal]" withString:@"ios"];
    return rsJSONModel;
}
- (UIImage *)decodeBase64ToImage:(NSString *)strEncodeData {
    NSData *data = [[NSData alloc]initWithBase64EncodedString:strEncodeData options:NSDataBase64DecodingIgnoreUnknownCharacters];
    return [UIImage imageWithData:data];
}

-(void) pressentViewController:(UIViewController*) destVc{
    [self.commandDelegate runInBackground:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            UIViewController *currentTopVC = [self currentTopViewController];
            dispatch_async(dispatch_get_main_queue(), ^{
                [currentTopVC presentViewController:destVc animated:YES completion:nil];
            });
        });
    }];
}
@end
