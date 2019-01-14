var exec = require('cordova/exec');

exports.OCR_DEBIT_CARD = function (arg0, success, error) {
    exec(success, error, 'GidLibVision', 'OCR_DEBIT_CARD', [arg0]);
};

exports.OCR_NPWP = function (arg0, success, error) {
    exec(success, error, 'GidLibVision', 'OCR_NPWP', [arg0]);
};

exports.OCR_KTP = function (arg0, success, error) {
    exec(success, error, 'GidLibVision', 'OCR_KTP', [arg0]);
};
