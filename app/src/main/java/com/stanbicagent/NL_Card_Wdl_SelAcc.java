package com.stanbicagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DispTextMode;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadType;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.emv.EmvAidPara;
import com.morefun.yapi.emv.EmvCapk;
import com.morefun.yapi.emv.EmvChannelType;
import com.morefun.yapi.emv.EmvHandler;
import com.morefun.yapi.emv.EmvOnlineRequest;
import com.morefun.yapi.emv.EmvProcessResult;
import com.morefun.yapi.emv.EmvTermCfgConstrants;
import com.morefun.yapi.emv.EmvTransDataConstrants;
import com.morefun.yapi.emv.OnEmvProcessListener;
import com.morefun.yapi.engine.DeviceServiceEngine;
import androidx.fragment.app.Fragment;

import com.newland.mtype.module.common.cardreader.K21CardReader;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.util.ISOUtils;

import java.util.ArrayList;
import java.util.List;

import adapter.adapter.DepoMenuAdapt;
import adapter.adapter.OTBList;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import mf_test.CardReaderActivity;
import mf_test.interfaces.OnInputAmountCallBack;
import publib.Utils;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static publib.Utils.byte2string;
import static publib.Utils.getDateTime;
import static publib.Utils.pubByteToHexString;
import static publib.Utils.string2byte;

public class NL_Card_Wdl_SelAcc extends BaseActivity {
    GridView gridView;
    List<OTBList> planetsList = new ArrayList<OTBList>();
    String ptype;
    ListView lv;
    DepoMenuAdapt aAdpt;
    ProgressDialog prgDialog;
    SessionManagement session;
    String sbpam = "0", pramo = "0";
    boolean blsbp = false, blpr = false, blpf = false, bllr = false, blms = false, blmpesa = false, blcash = false;
    ArrayList<String> ds = new ArrayList<String>();
    private EmvTransController controller;
    private EmvModule emvModule;
    private K21CardReader cardReader;
    private static Handler pinHandler;
    String txn_amount;
    String accNo;
    String tran_type;
    static String tid;
    static String mid;
    static String mer_name = "";
    private byte[] pin;
    DeviceServiceEngine mSDKManager;
    EmvHandler mEmvHandler;
    String cardNum = "";
    int pin_iret = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_fbnwdl_sel_acc);
        session = new SessionManagement(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SDKManager.getInstance().bindService(getApplicationContext());
        initEmvListener();
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        gridView = (GridView) findViewById(R.id.gridView1);
        prgDialog.setCancelable(false);

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        txn_amount = pref.getString("txn_amount", "");
        tran_type = pref.getString("trans_type", "");
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");

        //checkInternetConnection2();
        lv = (ListView) findViewById(R.id.lv);
        SetPop();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
                if (mSDKManager == null) {
                    SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
                    return;
                }
                try {
                    mEmvHandler = mSDKManager.getEmvHandler();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Fragment fragment = null;
                if (position == 0) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("acc_ttype", "ACC1");
                    editor.commit();
                    Start_EMV_Transaction();
                } else if (position == 1) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("acc_ttype", "ACC2");
                    editor.commit();
                    Start_EMV_Transaction();
                } else if (position == 2) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("acc_ttype", "ACC3");
                    editor.commit();
                    Start_EMV_Transaction();
                }
                if (fragment != null) {

                }
            }
        });
        session.setString("bankname", null);
        session.setString("bankcode", null);
        session.setString("recanno", null);
    }

    public void Start_EMV_Transaction() {
        onInputAmount(new OnInputAmountCallBack() {
            @Override
            public void onInputAmount(final String amount) {
                if (amount == null || amount.length() == 0) {
                    SecurityLayer.Log("onInputAmount", "input amount fail");
                    return;
                }
                try {
                    CardReaderActivity.getInstance().setEmvHandler(mSDKManager).searchCard(new CardReaderActivity.OnSearchListener() {
                      public void onSearchResult(int retCode, Bundle bundle) {
                            if (ServiceResult.Success == retCode) {
                                emvPBOC(bundle, amount);
                            } else {
                                SecurityLayer.Log("onSearchResult", "searchCard fail: " + retCode);
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private int mChannel;

    public void emvPBOC(Bundle bundle, String amount) {
        int channel = bundle.getInt(ICCSearchResult.CARDOTHER) == IccReaderSlot.ICSlOT1 ? EmvChannelType.FROM_ICC : EmvChannelType.FROM_PICC;
        SecurityLayer.Log("emvPBOC", "emvPBOC channel = " + channel);
        mChannel = channel;
        Bundle inBundle = getInitBundleValue(channel, (byte) 0x50, amount,"0");
        try {
            initTermConfig();
            int ret = mSDKManager.getEmvHandler().emvProcess(inBundle, mOnEmvProcessListener);
            SecurityLayer.Log("emvProcess", "emvProcess ret = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static Bundle getInitBundleValue(int channelType, byte trans9C, String amount, String cashBackAmt) {
        Bundle bundle = new Bundle();
        byte[] transDate = new byte[3];
        byte[] transTime = new byte[3];
        getDateTime(transDate, transTime);
        bundle.putInt(EmvTransDataConstrants.MKEYIDX, 1);
        bundle.putBoolean(EmvTransDataConstrants.ISSUPPORTEC, false);
        bundle.putInt(EmvTransDataConstrants.PROCTYPE, 0);
        bundle.putInt(EmvTransDataConstrants.ISQPBOCFORCEONLINE, 1);
        bundle.putInt(EmvTransDataConstrants.CHANNELTYPE, channelType);
        bundle.putByte(EmvTransDataConstrants.B9C, trans9C);
        bundle.putString(EmvTransDataConstrants.TRANSDATE, pubByteToHexString(transDate));
        bundle.putString(EmvTransDataConstrants.TRANSTIME, pubByteToHexString(transTime));
        bundle.putString(EmvTransDataConstrants.SEQNO, "0001");
        bundle.putString(EmvTransDataConstrants.TRANSAMT, amount);
        bundle.putString(EmvTransDataConstrants.CASHBACKAMT, cashBackAmt);
        bundle.putString(EmvTransDataConstrants.CASHBACKAMT, "0");
        bundle.putString(EmvTransDataConstrants.MERNAME, mer_name);
        bundle.putString(EmvTransDataConstrants.MERID, mid);
        bundle.putString(EmvTransDataConstrants.TERMID, tid);

        //Pin Free Amount for contactLess
        bundle.putString(EmvTransDataConstrants.CONTACTLESS_PIN_FREE_AMT, "200000");
        //TODO For online transactions, the terminal must force to enter the password,Please set true
//        bundle.putBoolean(EmvTransDataConstrants.FORCE_ONLINE_CALL_PIN, false);
        //some additional requirements, It's optional
        bundle.putStringArrayList(EmvTransDataConstrants.TERMINAL_TLVS, setTerminalParamByTlvs());
        SecurityLayer.Log("getInitBundleValue", "getInitBundleValue");
        return bundle;
    }

    public void initTermConfig() throws RemoteException {
        Bundle bundle1 = new Bundle();
        bundle1.putByteArray(EmvTermCfgConstrants.TERMCAP, new byte[]{(byte) 0xE0, (byte) 0x40, (byte) 0xC8});
        bundle1.putByte(EmvTermCfgConstrants.TERMTYPE, (byte) 0x22);
        bundle1.putByteArray(EmvTermCfgConstrants.COUNTRYCODE, new byte[]{(byte) 0x04, (byte) 0x04});
        bundle1.putByteArray(EmvTermCfgConstrants.CURRENCYCODE, new byte[]{(byte) 0x04, (byte) 0x04});
        mSDKManager.getEmvHandler().initTermConfig(bundle1);
        SecurityLayer.Log("initTermConfig", "initTermConfig after ");
    }
    private static ArrayList<String> setTerminalParamByTlvs() {
        //DF811B0130DF8122050101010101DF81180170DF811901185F2A020156
        ArrayList<String> terminalParams = new ArrayList<>();
//        terminalParams.add("DF811B0130");
//        terminalParams.add("DF8122050101010101");
        //master adjust
        terminalParams.add("DF81180170");
        terminalParams.add("DF81190118");
//        terminalParams.add("9F4005F200F0A001");
        return terminalParams;
    }
    public void onInputAmount(final OnInputAmountCallBack callBack) {
        long amount = 0;
        try {
            amount = Long.parseLong(txn_amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SecurityLayer.Log("onInputAmount", "input amount = " + amount);
        if (amount == 0) {
            callBack.onInputAmount(null);
        } else {
            callBack.onInputAmount(String.valueOf(amount * 100));
        }
    }

    public void initEmvListener() {
        SecurityLayer.Log("initEmvListener", "initEmvListener");
        mOnEmvProcessListener = new OnEmvProcessListener.Stub() {
            @Override
            public void onSelApp(List<String> list, boolean b) throws RemoteException {
                SecurityLayer.Log("onSelApp", "onSelApp = " + b);
                mEmvHandler.onSetSelAppResponse(0);
            }

            @Override
            public void onConfirmCardNo(String cardNo) throws RemoteException {
                Log.d("onConfirmCardNo", "onConfirmCardNo = " + cardNo);
                cardNum = cardNo;
                mEmvHandler.onSetConfirmCardNoResponse(true);
            }

            //持卡人输Pin回调
//            @Override
//            public void onCardHolderInputPin(boolean isOnlinePin, int leftTimes) throws RemoteException {
//                SecurityLayer.Log("onCardHolderInputPin", "onCardHolderInputPin = " + isOnlinePin + ", " + leftTimes);
//                //byte[] pinBlock = string2byte("7B74FFF725825835");
//                if (!isOnlinePin && leftTimes > 0) {
//                    //YS1DK
//                    handler.sendEmptyMessage(2);
//                } else {
//                    handler.sendEmptyMessage(1);
//                    //mEmvHandler.onSetCardHolderInputPin(pinBlock);
//                }
//            }

            @Override
            public void onCardHolderInputPin(boolean isOnlinePin, int messageType) throws RemoteException {
                SecurityLayer.Log("onCardHolderInputPin isOnlinePin = " + isOnlinePin + "," + messageType);
                String messagePrompt = "Please enter PIN";
                if (messageType == 3) {
                    messagePrompt = "Please enter PIN";
                } else if (messageType == 2) {
                    messagePrompt = "Enter PIN again";
                } else if (messageType == 1) {
                    messagePrompt = "Enter  laster PIN ";
                }
                //7B 74 FF F7 25 82 58 35
                byte[] pinBlock = string2byte("7B74FFF725825835");
                if (!isOnlinePin) {
                    //YS1DK
                    handler.sendEmptyMessage(2);
                } else {
                    handler.sendEmptyMessage(1);
                    //this is test Pinblock.
//                    mEmvHandler.onSetCardHolderInputPin(pinBlock);
                }
            }

            @Override
            public void onPinPress(byte b) throws RemoteException {
                SecurityLayer.Log("onPinPress", "onPinPress = " + b);
            }

            @Override
            public void onCertVerify(String pszCertNO, String cCertType) throws RemoteException {
                SecurityLayer.Log("onCertVerify", "onCertVerify = " + pszCertNO + ", s1 = " + cCertType);
                mEmvHandler.onSetCertVerifyResponse(true);
            }

            @Override
            public void onOnlineProc(Bundle bundle) throws RemoteException {
                SecurityLayer.Log("onOnlineProc", "onOnlineProc = " + pin_iret + " >>" + bundle);
                if(pin_iret == -1){
                    finish();
                    Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    dealOnline(bundle);
                    Intent intent = new Intent(NL_Card_Wdl_SelAcc.this, NL_Card_Wdl_Agent_PIN.class);
                    startActivity(intent);
                }
                //mSDKManager.getEmvHandler().onSetOnlineProcResponse(ServiceResult.Success, bundle);
            }

            @Override
            public void onContactlessOnlinePlaceCardMode(int i) throws RemoteException {

            }


            //
            @Override
            public void onFinish(int ret, Bundle bundle) throws RemoteException {
                SecurityLayer.Log("onFinish", "onFinish = " + ret);
                if (ret == ServiceResult.Success) {
                    byte[] ecBalance = bundle.getByteArray(EmvProcessResult.ECBALANCE);
                    if (ecBalance != null && ecBalance.length > 0) {
                        //mAlertDialogOnShowListener.showMessage(new String(ecBalance));
                    }
                } else {
                    //mAlertDialogOnShowListener.showMessage(getString(R.string.msg_fail));
                    //mSDKManager.getBeeper().beep(BeepModeConstrants.FAIL);
                }
            }

            @Override
            public void onSetAIDParameter(String s) throws RemoteException {
                EmvAidPara emvAidPara = new EmvAidPara();
                mEmvHandler.onSetAIDParameterResponse(emvAidPara);
            }

            @Override
            public void onSetCAPubkey(String s, int i, int i1) throws RemoteException {
                EmvCapk emvCapk = new EmvCapk();
                mEmvHandler.onSetCAPubkeyResponse(emvCapk);
            }

            @Override
            public void onTRiskManage(String pan, String panSn) throws RemoteException {
                SecurityLayer.Log("onTRiskManage", "onTRiskManage = " + pan + ", " + panSn);
                String resut = "";
                mEmvHandler.onSetTRiskManageResponse(resut);
            }

            @Override
            public void onSelectLanguage(String language) throws RemoteException {
                SecurityLayer.Log("onSelectLanguage", "onSelectLanguage = " + language);
                mEmvHandler.onSetSelectLanguageResponse(0);
            }

            @Override
            public void onSelectAccountType(List<String> accountTypes) throws RemoteException {
                //default 0  "Default"
                int index = 0;
                //accountTypes ->  "Default", "Savings", "Cheque/debit", "Credit"
                try {
                    mEmvHandler.onSetSelectAccountTypeResponse(index);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIssuerVoiceReference(String sPan) throws RemoteException {
                //0 : accept
                //other : refund
                mEmvHandler.onSetIssuerVoiceReferenceResponse(0);
            }
        };
    }

    OnEmvProcessListener.Stub mOnEmvProcessListener;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SecurityLayer.Log("handleMessage", "msg.what = showOnlinePinPanKeyK");
                    try {
                        showOnlinePinPanKeyK(mSDKManager, mEmvHandler, cardNum);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    SecurityLayer.Log("handleMessage", "msg.what = showOffLinePinKey");
                    try {
                        showOffLinePinKey(mSDKManager, mEmvHandler);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    SecurityLayer.Log("handleMessage", "default msg.what = showOnlinePinPanKeyK");
                    try {
                        showOnlinePinPanKeyK(mSDKManager, mEmvHandler, cardNum);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public void dealOnline(final Bundle bundle) throws RemoteException {
        SecurityLayer.Log("dealOnline", "dealOnline");
        String cardSN, expiredDate, track2 = "";
        if (TextUtils.isEmpty(cardNum)) {
            cardNum = getTag("5A");
        }
        StringBuilder builder = new StringBuilder();
        byte[] data = new byte[1024];
        String[] taglist = getTagList().toArray(new String[getTagList().size()]);
        int readlen = mEmvHandler.readEmvData(taglist, data, new Bundle());
        SecurityLayer.Log("readlen", "readlen = " + readlen);
        if (readlen > 0) {
            byte[] ic55Data = Utils.getByteArray(data, 0, readlen);
            builder.append(Utils.byte2string(ic55Data));
        }
        String iccdata = builder.toString();
        SecurityLayer.Log("iccdata", "iccdata = " + iccdata);

        final byte[] pinBytes = bundle.getByteArray(EmvOnlineRequest.PIN);
        SecurityLayer.Log("dealOnline", "EmvOnlineRequest.PIN = " + byte2string(pinBytes));

        cardSN = getTag("5F34");
        track2 = getTag("57");
        expiredDate = getTag("5F24");
        if (cardSN == null) {
            cardSN = "000";
        } else {
            cardSN = ISOUtils.padleft(cardSN, 3, '0');
        }
        if (expiredDate.length() == 6) {
            expiredDate = expiredDate.substring(0, expiredDate.length() - 2);
        }

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cardNo", cardNum);
        editor.putString("cardSN", cardSN);
        editor.putString("track2", track2);
        editor.putString("expiredDate", expiredDate);
        editor.putString("cholder_name", getTag("5F20"));
        editor.putString("card_label", getTag("50"));
        editor.putString("ctyp",  getTag("50"));
        editor.putString("aid",  getTag("4F"));
        editor.putString("tsi",getTag("9B"));
        editor.putString("data55",  iccdata);
        editor.commit();

       /* builder.append("CardNum = " + cardNum + "\n");
        builder.append("CardSn = " + byte2string(cardSn) + "\n");
        builder.append("PinBlock = " + byte2string(pinBytes) + "\n");
        builder.append("IC data \n");
        builder.append("9F27=" + getTag("9F27") + "\n");
        builder.append("5A=" + getTag("5A") + "\n");
        builder.append("50=" + getTag("50") + "\n");
        builder.append("9F10=" + getTag("9F10") + "\n");
        builder.append("9F37=" + getTag("9F37") + "\n");
        builder.append("9F36=" + getTag("9F36") + "\n");
        builder.append("9F36=" + getTag("9F36") + "\n");
        builder.append("95=" + getTag("95") + "\n");
        builder.append("9A=" + getTag("9A") + "\n");
        builder.append("9C=" + getTag("9C") + "\n");
        builder.append("9F02=" + getTag("9F02") + "\n");
        builder.append("5F2A=" + getTag("5F2A") + "\n");
        builder.append("82=" + getTag("82") + "\n");
        builder.append("9F1A=" + getTag("9F1A") + "\n");
        builder.append("9F03=" + getTag("9F03") + "\n");
        builder.append("9F33=" + getTag("9F33") + "\n");
        builder.append("9F35=" + getTag("9F35") + "\n");
        builder.append("9F1E=" + getTag("9F1E") + "\n");
        builder.append("84=" + getTag("84") + "\n");
        builder.append("9F09=" + getTag("9F09") + "\n");
        builder.append("9F41=" + getTag("9F41") + "\n");
        builder.append("9F63=" + getTag("9F63") + "\n");
        builder.append("9F26=" + getTag("9F26") + "\n");
        SecurityLayer.Log("dealOnline", "dealOnline >> " + builder.toString());*/
    }

//    public void dealOnline(final Bundle bundle) throws RemoteException {
//        SecurityLayer.Log("dealOnline", "dealOnline");
//        String cardSN, expiredDate, track2 = "";
//        if (TextUtils.isEmpty(cardNum)) {
//            cardNum = getTag("5A");
//        }
//
//        StringBuilder builder = new StringBuilder();
//        byte[] data = new byte[1024];
//        String[] taglist = getTagList().toArray(new String[getTagList().size()]);
//        int readlen = mEmvHandler.readKernelData(taglist, data);
//        if (readlen > 0) {
//            byte[] ic55Data = Utils.getByteArray(data, 0, readlen);
//            builder.append(Utils.byte2string(ic55Data));
//        }
//        String iccdata = builder.toString();
//        SecurityLayer.Log("iccdata", "iccdata = " + iccdata);
//
//        final byte[] pinBytes = bundle.getByteArray(EmvOnlineRequest.PIN);
//        SecurityLayer.Log("dealOnline", "EmvOnlineRequest.PIN = " + byte2string(pinBytes));
//        final byte[] cardSn = bundle.getByteArray(EmvOnlineRequest.CARDSN);
//        //StringBuilder builder = new StringBuilder();
//
//        cardSN = getTag("5F34");
//        track2 = getTag("57");
//        expiredDate = getTag("5F24");
//        if (cardSN == null) {
//            cardSN = "000";
//        } else {
//            cardSN = ISOUtils.padleft(cardSN, 3, '0');
//        }
//        if (expiredDate.length() == 6) {
//            expiredDate = expiredDate.substring(0, expiredDate.length() - 2);
//        }
//
//        SharedPreferences pref = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("cardNo", cardNum);
//        editor.putString("cardSN", cardSN);
//        editor.putString("track2", track2);
//        editor.putString("expiredDate", expiredDate);
//        editor.putString("cholder_name", getTag("5F20"));
//        editor.putString("card_label", getTag("50"));
//        editor.putString("tvr",  getTag("9F06"));
//        editor.putString("aid",  getTag("4F"));
//        editor.putString("data55",  iccdata);
//        editor.commit();
//
//        builder.append("CardNum = " + cardNum + "\n");
//        builder.append("CardSn = " + byte2string(cardSn) + "\n");
//        builder.append("PinBlock = " + byte2string(pinBytes) + "\n");
//        builder.append("IC data \n");
//        builder.append("9F27=" + getTag("9F27") + "\n");
//        builder.append("5A=" + getTag("5A") + "\n");
//        builder.append("50=" + getTag("50") + "\n");
//        builder.append("9F10=" + getTag("9F10") + "\n");
//        builder.append("9F37=" + getTag("9F37") + "\n");
//        builder.append("9F36=" + getTag("9F36") + "\n");
//        builder.append("9F36=" + getTag("9F36") + "\n");
//        builder.append("95=" + getTag("95") + "\n");
//        builder.append("9A=" + getTag("9A") + "\n");
//        builder.append("9C=" + getTag("9C") + "\n");
//        builder.append("9F02=" + getTag("9F02") + "\n");
//        builder.append("5F2A=" + getTag("5F2A") + "\n");
//        builder.append("82=" + getTag("82") + "\n");
//        builder.append("9F1A=" + getTag("9F1A") + "\n");
//        builder.append("9F03=" + getTag("9F03") + "\n");
//        builder.append("9F33=" + getTag("9F33") + "\n");
//        builder.append("9F35=" + getTag("9F35") + "\n");
//        builder.append("9F1E=" + getTag("9F1E") + "\n");
//        builder.append("84=" + getTag("84") + "\n");
//        builder.append("9F09=" + getTag("9F09") + "\n");
//        builder.append("9F41=" + getTag("9F41") + "\n");
//        builder.append("9F63=" + getTag("9F63") + "\n");
//        builder.append("9F26=" + getTag("9F26") + "\n");
//        //SecurityLayer.Log("dealOnline", "dealOnline >> " + builder.toString());
//    }

    public List<String> getTagList() {
        List<String> tagList = new ArrayList<>();
        tagList.add("50");
        //tagList.add("5A");
        tagList.add("4F");
        tagList.add("57");
        tagList.add("95");
        //tagList.add("81");
        //tagList.add("84");
        tagList.add("91");
        //tagList.add("99");
        tagList.add("9A");
        tagList.add("9B");
        tagList.add("9C");
        tagList.add("5F20");
        tagList.add("5F24");
        tagList.add("5F25");
        tagList.add("5F2A");
        //tagList.add("5F28");
        tagList.add("5F34");
        tagList.add("82");
        //tagList.add("8E");
        tagList.add("9F01");
        tagList.add("9F02");
        tagList.add("9F03");
        //tagList.add("9F0D");
        //tagList.add("9F0F");
        //tagList.add("9F0E");
        tagList.add("9F06");
        tagList.add("9F07");
        tagList.add("9F10");
        tagList.add("9F15");
        tagList.add("9F16");
        tagList.add("9F19");
        tagList.add("9F1A");
        tagList.add("9F1C");
        //tagList.add("9F1E");
        tagList.add("9F21");
        tagList.add("9F24");
        tagList.add("9F27");
        tagList.add("9F33");
        tagList.add("9F34");
        tagList.add("9F35");
        tagList.add("9F36");
        tagList.add("9F37");
        tagList.add("9F09");
        tagList.add("9F40");
        //tagList.add("9F41");
        tagList.add("9F5D");
        tagList.add("9F63");
        tagList.add("9F26");
        return tagList;
    }


    private String getTag(String tag) {
        byte[] Tag = string2byte(tag);
        try {
            byte[] tlvs = mEmvHandler.getTlvs(Tag, 0, new Bundle());
            return byte2string(tlvs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void showOnlinePinPanKeyK(DeviceServiceEngine mSDKManager, final EmvHandler mEmvHandler, String pan) throws RemoteException {
        mSDKManager.getPinPad().initPinPad(PinPadType.EXTERNAL);
        byte[] panBlock = pan.getBytes();
        SecurityLayer.Log("showOnlinePinPanKeyK", "showOnlinePinPanKeyK pan = " + pan);
        int minLength = 4;
        int maxLength = 4;
        mSDKManager.getPinPad().setSupportPinLen(new int[]{minLength, maxLength});
        int pinResult = mSDKManager.getPinPad().initPinPad(PinPadType.INTERNAL);
        if (checkPinPadNeedAllowPermission(mEmvHandler, pinResult)) {
            SecurityLayer.Log("checkPinPadNeedAllowPermission", "checkPinPadNeedAllowPermission =  true");
            return;
        }

        pinResult = mSDKManager.getPinPad().inputOnlinePin(new Bundle(), panBlock, 0, PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
            @Override
            public void onInputResult(int i, byte[] bytes, String s) throws RemoteException {
                SecurityLayer.Log("onInputResult", "onInputResult =  " + byte2string(bytes));

                boolean isByPass = "000000000000".equals(byte2string(bytes));
                if (mEmvHandler != null) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("data_send", byte2string(bytes));
                    editor.commit();
//                    mEmvHandler.onSetCardHolderInputPin(Utils.getByteArray(bytes, 0, 8));
                    mEmvHandler.onSetCardHolderInputPin(isByPass ? new byte[0] : Utils.getByteArray(bytes, 0, 8));
                } else {
                    SecurityLayer.Log("onInputResult", "online pin : " + byte2string(bytes));
                }
            }

            @Override
            public void onSendKey(byte keyCode) throws RemoteException {
                SecurityLayer.Log("onSendKey", "onSendKey keyCode = " + keyCode);
                if (keyCode == (byte) ServiceResult.PinPad_Input_Cancel) {
                    if (mEmvHandler != null) {
                        mEmvHandler.onSetCardHolderInputPin(null);
                    }
                    SecurityLayer.Log("onSendKey", "onSendKey >> Pin Pad is cancel");
                    pin_iret = -1;
                }
            }
        });
        checkPinPadNeedAllowPermission(mEmvHandler, pinResult);
    }

    public void showOffLinePinKey(DeviceServiceEngine mSDKManager, final EmvHandler mEmvHandler) throws RemoteException {
        SecurityLayer.Log("showOffLinePinKey", "showOffLinePinKey");
        int textMode = DispTextMode.PASSWORD;
        //int textMode = DispTextMode.PLAINTEXT;
        int pinResult = mSDKManager.getPinPad().initPinPad(PinPadType.INTERNAL);
        if (checkPinPadNeedAllowPermission(mEmvHandler, pinResult)) {
            SecurityLayer.Log("checkPinPadNeedAllowPermission", "checkPinPadNeedAllowPermission =  true");
            return;
        }
        pinResult = mSDKManager.getPinPad().inputText(new Bundle(), new OnPinPadInputListener.Stub() {
            @Override
            public void onInputResult(int i, byte[] bytes, String s) throws RemoteException {
                SecurityLayer.Log("showOffLinePinKey", "onInputResult bytes = " + new String(bytes));
                if (mEmvHandler != null) {
                    mEmvHandler.onSetCardHolderInputPin(bytes);
                } else {
                    //alertDialogOnShowListener.showMessage("offline pin : " + new String(bytes));
                }
            }

            @Override
            public void onSendKey(byte keyCode) throws RemoteException {
                SecurityLayer.Log("onSendKey", "onSendKey keyCode = " + keyCode);
                if (keyCode == (byte) ServiceResult.PinPad_Input_Cancel) {
                    if (mEmvHandler != null) {
                        mEmvHandler.onSetCardHolderInputPin(null);
                    }
                    pin_iret = -1;
                }
            }
        }, textMode);
        checkPinPadNeedAllowPermission(mEmvHandler, pinResult);
    }



    private boolean checkPinPadNeedAllowPermission(EmvHandler mEmvHandler, int pinResult) throws
            RemoteException {
        if (pinResult == ServiceResult.PinPad_Base_Error) {
            SecurityLayer.Log("checkPinPadNeedAllowPermission", "pinResult = " + pinResult);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            SecurityLayer.Log("checkPinPadNeedAllowPermission", " = Please allow YSDK App permission");
            if (mEmvHandler != null) {
                mEmvHandler.onSetCardHolderInputPin(null);
            }
            return true;
        }
        return false;
    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(NL_Card_Wdl_SelAcc.this)
                .title(title)
                .content(message)
                .negativeText("Dismiss")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), FMobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SetPop() {
        planetsList.clear();
        planetsList.add(new OTBList("SAVINGS", "057"));
        planetsList.add(new OTBList("CURRENT", "058"));
        planetsList.add(new OTBList("CREDIT", "059"));
        aAdpt = new DepoMenuAdapt(planetsList, this);
        lv.setAdapter(aAdpt);
    }
}
