package rest;

import model.BalanceInquiry;
import model.ChangePinModel;
import model.Chat;
import model.DeviceActivation;
import model.DoAirtimeTxn;
import model.DoBillPayment;
import model.FMoniWallet;
import model.GetAgentId;
import model.GetAirtimeBillers;
import model.GetBanks;
import model.GetBillers;
import model.GetFee;
import model.GetPerf;
import model.GetSecurity;
import model.GetServices;
import model.GetWallets;
import model.InterBank;
import model.IntraBank;
import model.Login;
import model.Ministat;
import model.NameEnquiry;
import model.SaveChat;
import model.UpdateAd;
import model.UploadPic;
import model.WithdrawalConfirmOTP;
import model.WithdrawalSendOTP;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiInterface {
    @POST("transfer/nameenq.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{bankCode}/{beneficiaryAccount}")
    Call<NameEnquiry> getAccountDetails(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("bankCode") String bankCode, @Path("beneficiaryAccount") String beneficiaryAccount);

    @POST("login/login.action/{channel}/{userId}/{pin}/{mobile}")
    Call<Login> getLoginResponse(@Path("channel") String channel, @Path("userId") String userId, @Path("pin") String pin, @Path("mobile") String mobile);

    @POST("transfer/intrabank.action/{channel}/{makerId}/{merchantId}/{mobileNumber}/{serviceId}/{amount}/{beneficiaryAccount}/{beneficiaryName}/{narration}/{dateTime}")
    Call<IntraBank> getIntraBankResp(@Path("channel") String channel, @Path("makerId") String makerId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("serviceId") String serviceId, @Path("amount") String amount, @Path("beneficiaryAccount") String beneficiaryAccount, @Path("beneficiaryName") String beneficiaryName, @Path("narration") String narration, @Path("dateTime") String dateTime);

    @POST("transfer/getbanks.action")
    Call<GetBanks> getBanks();

    @POST("transfer/getwallets.action")
    Call<GetWallets> getWallets();


    @POST("transfer/interbank.action/{channel}/{makerId}/{merchantId}/{mobileNumber}/{serviceId}/{amount}/{bankCode}/{beneficiaryAccount}/{beneficiaryName}/{narration}/{dateTime}")
    Call<InterBank> getInterBankResp(@Path("channel") String channel, @Path("makerId") String makerId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("serviceId") String serviceId, @Path("amount") String amount,@Path("bankCode") String bankCode, @Path("beneficiaryAccount") String beneficiaryAccount, @Path("beneficiaryName") String beneficiaryName, @Path("narration") String narration, @Path("dateTime") String dateTime);


    @POST("core/balenquirey.action/{channel}/{userId}/{merchantId}/{mobileNumber}")
    Call<BalanceInquiry> getBalInq(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber);

    @POST("core/stmt.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{fromDate}/{endDate}")
    Call<Ministat> getMiniStat(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("fromDate") String fromDate,@Path("endDate") String endDate);

    @POST("wallet/depwallet.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{amount}/{walletCode}/{walletMobileNumber}/{narration}")
    Call<FMoniWallet> getWalletInfo(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("amount") String amount, @Path("walletCode") String walletCode,@Path("walletMobileNumber") String walletMobileNumber, @Path("narration") String narration);


    @POST("billpayment/services.action/{channel}/{userId}/{merchantId}/{mobileNumber}")
    Call<GetServices> getServices(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber);

    @POST("billpayment/getbillers.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{serviceId}")
    Call<GetBillers> getBillers(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("serviceId") String serviceId);

    @POST("billpayment/dobillpayment.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{id}/{sid}/{amount}/{packId}/{customerMobile}/{customerEmail}/{customerId}/{pin}")
    Call<DoBillPayment> getBillPay(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("id") String id, @Path("sid") String sid, @Path("amount") String amount, @Path("packId") String packId,@Path("customerMobile") String customerMobile, @Path("customerEmail") String customerEmail, @Path("customerId") String customerId,@Path("pin") String pin);

    @POST("billpayment/mobileRecharge.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{id}/{sid}/{amount}/{packId}/{customerMobile}/{customerEmail}/{customerId}/{pin}")
    Call<DoAirtimeTxn> getAirtimePay(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("id") String id, @Path("sid") String sid, @Path("amount") String amount, @Path("packId") String packId, @Path("customerMobile") String customerMobile, @Path("customerEmail") String customerEmail, @Path("customerId") String customerId,@Path("pin") String pin);


    @POST("billpayment/MMObillers.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{serviceId}")
    Call<GetAirtimeBillers> getAirBillPay(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("serviceId") String serviceId);


    @POST("withdrawal/cashbyaccountinit.action/{channel}/{makerId}/{merchantId}/{mobileNumber}/{customerAccountNumber}/{customerName}")
    Call<WithdrawalSendOTP> getWithOTP(@Path("channel") String channel, @Path("makerId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("customerAccountNumber") String customerAccountNumber, @Path("customerName") String customerName);


    @POST("withdrawal/cashbyaccountconfirm.action/{channel}/{makerId}/{merchantId}/{mobileNumber}/{amount}/{txnRef}/{customerAccountNumber}/{customerName}/{narration}/{otp}/{pin}")
    Call<WithdrawalConfirmOTP> getConfirmOTP(@Path("channel") String channel, @Path("makerId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("amount") String amount, @Path("txnRef") String txnRef, @Path("customerAccountNumber") String customerAccountNumber, @Path("customerName") String customerName,@Path("narration") String narration, @Path("otp") String otp, @Path("pin") String pin);

    @POST("reg/devReg.action/{channel}/{userId}/{mobileNumber}/{pin}/{secans1}/{secans2}/{secans3}/{macAddr}/{deviceIp}/{imeiNo}/{serialNo}/{version}/{deviceType}/{gcmId}")
    Call<DeviceActivation> getDevReg(@Path("channel") String channel, @Path("userId") String userId, @Path("mobileNumber") String mobileNumber, @Path("pin") String pin, @Path("secans1") String secans1, @Path("secans2") String secans2, @Path("secans3") String secans3, @Path("macAddr") String macAddr, @Path("deviceIp") String deviceIp, @Path("imeiNo") String imeiNo, @Path("serialNo") String serialNo, @Path("version") String version, @Path("deviceType") String deviceType, @Path("gcmId") String gcmId);


    @POST("login/changepin.action/{channel}/{userId}/{merchantId}/{mobileNo}/{oldPin}/{newPin}")
    Call<ChangePinModel> getChngPin(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNo") String mobileNo, @Path("oldPin") String oldPin, @Path("newPin") String newPin);



    @POST("report/genrpt.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{rpttype}/{fromDate}/{endDate}")
    Call<GetPerf> getPerfData(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber,@Path("rpttype") String rpttype, @Path("fromDate") String fromDate,@Path("endDate") String endDate);




   // http://localhost:9399/agencyapi/app/adverts/ads.action/1/CEVA/PAND00000000019493818389
    @POST("adverts/ads.action/{channel}/{userId}/{merchantId}/{mobileNumber}")
    Call<GetAgentId> GetAgId(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber);


    @POST("adverts/update.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{id}")
    Call<UpdateAd> UpdateAd(@Path("channel") String channel, @Path("id") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber, @Path("id") String id);


    @POST("chat/load.action/{channel}/{userId}/{merchantId}/{mobileNumber}")
    Call<Chat> getChat(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber);

    @POST("chat/save.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{message}")
    Call<SaveChat> savechat(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("mobileNumber") String mobileNumber,@Path("message") String message);

    @POST("fee/getfee.action/{channel}/{userId}/{merchantId}/{serviceCode}/{amount}")
    Call<GetFee> getFee(@Path("channel") String channel, @Path("userId") String userId, @Path("merchantId") String merchantId, @Path("serviceCode") String serviceCode, @Path("amount") String amount);

    @POST("security")
    Call<GetSecurity> getSecurity(@Path("secparam") String secparam);

    @POST("{string}")
    Call<String> setGenericRequestRaw(@Path(value="string",encoded=true) String string);


}