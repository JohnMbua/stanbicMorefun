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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.DispTextMode;
import com.morefun.yapi.device.pinpad.OnPinPadInputListener;
import com.morefun.yapi.device.pinpad.PinAlgorithmMode;
import com.morefun.yapi.device.pinpad.PinPadConstrants;
import com.morefun.yapi.device.pinpad.PinPadType;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
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
import com.newland.mtype.util.ISOUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import mf_test.CardReaderActivity;
import mf_test.interfaces.OnInputAmountCallBack;
import publib.Utils;
import security.SecurityLayer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static publib.Utils.byte2string;
import static publib.Utils.getDateTime;
import static publib.Utils.pubByteToHexString;
import static publib.Utils.string2byte;

public class NL_Card_Wdl_InsCard extends BaseActivity implements View.OnClickListener {
    ProgressDialog pDialog;
    Button btnsub, btn_test;
    TextView txtinscard;
    String txn_amount = "";
    ProgressDialog prgDialog;
    DeviceServiceEngine mSDKManager;
    int check = 0,pnTms;
    String tran_type;
    static String tid;
    static String mid;
    static String mer_name = "";
    boolean isLightLed = false;

    String cardNum = "";
    int pin_iret = 0;

    EmvHandler mEmvHandler;
    OnEmvProcessListener.Stub mOnEmvProcessListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nl_activity_card_transfer_insert_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SDKManager.getInstance().bindService(getApplicationContext());
        initEmvListener();
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        txn_amount = pref.getString("txn_amount", "");
        tran_type = pref.getString("trans_type", "");
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("acc_ttype", "ACC1");
        editor.commit();

        /*pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);*/
        txtinscard = (TextView)findViewById(R.id.txtinscard);
        btnsub = (Button) findViewById(R.id.button2);
        btnsub.setOnClickListener(this);
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
            return;
        }
        btn_test = (Button) findViewById(R.id.button3);
        btn_test.setVisibility(View.GONE);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Load_PK();
            }
        });
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


//        Load_PK();
//        Load_AID();
        Test_Card();


        Fragment fragment = null;
    }

    public void Test_Card(){
        SecurityLayer.Log("Check_Card", "Check_Card");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final IccCardReader iccCardReader = mSDKManager.getIccCardReader(IccReaderSlot.ICSlOT1);
                    OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                        @Override
                        public void onSearchResult(final int retCode, Bundle bundle) throws RemoteException {
                            String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                            SecurityLayer.Log("onSearchResult", "retCode= " + retCode + "," + cardType);
                            iccCardReader.stopSearch();
                            check = retCode;
                            getMainLooper().prepare();
                            After_Card_Check();
                            getMainLooper().loop();
                        }
                    };
                    iccCardReader.searchCard(listener, 15, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void After_Card_Check() {
        if (check == 0) {
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Reading Card Details" + "\n" + "Please Wait....");
            prgDialog.setCancelable(false);
            prgDialog.show();

            Start_EMV_Transaction();

        } else {
            finish();
            Intent intent = new Intent(NL_Card_Wdl_InsCard.this, FMobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    public void Load_PK() {
        String[] keys = new String[]{
                "9F0605A0000000039F220109DF05083230313631323331DF060101DF070101DF0281F89D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41DF040103DF03141FF80A40173F52D7D27E0F26A146A1C8CCB29046",
                "9F0605A0000000039F220107DF05083230313231323331DF060101DF070101DF028190A89F25A56FA6DA258C8CA8B40427D927B4A1EB4D7EA326BBB12F97DED70AE5E4480FC9C5E8A972177110A1CC318D06D2F8F5C4844AC5FA79A4DC470BB11ED635699C17081B90F1B984F12E92C1C529276D8AF8EC7F28492097D8CD5BECEA16FE4088F6CFAB4A1B42328A1B996F9278B0B7E3311CA5EF856C2F888474B83612A82E4E00D0CD4069A6783140433D50725FDF040103DF0314B4BC56CC4E88324932CBC643D6898F6FE593B172",
                "9F0605A0000000039F220108DF05083230313431323331DF060101DF070101DF0281B0D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0BDF040103DF031420D213126955DE205ADC2FD2822BD22DE21CF9A8",
                "9F0605A0000000039F220101DF05083230313431323331DF060101DF070101DF028180C696034213D7D8546984579D1D0F0EA519CFF8DEFFC429354CF3A871A6F7183F1228DA5C7470C055387100CB935A712C4E2864DF5D64BA93FE7E63E71F25B1E5F5298575EBE1C63AA617706917911DC2A75AC28B251C7EF40F2365912490B939BCA2124A30A28F54402C34AECA331AB67E1E79B285DD5771B5D9FF79EA630B75DF040103DF0314D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CC",
                "9F0605A0000000039F220103DF05083230313431323331DF060101DF070101DF0270B3E5E667506C47CAAFB12A2633819350846697DD65A796E5CE77C57C626A66F70BB630911612AD2832909B8062291BECA46CD33B66A6F9C9D48CED8B4FC8561C8A1D8FB15862C9EB60178DEA2BE1F82236FFCFF4F3843C272179DCDD384D541053DA6A6A0D3CE48FDC2DC4E3E0EEE15FDF040103DF0314FE70AB3B4D5A1B9924228ADF8027C758483A8B7E",
                "9F0605A0000003719F220105DF05083230313631323331DF060101DF070101DF0281B0B036A8CAE0593A480976BFE84F8A67759E52B3D9F4A68CCC37FE720E594E5694CD1AE20E1B120D7A18FA5C70E044D3B12E932C9BBD9FDEA4BE11071EF8CA3AF48FF2B5DDB307FC752C5C73F5F274D4238A92B4FCE66FC93DA18E6C1CC1AA3CFAFCB071B67DAACE96D9314DB494982F5C967F698A05E1A8A69DA931B8E566270F04EAB575F5967104118E4F12ABFF9DEC92379CD955A10675282FE1B60CAD13F9BB80C272A40B6A344EA699FB9EFA6867DF040103DF0314676822D335AB0D2C3848418CB546DF7B6A6C32C0",
                "9F0605A0000003719F220104DF05083230313631323331DF060101DF070101DF028190D13CD5E1B921E4E0F0D40E2DE14CCE73E3A34ED2DCFA826531D8195641091E37C8474D19B686E8243F089A69F7B18D2D34CB4824F228F7750F96D1EFBDFF881F259A8C04DE64915A3A3D7CB846135F4083C93CDE755BC808886F600542DFF085558D5EA7F45CB15EC835064AA856D602A0A44CD021F54CF8EC0CC680B54B3665ABE74A7C43D02897FF84BB4CB98BC91DDF040103DF03148B36A3E3D814CE6C6EBEAAF27674BB7BC67275B1",
                "9F0605A0000000049F220100DF05083230313431323331DF060101DF070101DF0281A09C6BE5ADB10B4BE3DCE2099B4B210672B89656EBA091204F613ECC623BEDC9C6D77B660E8BAEEA7F7CE30F1B153879A4E36459343D1FE47ACDBD41FCD710030C2BA1D9461597982C6E1BDD08554B726F5EFF7913CE59E79E357295C321E26D0B8BE270A9442345C753E2AA2ACFC9D30850602FE6CAC00C6DDF6B8D9D9B4879B2826B042A07F0E5AE526A3D3C4D22C72B9EAA52EED8893866F866387AC05A1399DF040103DF0314EC0A59D35D19F031E9E8CBEC56DB80E22B1DE130",
                "9F0605A0000000049F220101DF05083230313431323331DF060101DF070101DF028180C696034213D7D8546984579D1D0F0EA519CFF8DEFFC429354CF3A871A6F7183F1228DA5C7470C055387100CB935A712C4E2864DF5D64BA93FE7E63E71F25B1E5F5298575EBE1C63AA617706917911DC2A75AC28B251C7EF40F2365912490B939BCA2124A30A28F54402C34AECA331AB67E1E79B285DD5771B5D9FF79EA630B75DF040103DF03148C05A64127485B923C94B63D264AF0BF85CB45D9",
                "9F0605A0000000049F220102DF05083230313431323331DF060101DF070101DF0270CF4264E1702D34CA897D1F9B66C5D63691EACC612C8F147116BB22D0C463495BD5BA70FB153848895220B8ADEEC3E7BAB31EA22C1DC9972FA027D54265BEBF0AE3A23A8A09187F21C856607B98BDA6FC908116816C502B3E58A145254EEFEE2A3335110224028B67809DCB8058E24895DF040103DF0314AF1CC1FD1C1BC9BCA07E78DA6CBA2163F169CBB7",
                "9F0605A0000003339F220102DF050420211231DF060101DF070101DF028190A3767ABD1B6AA69D7F3FBF28C092DE9ED1E658BA5F0909AF7A1CCD907373B7210FDEB16287BA8E78E1529F443976FD27F991EC67D95E5F4E96B127CAB2396A94D6E45CDA44CA4C4867570D6B07542F8D4BF9FF97975DB9891515E66F525D2B3CBEB6D662BFB6C3F338E93B02142BFC44173A3764C56AADD202075B26DC2F9F7D7AE74BD7D00FD05EE430032663D27A57DF040103DF031403BB335A8549A03B87AB089D006F60852E4B8060",
                "9F0605A0000003339F220103DF050420221231DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26",
                "9F0605A0000003339F220104DF050420221231DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5"
        };
        try {
            for (int j = 0; j < keys.length; j++) {
                String key = keys[j];
                int ret = mSDKManager.getEmvHandler().addCAPKParam(string2byte(key));
                //SecurityLayer.Log("ICPublicKeyManage", "result = " + (ret == ServiceResult.Success));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void Load_AID() {
        String[] keys = new String[]{
                "9F0608A000000333010101DF2006000000100000DF010100DF14039F3704DF170100DF1801019F09020020DF1205FCF8E4F8809F1B0400000000DF2106000000010000DF160100DF150400000000DF1105FCF0E40800DF19060000000500009F7B06000000080000DF13050010000000",
                "9F0608A000000333010100DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101",
                "9F0608A000000333010102DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101",
                "9F0608A000000333010103DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF180101"
        };                       //08A000000333010101
        try {
            for (int j = 0; j < keys.length; j++) {
                String key = keys[j];
                int ret = mSDKManager.getEmvHandler().addAidParam(string2byte(key));
                SecurityLayer.Log("ICAidManage", "ICAidManage >> result = " + (ret == ServiceResult.Success));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button2) {
            //cardReader.cancelCardRead();
            finish();
            Intent intent = new Intent(NL_Card_Wdl_InsCard.this, FMobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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
                        @Override
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

    public void emvPBOC(Bundle bundle, String amount) {
        int channel = bundle.getInt(ICCSearchResult.CARDOTHER) == IccReaderSlot.ICSlOT1 ? EmvChannelType.FROM_ICC : EmvChannelType.FROM_PICC;
        SecurityLayer.Log("emvPBOC", "emvPBOC channel = " + channel);
        Bundle inBundle = getInitBundleValue(channel, (byte) 0x50, amount);
        try {
            initTermConfig();
            int ret = mSDKManager.getEmvHandler().emvProcess(inBundle, mOnEmvProcessListener);
            SecurityLayer.Log("emvProcess", "emvProcess ret = " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static Bundle getInitBundleValue(int channelType, byte trans9C, String amount) {
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
    /**
     * It's optional
     *
     * @return
     */
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
    public void initTermConfig() throws RemoteException {
        Bundle bundle1 = new Bundle();
        bundle1.putByteArray(EmvTermCfgConstrants.TERMCAP, new byte[]{(byte) 0xE0, (byte) 0x40, (byte) 0xC8});
        bundle1.putByte(EmvTermCfgConstrants.TERMTYPE, (byte) 0x22);
        bundle1.putByteArray(EmvTermCfgConstrants.COUNTRYCODE, new byte[]{(byte) 0x04, (byte) 0x04});
        bundle1.putByteArray(EmvTermCfgConstrants.CURRENCYCODE, new byte[]{(byte) 0x04, (byte) 0x04});
        mSDKManager.getEmvHandler().initTermConfig(bundle1);
        SecurityLayer.Log("initTermConfig", "initTermConfig after ");
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
            if((tran_type.compareTo("BEQ")==0)|| (tran_type.compareTo("MIN")==0)) {
                callBack.onInputAmount(String.valueOf(amount * 100));
            }else{
                callBack.onInputAmount(null);
            }
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
            @Override
            public void onCardHolderInputPin(boolean isOnlinePin, int leftTimes) throws RemoteException {
                SecurityLayer.Log("onCardHolderInputPin", "onCardHolderInputPin = " + isOnlinePin + ", " + leftTimes);
                 pnTms=leftTimes;
                //byte[] pinBlock = string2byte("7B74FFF725825835");
                if (!isOnlinePin && leftTimes > 0) {
                    //YS1DK
                    handler.sendEmptyMessage(2);
                } else {
                    handler.sendEmptyMessage(1);
                    //mEmvHandler.onSetCardHolderInputPin(pinBlock);
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
                    if(tran_type.equals("TRANS")){
                        Intent intent = new Intent(NL_Card_Wdl_InsCard.this, NL_CEVA_Card_Trans_AgentPIN.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(NL_Card_Wdl_InsCard.this, NL_Card_Wdl_Agent_PIN.class);
                        startActivity(intent);
                    }
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
            byte[] ic55Data = publib.Utils.getByteArray(data, 0, readlen);
            builder.append(publib.Utils.byte2string(ic55Data));
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
        editor.putString("tsi", getTag("9B"));
        editor.putString("data55",  iccdata);
        editor.commit();


    }



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
    public void showOnlinePinPanKeyK(DeviceServiceEngine mSDKManager,
                                     final EmvHandler mEmvHandler, String pan) throws RemoteException {
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

        Bundle bundle = new Bundle();
        bundle.putBoolean(PinPadConstrants.IS_SHOW_PASSWORD_BOX, true);
        bundle.putBoolean(PinPadConstrants.IS_SHOW_TITLE_HEAD, true);
        bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input online Pin\n");
        pinResult = mSDKManager.getPinPad().inputOnlinePin(bundle, panBlock, 0, PinAlgorithmMode.ISO9564FMT1, new OnPinPadInputListener.Stub() {
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
                    finish();
                }
            }
        });
        checkPinPadNeedAllowPermission(mEmvHandler, pinResult);
    }

    public void showOffLinePinKey(DeviceServiceEngine mSDKManager, final EmvHandler mEmvHandler) throws RemoteException {
        int textMode = DispTextMode.PASSWORD;
//        int textMode = DispTextMode.PLAINTEXT;
        //check permission for PinPad dialog.
        int pinResult = mSDKManager.getPinPad().initPinPad(PinPadType.INTERNAL);
        if (checkPinPadNeedAllowPermission(mEmvHandler, pinResult)) {
            SecurityLayer.Log("checkPinPadNeedAllowPermission =  true");
            return;
        }
        //setSupportPinLen is Config set Pin Len . if you need input the  12 Len Pin .. you can set minLength 12 and maxLength 12.
        int minLength = 4;
        int maxLength = 4;
        mSDKManager.getPinPad().setSupportPinLen(new int[]{minLength, maxLength});


//        bundle.putString(PinPadConstrants.COMMON_TYPEFACE_PATH , path);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PinPadConstrants.IS_SHOW_PASSWORD_BOX, true);
        bundle.putBoolean(PinPadConstrants.IS_SHOW_TITLE_HEAD, true);
        if (pnTms==3){
            bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Please input offline Pin\n");
        }else if(pnTms==2){
            bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Second attempt offline Pin\n");
        }else{
            bundle.putString(PinPadConstrants.TITLE_HEAD_CONTENT, "Last attempt offline Pin\n");
        }
        pinResult = mSDKManager.getPinPad().inputText(bundle, new OnPinPadInputListener.Stub() {
            @Override
            public void onInputResult(int ret, byte[] pin, String ksn) throws RemoteException {
                SecurityLayer.Log( "ret= " + ret + ",bytes = " + pin);
                if (mEmvHandler != null && ret == ServiceResult.Success) {
                    mEmvHandler.onSetCardHolderInputPin(pin);
                } else {
                    //alertDialogOnShowListener.showMessage("offline pin : " + new String(pin));
                }
            }

            @Override
            public void onSendKey(byte keyCode) throws RemoteException {
                SecurityLayer.Log( "keyCode = " + keyCode);
                if (keyCode == (byte) ServiceResult.PinPad_Input_Cancel) {
                    if (mEmvHandler != null) {
                        mEmvHandler.onSetCardHolderInputPin(null);
                    }
//                    Toast.showMessage("Pin Pad is cancel.");
                    Toast.makeText(getApplicationContext(),"Pin Pad is cancel.",Toast.LENGTH_LONG).show();
                    finish();
                } else if (keyCode == (byte) ServiceResult.PinPad_Input_OK) {
                    SecurityLayer.Log( "keyCode =  PinPad_Input_OK");
                } else if (keyCode == (byte) ServiceResult.PinPad_Input_Clear) {
                    SecurityLayer.Log( "keyCode =  PinPad_Input_Clear");
                } else if (keyCode == (byte) ServiceResult.PinPad_Input_Num) {
                    SecurityLayer.Log("keyCode =  PinPad_Input_Num");
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
        new MaterialDialog.Builder(NL_Card_Wdl_InsCard.this)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

