package com.stanbicagent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.BuildConfig;
import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;
import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.pinpad.PinPad;
import com.morefun.yapi.device.pinpad.WorkKeyType;
import com.morefun.yapi.device.printer.FontFamily;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.yapi.engine.DeviceInfoConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import publib.BytesUtils;
import publib.ISO8583;
import publib.LoggerUtils;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;
import utils.FileUtils;
import utils.mfdes;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import adapter.Dashboard;
import adapter.DashboardAdapter;
import adapter.ServicesMenuAdapt;
import adapter.adapter.OTBRetroAdapt;
import model.BalInquiryData;
import model.BalanceInquiry;
import model.GetAgentIdData;
import model.GetBanksData;
import model.GetServicesData;
import rest.ApiClient;

import static android.os.Looper.getMainLooper;
import static com.stanbicagent.ApplicationConstants.SWITCH_IP;
import static com.stanbicagent.ApplicationConstants.SWITCH_PORT;


//import static com.stanbicbank.stanbicagent.ApplicationConstants.APP_VERSION;
//import static com.stanbicbank.stanbicagent.ApplicationConstants.TDK;
//import static com.stanbicbank.stanbicagent.ApplicationConstants.TLK;
//import static com.stanbicbank.stanbicagent.SessionManagement.KEY_ALL_USERS;
import static com.stanbicagent.ApplicationConstants.CUPS8583;
import static com.stanbicagent.ApplicationConstants.TDK;
        import static com.stanbicagent.ApplicationConstants.TLK;
import static com.stanbicagent.NL_Card_Wdl_Agent_PIN.maskString;
import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;
import static publib.Utils.string2byte;

public class HomeAccountFragNewUI extends Fragment implements View.OnClickListener {
    ImageView imageView1;
    ListView lv;
    TextView tv, tvacco, tvcomm, txttogrid;
    List<GetBanksData> planetsList = new ArrayList<GetBanksData>();
    List<Dashboard> servicesList = new ArrayList<Dashboard>();
    List<GetServicesData> servicesdata = new ArrayList<GetServicesData>();
    OTBRetroAdapt aAdpt;
    Button btn1, btn2, btn3;
    ProgressDialog pro;
    TextView curbal, lastl, greet, commamo;
    SessionManagement session;
    ProgressDialog prgDialog;
    ProgressBar prgbar;
    ImageView iv;
    ProgressDialog prgDialog2;
    ServicesMenuAdapt servadapt;
    GridView gridViewServices;
    DashboardAdapter dashboardAdapter;
    LinearLayout rldepo, rlwithdrw, rltransf, rlpybill, rlairtime,rlBalInq,rlministat, rlopenacc, rltransfers;
    RelativeLayout rlagac, rlcommac, rlreprint;
    List<GetAgentIdData> plan = new ArrayList<GetAgentIdData>();
    String agid,mTypeFacePath;
    String tid, mid, mer_name, loc, serial_no, requst, fld_120, fld_43, enc_tmk, enc_tpk, last_key_change, curr_date, tmk_tpk_combined, status, ccid, rpc, clr_tdk, clr_tmk, clr_tpk = "",
            reprint_data = "";
    String amou,crdN,recanno="",appvers,ctyp;
    DeviceServiceEngine mSDKManager;
    //    static N900Device n900Device;
//    private K21Pininput pinInput;
//    private K21CardReader cardReader;
//    ISO8583 iso8583;
    byte[] reqData = null;
    byte[] respData = null;
    byte[] cbcInitData = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};
    byte[] result;
    byte[] data = null;
    int sel_type = 0;
    int check = 0;
    ISO8583 iso8583;
    SharedPreferences pref;
    boolean isLightLed=false;
    public HomeAccountFragNewUI() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_mainnewui, null);
        btn1 = (Button) root.findViewById(R.id.btn1);
        curbal = (TextView) root.findViewById(R.id.currentbal);
        tvacco = (TextView) root.findViewById(R.id.hfjdj);
        lastl = (TextView) root.findViewById(R.id.txt2);
//        greet = (TextView) root.findViewById(R.id.greet);
        tvcomm = (TextView) root.findViewById(R.id.comm);
        commamo = (TextView) root.findViewById(R.id.accountbal);
        rlagac = (RelativeLayout) root.findViewById(R.id.rlagac);
        rlcommac = (RelativeLayout) root.findViewById(R.id.rlcommwal);
        rlagac.setOnClickListener(this);
        rlcommac.setOnClickListener(this);
        rlreprint = (RelativeLayout) root.findViewById(R.id.rlreprint);
        rlreprint.setOnClickListener(this);
        pro = new ProgressDialog(getActivity());
        pro.setMessage("Loading...");
        pro.setTitle("");
        pro.setCancelable(false);
        Pop();
        gridViewServices = (GridView) root.findViewById(R.id.gridView1);
        dashboardAdapter = new DashboardAdapter(servicesList, getActivity());
        prgDialog2 = new ProgressDialog(getActivity());
        prgDialog2.setMessage("Loading....");
        prgDialog2.setCancelable(false);
        iv = (ImageView) root.findViewById(R.id.img);
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        btn2 = (Button) root.findViewById(R.id.btn2);
        btn3 = (Button) root.findViewById(R.id.btn3);
        rlairtime = (LinearLayout) root.findViewById(R.id.airtim);
        rldepo = (LinearLayout) root.findViewById(R.id.depo);
        rltransfers = (LinearLayout) root.findViewById(R.id.transfers);
//        rltransf = (LinearLayout) root.findViewById(R.id.lintrans);
        //rltransf.setVisibility(View.GONE);
//        rlpybill = (LinearLayout) root.findViewById(R.id.bills);
        rlBalInq=(LinearLayout) root.findViewById(R.id.balInq);
        rlministat=(LinearLayout) root.findViewById(R.id.miniStat);
        rlopenacc = (LinearLayout) root.findViewById(R.id.opacc);
        rlwithdrw = (LinearLayout) root.findViewById(R.id.withd);
        rlairtime.setOnClickListener(this);
//        rltransf.setOnClickListener(this);
//        rlpybill.setOnClickListener(this);
        rlopenacc.setOnClickListener(this);
        rlwithdrw.setOnClickListener(this);
        rldepo.setOnClickListener(this);
        rlBalInq.setOnClickListener(this);
        rlministat.setOnClickListener(this);
        rltransfers.setOnClickListener(this);


        session = new SessionManagement(getActivity());
        boolean checktpref = session.checkShwBal();
        SecurityLayer.Log("Boolean checkpref", String.valueOf(checktpref));
        Calendar cal = Calendar.getInstance();
        String time = "";
        int v = cal.getTime().getHours();
        if (v < 12) {
            time = "Morning";
        }
        if (v >= 12 && v < 18) {
            time = "Afternoon";
        }
        if (v >= 18 && v < 24) {
            time = "Evening";
        }
        String custname = Utility.gettUtilCustname(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        if (Utility.isNotNull(agentid)) {
            //    tvcomm.setText(agentid);
        }

        lv = (ListView) root.findViewById(R.id.lv);
        SetPop();
        String cntopen = session.getString(SessionManagement.KEY_SETCNTOPEN);
        if (Utility.isNotNull(cntopen)) {
            SecurityLayer.Log("Security Can Open", cntopen);
            if (cntopen.equals("0")) {
                rlopenacc.setVisibility(View.GONE);
            } else if (cntopen.equals("1")) {
                rlopenacc.setVisibility(View.GONE);
            }
        }

        appvers = Utility.getAppVersion(getContext());
        checkAppvers();
        String chkappvs = session.getString("APPVERSBOOL");
        SecurityLayer.Log("chkappvs", chkappvs);
        if (chkappvs.equals("Y")) {
            GetAppversion();
        }

        ccid = ApplicationClass.get().getGlobalVals().getSimSerial();

        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            SecurityLayer.Log("MF_SDKManager", "ServiceEngine is Null");
        }
        mTypeFacePath = initTypeFacePath();
//        Load_PK();
//        Load_AID();

        return root;
    }

    private String initTypeFacePath() {
        {
            String namePath = "arial";
            String mTypefacePath = null;
            if (Build.VERSION.SDK_INT >= 23) {
                mTypefacePath = FileUtils.getExternalCacheDir(getContext(), namePath + ".ttf");
            } else {
                String filePath = FileUtils.createTmpDir(getContext());
                SecurityLayer.Log("initTypeFacePath", "filePath = " + filePath);
                mTypefacePath = filePath + namePath + ".ttf";
            }
            SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
            try {
                FileUtils.copyFromAssets(getContext().getAssets(), namePath, mTypefacePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SecurityLayer.Log("initTypeFacePath", "mTypefacePath = " + mTypefacePath);
            return mTypefacePath;
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
                //SecurityLayer.Log("ICAidManage", "ICAidManage >> result = " + (ret == ServiceResult.Success));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void swithgrid() {
        Fragment fragment = new NewHomeGrid();
        String title = "New Grid";
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        fragmentTransaction.replace(R.id.container_body, fragment, title);
        fragmentTransaction.addToBackStack(title);
        fragmentTransaction.commit();
    }

    public void Pop() {
        servicesList.add(new Dashboard("Transfer", R.drawable.transferic));
        servicesList.add(new Dashboard("Pay Bills", R.drawable.paybillic));
        servicesList.add(new Dashboard("Withdraw", R.drawable.withdrawic));
        servicesList.add(new Dashboard("Deposit", R.drawable.depositic));
        servicesList.add(new Dashboard("Airtime", R.drawable.airtimeic));
        servicesList.add(new Dashboard("Open Account", R.drawable.opaccic));
    }

    public void SetPop() {
        planetsList.clear();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rlagac) {
            ((FMobActivity) getActivity()).showEditDialog("MINIST");
        }
        if (view.getId() == R.id.rlcommwal) {
            ((FMobActivity) getActivity()).showEditDialog("COMM");
        }
        if (view.getId() == R.id.depo) {
            if (Utility.checkInternetConnection(getActivity())) {
                sel_type = 1;
                WDL_Check_Term_Status();
            }
        }
        if (view.getId() == R.id.withd) {
            if (Utility.checkInternetConnection(getActivity())) {
                sel_type = 2;
                WDL_Check_Term_Status();
            }
        }
        if (view.getId() == R.id.airtim) {
            sel_type = 3;
            WDL_Check_Term_Status();
        }
        if (view.getId() == R.id.balInq) {
            sel_type = 4;
            WDL_Check_Term_Status();
        }
        if (view.getId() == R.id.miniStat) {
            sel_type = 5;
            WDL_Check_Term_Status();
        }
        if (view.getId() == R.id.transfers) {
            sel_type = 6;
            WDL_Check_Term_Status();
        }
        if (view.getId() == R.id.opacc) {
            showDialog();
        }
        if (view.getId() == R.id.rlreprint) {
//            reprint_data = session.getString(SecurityLayer.KEY_REPRINT);
            reprint_data=ApplicationClass.get().getGlobalVals().getReprintData();
            if(reprint_data == null){
                Toast.makeText(
                        getActivity(),
                        "No reprint available at the moment.",
                        Toast.LENGTH_LONG).show();
            }else{
                sel_type = 7;
                WDL_Check_Term_Status();
            }
        }
    }

    private void showDialog() {
        final CharSequence[] items = {"Register for Biometric", "Withdraw", "Deposit",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Register for Biometric")) {
                    //startActivity(new Intent(getActivity(), Bio_Register_Capture_Details.class));
                } /*else if (items[item].equals("Withdraw")) {
                    startActivity(new Intent(getActivity(), OpenAccActivity.class));
                }else if (items[item].equals("Deposit")) {
                    startActivity(new Intent(getActivity(), OpenAccActivity.class));
                }*/ else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void StartChartAct(int i) {

    }

    public void setBalInqu() {
        if (Utility.checkInternetConnection(getActivity())) {
            prgDialog.show();
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            Call<BalanceInquiry> call = apiService.getBalInq("1", usid, agentid, "9493818389");
            call.enqueue(new Callback<BalanceInquiry>() {
                @Override
                public void onResponse(Call<BalanceInquiry> call, Response<BalanceInquiry> response) {
                    if (!(response.body() == null)) {
                        String responsemessage = response.body().getMessage();
                        BalInquiryData baldata = response.body().getData();
                        SecurityLayer.Log("Response Message", responsemessage);

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                        if (!(baldata == null)) {
                            String balamo = baldata.getbalance();
                            String comamo = baldata.getcommision();


                            String fbal = Utility.returnNumberFormat(balamo);
                            curbal.setText(Html.fromHtml("&#8358") + " " + fbal);

                            String cmbal = Utility.returnNumberFormat(comamo);
                            commamo.setText(Html.fromHtml("&#8358") + " " + cmbal);
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error retrieving your balance ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error retrieving your balance ",
                                Toast.LENGTH_LONG).show();
                    }
                    try {
                        if ((prgDialog != null) && prgDialog.isShowing()) {
                            prgDialog.dismiss();
                        }
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } catch (final Exception e) {
                        // Handle or log or ignore
                    } finally {
                        prgDialog = null;
                    }

                    //  prgDialog.dismiss();
                }

                @Override
                public void onFailure(Call<BalanceInquiry> call, Throwable t) {
                    // Log error here since request failed
                    SecurityLayer.Log("Throwable error", t.toString());
                    Toast.makeText(
                            getActivity(),
                            "There was an error retrieving your balance ",
                            Toast.LENGTH_LONG).show();
                    prgDialog.dismiss();
                }
            });
        }
    }

    private void dismissProgressDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void setBalInquSec() {
        prgDialog2.show();
        String endpoint = "core/balenquirey.action";
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/9493818389";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }

        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    //obj = Utility.onresp(obj,getActivity());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    JSONObject plan = obj.optJSONObject("data");
                    if (!(response.body() == null)) {
                        if (respcode.equals("00")) {
                            SecurityLayer.Log("Response Message", responsemessage);
                            if (!(plan == null)) {
                                String balamo = plan.optString("balance");
                                String comamo = plan.optString("commision");
                                String fbal = Utility.returnNumberFormat(balamo);
                                curbal.setText(Html.fromHtml("&#8358") + " " + fbal);
                                String cmbal = Utility.returnNumberFormat(comamo);
                                commamo.setText(Html.fromHtml("&#8358") + " " + cmbal);
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "There was an error retrieving your balance ",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error retrieving your balance ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error retrieving your balance ",
                                Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }

                prgDialog2.dismiss();
                if (session.getString(SessionManagement.KEY_SETBANKS).equals("N")) {
                    GetServv();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                //   pDialog.dismiss();
                prgbar.setVisibility(View.GONE);
                try {
                    if ((prgDialog2 != null) && prgDialog2.isShowing()) {
                        prgDialog2.dismiss();
                    }
                } catch (final IllegalArgumentException e) {
                    // Handle or log or ignore
                } catch (final Exception e) {
                    // Handle or log or ignore
                } finally {
                    prgDialog2 = null;
                }
                if (session.getString(SessionManagement.KEY_SETBANKS).equals("N")) {
                    GetServv();
                }
                prgDialog2.dismiss();
            }
        });
    }

    private void GetServv() {
        String endpoint = "transfer/getbanks.action";
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String params = usid + "/" + agentid + "/93939393";
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                //SecurityLayer.Log("cbcurl",url);
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }


            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


            Call<String> call = apiService.setGenericRequestRaw(urlparams);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        // JSON Object


                        SecurityLayer.Log("Get Banks Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        JSONArray servdata = obj.optJSONArray("data");
                        String strservdata = servdata.toString();
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);

                                    if (respcode.equals("00")) {
                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        session.setString(SessionManagement.KEY_SETBANKS, "Y");
                                        session.setString(SessionManagement.KEY_BANKS, strservdata);

                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), SignInActivity.class));
                                    Toast.makeText(
                                            getActivity(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {

                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();


                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                        // prgDialog2.dismiss();


                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    //    prgDialog2.dismiss();
                    if (session.getString(SessionManagement.KEY_SETBILLERS).equals("N")) {
                        if (!(getActivity() == null)) {
                            GetBillPay();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());


                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();


                    //   prgDialog2.dismiss();
                    if (session.getString(SessionManagement.KEY_SETBILLERS).equals("N")) {
                        GetBillPay();
                    }
                }
            });
        }
    }

    private void GetBillPay() {
        //   prgDialog2.show();
        String endpoint = "billpayment/services.action";
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/9493818389";


            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                //SecurityLayer.Log("cbcurl",url);
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }

            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);


            Call<String> call = apiService.setGenericRequestRaw(urlparams);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        // JSON Object


                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        JSONArray servdata = obj.optJSONArray("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);

                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");

                            SecurityLayer.Log("Response Message", responsemessage);

                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);

                                    if (respcode.equals("00")) {
                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        String billerdata = servdata.toString();
                                        session.setString(SessionManagement.KEY_SETBILLERS, "Y");
                                        session.setString(SessionManagement.KEY_BILLERS, billerdata);
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), SignInActivity.class));
                                    Toast.makeText(
                                            getActivity(),
                                            "You have been locked out of the app.Please call customer care for further details",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {

                                Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();


                            }
                        } else {

                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();


                        }
                        // prgDialog2.dismiss();


                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());

                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    //    prgDialog2.dismiss();
                    if (!(getActivity() == null)) {
                        if (session.getString(SessionManagement.KEY_SETWALLETS).equals("N")) {
                            GetWallets();
                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    // Log error here since request failed
                    SecurityLayer.Log("throwable error", t.toString());


                    Toast.makeText(
                            getActivity(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();


                    //   prgDialog2.dismiss();
                    if (!(getActivity() == null)) {
                        if (session.getString(SessionManagement.KEY_SETWALLETS).equals("N")) {
                            GetWallets();
                        }

                    }
                }
            });
        }
    }

    private void PopulateAirtime() {
        String endpoint = "billpayment/MMObillers.action";
        //  prgDialog.show();
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String agentid = Utility.gettUtilAgentId(getActivity());
            String mobnoo = Utility.gettUtilMobno(getActivity());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/1";
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }
            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
            Call<String> call = apiService.setGenericRequestRaw(urlparams);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());

                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");

                        JSONArray plan = obj.optJSONArray("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);
                        if (!(response.body() == null)) {
                            if (respcode.equals("00")) {

                                SecurityLayer.Log("Response Message", responsemessage);
                                SecurityLayer.Log("JSON Aray", plan.toString());
                                String billerdata = plan.toString();
                                session.setString(SessionManagement.KEY_SETAIRTIME, "Y");
                                session.setString(SessionManagement.KEY_AIRTIME, billerdata);
                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        responsemessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error processing your request ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        //Utility.errornexttoken();
                        // TODO Auto-generated catch block
                        Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                    } catch (Exception e) {
                        //Utility.errornexttoken();
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        // SecurityLayer.Log(e.toString());
                    }
                    //        prgDialog.dismiss();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Log error here since request failed
                    SecurityLayer.Log("Throwable error", t.toString());
                    //Utility.errornexttoken();
                    Toast.makeText(
                            getActivity(),
                            "There was an error processing your request",
                            Toast.LENGTH_LONG).show();
                    //   prgDialog.dismiss();
                }
            });
        }
    }

    private void GetWallets() {
        String endpoint = "transfer/getwallets.action";
        String usid = Utility.gettUtilUserId(getActivity());
        String agentid = Utility.gettUtilAgentId(getActivity());
        String params = usid + "/" + agentid + "/93939393";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }
        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("Get Wallets Resp", response.body());
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptTransaction(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    JSONArray servdata = obj.optJSONArray("data");
                    if (!(response.body() == null)) {
                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                            if (!(Utility.checkUserLocked(respcode))) {
                                SecurityLayer.Log("Response Message", responsemessage);
                                if (respcode.equals("00")) {
                                    SecurityLayer.Log("JSON Aray", servdata.toString());
                                    if (servdata.length() > 0) {
                                        SecurityLayer.Log("JSON Aray", servdata.toString());
                                        String billerdata = servdata.toString();
                                        session.setString(SessionManagement.KEY_SETWALLETS, "Y");
                                        session.setString(SessionManagement.KEY_WALLETS, billerdata);
                                    } else {
                                        Toast.makeText(
                                                getActivity(),
                                                "No services available  ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "" + responsemessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), SignInActivity.class));
                                Toast.makeText(
                                        getActivity(),
                                        "You have been locked out of the app.Please call customer care for further details",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "There was an error on your request",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                    // SecurityLayer.Log(e.toString());
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                if (!(getActivity() == null)) {
                    if (session.getString(SessionManagement.KEY_SETAIRTIME).equals("N")) {
                        PopulateAirtime();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error on your request",
                        Toast.LENGTH_LONG).show();
                if (!(getActivity() == null)) {
                    if (session.getString(SessionManagement.KEY_SETAIRTIME).equals("N")) {
                        PopulateAirtime();
                    }
                }
            }
        });
    }

    private void LogRetro(String params, final String service) {
        pro.show();
        String endpoint = "login/login.action/";
        String urlparams = "";
        try {
            urlparams = SecurityLayer.generalLogin(params, "23322", getActivity(), endpoint);
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }
        ApiInterface apiService =
                ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparams);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    SecurityLayer.Log("response..:", response.body());
                    JSONObject obj = new JSONObject(response.body());
                    obj = SecurityLayer.decryptGeneralLogin(obj, getActivity());
                    SecurityLayer.Log("decrypted_response", obj.toString());
                    String respcode = obj.optString("responseCode");
                    String responsemessage = obj.optString("message");
                    JSONObject datas = obj.optJSONObject("data");
                    if (Utility.isNotNull(respcode) && Utility.isNotNull(responsemessage)) {
                        SecurityLayer.Log("Response Message", responsemessage);
                        if (respcode.equals("00")) {
                            if (!(datas == null)) {
                                if (service.equals("MINIST")) {
                                    android.app.Fragment fragment = new Minstat();
                                    String title = "Mini Statement";
                                    ((FMobActivity) getActivity()).addAppFragment(fragment, title);
                                }
                                if (service.equals("COMM")) {
                                    android.app.Fragment fragment = new CommReport();
                                    String title = "Commissions Report";
                                    ((FMobActivity) getActivity()).addAppFragment(fragment, title);
                                }
                            }
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                }
                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getActivity(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();
                if ((pro != null) && pro.isShowing() && !(getActivity() == null)) {
                    pro.dismiss();
                }
            }
        });
    }

    private void GetAppversion() {
        pro.show();
        String endpoint = "reg/appVersion.action";
        if (!(getActivity() == null)) {
            String usid = Utility.gettUtilUserId(getActivity());
            String appid = Utility.getFinAppid(getActivity());
            String appvers = Utility.getAppVersion(getActivity());
            String params = ApplicationConstants.CHANNEL_ID + usid + "/" + appid + "/" + appvers;
            SecurityLayer.Log("params", params);
            String urlparams = "";
            try {
                urlparams = SecurityLayer.genURLCBC(params, endpoint, getActivity());
                SecurityLayer.Log("RefURL", urlparams);
                SecurityLayer.Log("refurl", urlparams);
                SecurityLayer.Log("params", params);
            } catch (Exception e) {
                SecurityLayer.Log("encryptionerror", e.toString());
            }
            ApiInterface apiService =
                    ApiSecurityClient.getClient(getActivity()).create(ApiInterface.class);
            Call<String> call = apiService.setGenericRequestRaw(urlparams);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        long secs = (new Date().getTime()) / 1000;
                        session.setLong("CHECKTIME", secs);
                        SecurityLayer.Log("Cable TV Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());
                        JSONObject obj = new JSONObject(response.body());
                        //obj = Utility.onresp(obj,getActivity());
                        obj = SecurityLayer.decryptTransaction(obj, getActivity());
                        SecurityLayer.Log("decrypted_response", obj.toString());
                        JSONObject servdata = obj.optJSONObject("data");
                        //session.setString(SecurityLayer.KEY_APP_ID,appid);
                        if (!(response.body() == null)) {
                            String respcode = obj.optString("responseCode");
                            String responsemessage = obj.optString("message");
                            SecurityLayer.Log("Response Message", responsemessage);
                            if (Utility.isNotNull(respcode) && Utility.isNotNull(respcode)) {
                                if (!(Utility.checkUserLocked(respcode))) {
                                    SecurityLayer.Log("Response Message", responsemessage);
                                    if (respcode.equals("00")) {
                                        String minversion = servdata.optString("minVersion");
                                        SecurityLayer.Log("Min version", minversion);
                                        double dbvers = Double.parseDouble(minversion);
                                        String currvers = Utility.getAppVersion(getActivity());
                                        double dbcurrvers = Double.parseDouble(currvers);
                                        if (dbcurrvers < dbvers) {
                                            final String packageName = "stanbic.stanbicmob.com.stanbicagent";
                                            new MaterialDialog.Builder(getActivity())
                                                    .title(getActivity().getString(R.string.appupd_verstitle))
                                                    .content(getActivity().getString(R.string.appupd_vers))
                                                    .positiveText("Upgrade")
                                                    .negativeText("Not Now")
                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                        @Override
                                                        public void onPositive(MaterialDialog dialog) {
                                                            try {
                                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                                                            }
                                                        }

                                                        @Override
                                                        public void onNegative(MaterialDialog dialog) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    } else {
                                       /* Toast.makeText(
                                                getActivity(),
                                                "" + responsemessage,
                                                Toast.LENGTH_LONG).show();*/
                                    }
                                } else {
                                    if (!(getActivity() == null)) {
                                        ((FMobActivity) getActivity()).LogOut();
                                    }
                                }
                            } else {

                               /* Toast.makeText(
                                        getActivity(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();*/


                            }
                        } else {


                        }
                    } catch (JSONException e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        if (!(getActivity() == null)) {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        }
                    } catch (Exception e) {
                        SecurityLayer.Log("encryptionJSONException", e.toString());
                        if (!(getActivity() == null)) {
                            Toast.makeText(getActivity(), getActivity().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                            ((FMobActivity) getActivity()).SetForceOutDialog(getString(R.string.forceout), getString(R.string.forceouterr), getActivity());
                        }
                    }
                    if (!(getActivity() == null)) {
                        pro.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    SecurityLayer.Log("throwable error", t.toString());
                    long secs = (new Date().getTime()) / 1000;
                    session.setLong("CHECKTIME", secs);
                    if (!(getActivity() == null)) {
                        pro.dismiss();
                        Toast.makeText(
                                getActivity(),
                                "There was an error on your request",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void checkAppvers() {
        session.setString("APPVERSBOOL", "Y");
        long checkcurrtime = session.getLong("CHECKTIME");
        SecurityLayer.Log("checkcurrtime", Long.toString(checkcurrtime));
        if (checkcurrtime == 0) {
            long secs = (new Date().getTime()) / 1000;
            session.setLong("CHECKTIME", secs);
        }
        long checknewcurrtime = session.getLong("CHECKTIME");
        SecurityLayer.Log("checknewcurrtime", Long.toString(checknewcurrtime));
        long currsecs = (new Date().getTime()) / 1000;
        long diffsecs = currsecs - checknewcurrtime;
        SecurityLayer.Log("diffsecs", Long.toString(diffsecs));
        if (diffsecs > 86400) {
            session.setString("APPVERSBOOL", "Y");
        } else {
            session.setString("APPVERSBOOL", "N");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((FMobActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
    }

    @Override
    public void onStop() {
        super.onStop();
        ((FMobActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5f5f5")));
    }

    private void WDL_Check_Term_Status() {
        Check_Printer();
    }

    public void Check_Printer() {
        try {
            com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
            if (printer.getStatus() != 0){
                check = -1;
            }else{
                check = 0;
            }
            After_Printer_Check();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public int Check_Key_Exchange_Date() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("yyyy-MM-dd");
        curr_date = sdate_df.format(c);
        last_key_change = pref.getString("last_kchange", "");
        if (last_key_change.equals("")) {
            return -1;
        }
        if (last_key_change.equals(curr_date)) {
            return 0;
        }
        return -1;
    }


    public void After_Card_Check() {
        if (check != 0) {
            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getContext());
            tid = pref.getString("tid", "");
            mid = pref.getString("mid", "");
            mer_name = pref.getString("mer_name", "");
            fld_43 = pref.getString("fld_43", "");
            if (tid.equals("") && mid.equals("") && mer_name.equals("") && fld_43.equals("")) {
                HomeAccountFragNewUI.Send_User_Download_Request user_download_client = new HomeAccountFragNewUI.Send_User_Download_Request(SWITCH_IP, SWITCH_PORT);
                user_download_client.execute();
            } else {
                int cked = Check_Key_Exchange_Date();
                if (cked == 0) {

                    String txt = "0000000000";
                    SharedPreferences tt = PreferenceManager
                            .getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = tt.edit();
                    editor.putString("txn_amount", txt);
                    editor.commit();

                    if(sel_type==4) {
                        ApplicationClass.get().getGlobalVals().setTranType("BALANCE INQUIRY");
                    }else if (sel_type==5){
                        ApplicationClass.get().getGlobalVals().setTranType("MINI STATEMENT");
                    }
                    //hapa
                    /*isLightLed = true;
                    try {
                        mSDKManager.getLEDDriver().PowerLed(isLightLed, isLightLed, isLightLed, isLightLed);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                    Intent intent = new Intent(getActivity(), NL_Card_Wdl_InsCard.class);
//                    intent.putExtra("amou", txt);
//                    intent.putExtra("trantype", "BEQ"); //"MIN"
                    startActivity(intent);
                } else {

                    HomeAccountFragNewUI.Send_Key_Exchange_Request key_exchange_client = new HomeAccountFragNewUI.Send_Key_Exchange_Request(SWITCH_IP, SWITCH_PORT);
                    key_exchange_client.execute();

                }
            }
        } /*else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setDialog("Card is inserted in POS.\nPlease remove card before transacting.");
                    Toast.makeText(
                            getContext(),
                            "Please remove card before starting card transaction.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }*/
    }
    public void Check_Card() {
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
                            if (retCode<0) {
                                getMainLooper().prepare();
                                After_Card_Check();
                                getMainLooper().loop();
                            }else{
                                getMainLooper().prepare();
                                Toast.makeText(getActivity().getBaseContext(), "Please Remove Card!",  Toast.LENGTH_SHORT).show();
                                getMainLooper().loop();
                            }
                        }
                    };
                    iccCardReader.searchCard(listener, 1, new String[]{IccCardType.CPUCARD, IccCardType.AT24CXX, IccCardType.AT88SC102});
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void After_Printer_Check() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        tid = pref.getString("tid", "");
        mid = pref.getString("mid", "");
        mer_name = pref.getString("mer_name", "");
        fld_43 = pref.getString("fld_43", "");
        if (check == 0) {
            if (Utility.checkInternetConnection(getActivity())) {
                if (tid.equals("") && mid.equals("") && mer_name.equals("") && fld_43.equals("")) {
                    HomeAccountFragNewUI.Send_User_Download_Request user_download_client = new HomeAccountFragNewUI.Send_User_Download_Request(SWITCH_IP, SWITCH_PORT);
                    user_download_client.execute();
                } else {
                    if (sel_type == 1) {
                        startActivity(new Intent(getActivity(), NL_Dep_MenuActivity.class));
                    } else if (sel_type == 2) {
                        startActivity(new Intent(getActivity(), NL_CardWdlTransAmountActivity.class));// NL_WDLMenuActivity.class));
                    } else if (sel_type == 3) {
                        startActivity(new Intent(getActivity(), AirtimeTransfActivity.class));
//                        startActivity(new Intent(getActivity(), BillMenuActivity.class));
                    } else if (sel_type == 4) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("txn_amount", "0000000000");
                        editor.putString("card_ttype", "BEQ");
                        editor.putString("trans_type", "BEQ");
                        editor.commit();
                        //startActivity(new Intent(getActivity(), NL_CardWdlInqCard.class));
                        Check_Card();
                    } else if (sel_type == 5) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("txn_amount", "0000000000");
                        editor.putString("card_ttype", "MIN");
                        editor.putString("trans_type", "MIN");
                        editor.commit();
                        //startActivity(new Intent(getActivity(), NL_CardWdlInqCard.class));
                        Check_Card();
                    } else if (sel_type == 6) {
                        startActivity(new Intent(getActivity(), NL_CEVA_CardTransfer_Amount_Activity.class));
                    }else if (sel_type == 7) {
                        Print_Reprint();
                    }
                }
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setDialog("Please insert paper receipt before transacting.");
                }
            });
        }
    }

    public void setDialog(String message) {
        new MaterialDialog.Builder(getActivity())
                .title("Error")
                .content(message)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private class Send_Key_Exchange_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        SSLSocketFactory sslSocketFactory = null;
        SSLSocket sslSocket = null;
        SSLContext sslContext = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        Send_Key_Exchange_Request(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

//        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.setMessage("Performing Key Exchange....");
                    prgDialog.show();
                }
            });

            Set_Key_Exchange_Data();
        }



        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = new Socket();
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                socket.connect(new InetSocketAddress(dstAddress, dstPort), 15000);
                socket.setSoTimeout(60000);
                bos = new BufferedOutputStream(socket.getOutputStream());
                byte[] reqLenBs = ISOUtils.packIntToBytes(reqData.length, 2, true);
                bos.write(reqLenBs);
                //SecurityLayer.Log("Dump", "Dump --- " + Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(req));
                System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                bos.write(reqData);
                bos.flush();

                bis = new BufferedInputStream(socket.getInputStream());
                byte[] respLenbs = new byte[2];
                bis.read(respLenbs);
                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
                System.out.println(respLen);
                byte[] buffer = new byte[respLen];
                bis.read(buffer);
                System.out.println(Dump.getHexDump(buffer));
                respData = buffer;
                status = "1";
            } catch (UnknownHostException e) {
                status = "3";
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                status = "3";
                response = "No response received." + "\n" + "Please try again";
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        if (bos != null)
                            bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    SecurityLayer.Log("socket", "user download socket is null");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            prgDialog.dismiss();
            if (status.equals("1")) {
                if (!(respData == null) && respData.length > 0) {
                    Unpack_Key_Exchange_Data();
                }
            } else {
                SecurityLayer.Log("response", "user download response == " + response);
                setDialog("Error", response);
            }
        }
    }
    public void Set_Key_Exchange_Data() {
        Bundle devInfo = null;

        try {
            devInfo = mSDKManager.getDevInfo();
            serial_no = devInfo.getString(DeviceInfoConstrants.COMMOM_SN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //serial_no = "N7NL00479115";
        if(BuildConfig.DEBUG)
            serial_no = "19990004";
//        serial_no="01223125";
        else
        serial_no = serial_no.substring(serial_no.length() - 8);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
        String formattedDate = df.format(c);
        iso8583 = new ISO8583(getContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.setField(0, "0800");
            iso8583.setField(3, "990180");
            iso8583.setField(7, formattedDate);
            iso8583.setField(60, serial_no); //serial_no - N7NL00479115
            iso8583.setField(127, ccid);
        } catch (Exception e) {
            Log.v("iso", "initPack error");
            e.printStackTrace();
            return;
        }
        try {
            requst = iso8583.pack();
        } catch (Exception e) {
            LoggerUtils.e("Group 8583 package exception");
            e.printStackTrace();
            return;
        }
        Log.v("key exchange req iso", requst);
        reqData = BytesUtils.hexToBytes(requst);
    }
    public void Unpack_Key_Exchange_Data() {
        iso8583 = new ISO8583(getContext());
        try {
            iso8583.loadXmlFile(CUPS8583);
        } catch (Exception e) {
            e.printStackTrace();
        }
        iso8583.initPack();
        try {
            iso8583.unpack(respData);
            String rpc = iso8583.getField(39);
            SecurityLayer.Log("rpc", rpc);
            if (rpc.equals("00")) {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                tmk_tpk_combined = iso8583.getField(120);
                SecurityLayer.Log("tmk_tpk_combined", "tmk_tpk_combined >> " + tmk_tpk_combined);
                Load_Keys_KE();
                //startActivity(new Intent(NL_CardWdlInqCard.this, NL_CardWdlTransAmountActivity.class));
            } else {
                SecurityLayer.Log("keyc res", iso8583.getField(111));
                setDialog("Failed", "Response Code: " + rpc + "\n" + iso8583.getField(111));
            }
        } catch (Exception e) {
            SecurityLayer.Log("iso", "initunPack error");
            e.printStackTrace();
            return;
        }
    }
    public void Load_Keys_KE() throws RemoteException {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("yyyy-MM-dd");
        curr_date = sdate_df.format(c);

        String whole_120 = tmk_tpk_combined;
        String[] firstsplit = whole_120.split(Pattern.quote("|"));
        enc_tmk = firstsplit[0];
        enc_tpk = firstsplit[1];
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        //editor.putString("etmk", enc_tmk);
        //editor.putString("etpk", enc_tpk);
        editor.putString("last_kchange", curr_date);
        editor.commit();

        SecurityLayer.Log("enc_tmk", "enc_tmk >> " + enc_tmk);
        SecurityLayer.Log("enc_tpk", "enc_tpk >> " + enc_tpk);
        //iRet = Load_TLK(mSDKManager);
        //SecurityLayer.Log("Load_TLK", "Load_TLK >> " + (iRet == ServiceResult.Success ? getString(R.string.msg_succ) : getString(R.string.msg_fail)));
        Load_Other_Keys(mSDKManager);
    }
    public void Load_Other_Keys(DeviceServiceEngine engine) throws RemoteException {
        try {
            clr_tdk = mfdes.decrypt(TLK, TDK);
            SecurityLayer.Log("decrypt clr_tdk", "clr_tdk = " + clr_tdk);
            clr_tmk = mfdes.decrypt(TLK, enc_tmk);
            SecurityLayer.Log("decrypt clr_tmk", "clr_tmk = " + clr_tmk);
            clr_tpk = mfdes.decrypt(clr_tmk, enc_tpk);
            SecurityLayer.Log("decrypt clr_tpk", "clr_tpk = " + clr_tpk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PinPad pinPad = engine.getPinPad();
        int tdk_ret = pinPad.loadPlainWKey(4, WorkKeyType.TDKEY, string2byte(clr_tdk), 16);
        SecurityLayer.Log("loadPlainWKey", "loadPlainWKey TDK >> " + (tdk_ret == ServiceResult.Success ? "Success" : "Fail"));
        int tpk_ret = pinPad.loadPlainWKey(0, WorkKeyType.PINKEY, string2byte(clr_tpk), 16);
        SecurityLayer.Log("loadPlainWKey", "loadPlainWKey TDK >> " + (tpk_ret == ServiceResult.Success ? "Success" : "Fail"));
    }

    private class Send_User_Download_Request extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";

        Send_User_Download_Request(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prgDialog.setMessage("Downloading terminal parameters....");
                    prgDialog.show();
                }
            });
           // Set_User_Download_Data();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Socket socket = new Socket();
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                socket.connect(new InetSocketAddress(dstAddress, dstPort), 15000);
                socket.setSoTimeout(60000);
                bos = new BufferedOutputStream(socket.getOutputStream());
                byte[] reqLenBs = ISOUtils.packIntToBytes(reqData.length, 2, true);
                bos.write(reqLenBs);
                //SecurityLayer.Log("Dump", "Dump --- " + Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(req));
                System.out.println(Dump.getHexDump(reqLenBs) + "," + Dump.getHexDump(reqData));
                bos.write(reqData);
                bos.flush();

                bis = new BufferedInputStream(socket.getInputStream());
                byte[] respLenbs = new byte[2];
                bis.read(respLenbs);
                int respLen = ISOUtils.unPackIntFromBytes(respLenbs, 0, 2, true);
                System.out.println(respLen);
                byte[] buffer = new byte[respLen];
                bis.read(buffer);
                System.out.println(Dump.getHexDump(buffer));
                respData = buffer;
                status = "1";
            } catch (UnknownHostException e) {
                status = "3";
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                status = "3";
                response = "No response received." + "\n" + "Please try again";
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        if (bos != null)
                            bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    SecurityLayer.Log("socket", "user download socket is null");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            prgDialog.dismiss();
            if (status.equals("1")) {
                if (!(respData == null) && respData.length > 0) {
//                    Unpack_User_Download_Data();
                }
            } else {
                SecurityLayer.Log("response", "user download response == " + response);
                setDialog("Error", response);
                //setDialog("Error", "Unable to download terminal parameters. Please try again later");
            }
        }
    }



//    public void Set_User_Download_Data() {
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("MMddhhmmss");
//        SimpleDateFormat df_1 = new SimpleDateFormat("hhmmss");
//        String formattedDate = df.format(c);
//        String formattedDate_stan = df_1.format(c);
//        K21SecurityModule securityModule;
////        securityModule = n900Device.getSecurityModule();
//        serial_no = securityModule.getPosTusn();
//        serial_no = serial_no.substring(serial_no.length() - 8);
//        SecurityLayer.Log("serial_no", "serial_no >> " + serial_no);
////        iso8583 = new ISO8583(getActivity());
//        try {
////            iso8583.loadXmlFile(Const.FileConst.CUPS8583);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        iso8583.initPack();
//        try {
//            iso8583.setField(0, "0800");
//            iso8583.setField(3, "900280");
//            iso8583.setField(7, formattedDate);
//            iso8583.setField(11, formattedDate_stan);
//            iso8583.setField(60, serial_no); //serial_no // John - 00294487 // Mine - 00479115 // Tony - 01120660
//            iso8583.setField(101, APP_VERSION);
//            iso8583.setField(127, ccid); // ccid //John - 89254021024052021542 // Mine - 89254021104116677933 // Tony - 89254021104116677958
//        } catch (Exception e) {
//            SecurityLayer.Log("iso", "initPack error");
//            e.printStackTrace();
//            return;
//        }
//        try {
//            requst = iso8583.pack();
//        } catch (Exception e) {
//            LoggerUtils.e("Group 8583 package exception");
//            e.printStackTrace();
//            return;
//        }
//        SecurityLayer.Log("user download req iso", requst);
//        reqData = BytesUtils.hexToBytes(requst);
//    }

//    public void Unpack_User_Download_Data() {
//        iso8583 = new ISO8583(getActivity());
//        try {
//            iso8583.loadXmlFile(Const.FileConst.CUPS8583);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        iso8583.initPack();
//        try {
//            iso8583.unpack(respData);
//            rpc = iso8583.getField(39);
//            String fl_111 = iso8583.getField(111);
//            SecurityLayer.Log("rpc", rpc);
//            if (rpc.equals("00")) {
//                fld_120 = iso8583.getField(120);
//                Split_Fld_120();
//                //startActivity(new Intent(getApplicationContext(), NL_Stanbic_CardWdlTransActivity.class));
//            } else {
//                SecurityLayer.Log("ud response", fl_111);
//                setDialog("Failed", "Response Code: " + rpc + "\n" + fl_111);
//            }
//        } catch (Exception e) {
//            SecurityLayer.Log("iso", "initunPack error");
//            e.printStackTrace();
//            return;
//        }
//    }

//    private void Split_Fld_120() {
//        String whole_120 = fld_120;
//        SecurityLayer.Log("whole_120", whole_120);
//        String[] firstsplit = whole_120.split(Pattern.quote("^"));
//        String user_pass = firstsplit[0];
//        String[] user_pass_arr = user_pass.split(Pattern.quote(","));
//        String user_only = user_pass_arr[0];
//        String[] secondsplit = firstsplit[1].split(Pattern.quote("@"));
//        String terminal_details = secondsplit[0];
//        String[] terminal_details_arr = terminal_details.split(Pattern.quote("#"));
//        String tid = terminal_details_arr[1];
//        String mid = terminal_details_arr[2];
//        String name = terminal_details_arr[3];
//        String loc = terminal_details_arr[4];
//        loc = loc.replaceAll("\\s+", "");
//        String ccode = terminal_details_arr[5];
//        String admin_pass = secondsplit[1];
//        String[] thirdsplit = secondsplit[2].split(Pattern.quote("|"));
//        String tmk = thirdsplit[1];
//        String tpk = thirdsplit[2];
//        tmk_tpk_combined = tmk + "|" + tpk;
//
//        String fld_43_name = String.format("%-25s", name).replace(' ', ' ');
//        String fld_43_loc = String.format("%-13s", loc).replace(' ', ' ');
//        fld_43 = fld_43_name + "" + fld_43_loc + "" + ccode;
//
//        SecurityLayer.Log("arrays", user_pass + " ----- " + terminal_details + " ----- " + admin_pass + " ----- " + tmk + " ----- " + tpk);
//        /*SecurityLayer.Log("tmk_tpk_combined", tmk_tpk_combined);
//        SecurityLayer.Log("user", user_only);
//        SecurityLayer.Log("tid", tid);
//        SecurityLayer.Log("mid", mid);
//        SecurityLayer.Log("name", name);
//        SecurityLayer.Log("loc", loc);
//        SecurityLayer.Log("ccode", ccode);*/
//
//        String user_id = Utility.gettUtilUserId(getActivity());
//        boolean isContains = whole_120.contains(user_id);
//        SecurityLayer.Log("user_id", user_id + " = " + isContains);
//        //if (user_id.equals(user_only)) {
//        if(isContains){
//            session.setString(KEY_ALL_USERS, user_pass);
//            SharedPreferences pref = PreferenceManager
//                    .getDefaultSharedPreferences(getActivity());
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("user_only", user_only);
//            editor.putString("tid", tid);
//            editor.putString("mid", mid);
//            editor.putString("mer_name", name);
//            editor.putString("loc", loc);
//            editor.putString("ccode", ccode);
//            editor.putString("fld_43", fld_43);
//            editor.putString("tmk", tmk);
//            editor.putString("tpk", tpk);
//            editor.commit();
//            Load_Keys_KE();
//            if (sel_type == 1) {
//                startActivity(new Intent(getActivity(), NL_Dep_MenuActivity.class));
//            }else if (sel_type == 2) {
//                startActivity(new Intent(getActivity(), AirtimeTransfActivity.class));
//            } if(sel_type == 3) {
//                SharedPreferences pref3 = PreferenceManager
//                        .getDefaultSharedPreferences(getActivity());
//                SharedPreferences.Editor editor3 = pref3.edit();
//                editor3.putString("card_ttype", "SWDL");
//                editor3.commit();
//                startActivity(new Intent(getActivity(), NL_Stanbic_CardWdlTransActivity.class));
//            }else if(sel_type == 4){
//                SharedPreferences pref1 = PreferenceManager
//                        .getDefaultSharedPreferences(getActivity());
//                SharedPreferences.Editor editor1 = pref1.edit();
//                editor1.putString("txn_amount", "0000");
//                editor1.putString("card_ttype", "MIN");
//                editor1.commit();
//                startActivity(new Intent(getActivity(), NL_Stanbic_CardWdlInsCard.class));
//            }else if(sel_type == 5){
//                SharedPreferences pref2 = PreferenceManager
//                        .getDefaultSharedPreferences(getActivity());
//                SharedPreferences.Editor editor2 = pref2.edit();
//                editor2.putString("txn_amount", "0000");
//                editor2.putString("card_ttype", "BEQ");
//                editor2.commit();
//                startActivity(new Intent(getActivity(), NL_Stanbic_CardWdlInsCard.class));
//            }
//        } else {
//            setDialog("Error", "Please note that this terminal is not assigned to this customer attendant ID.");
//        }
//    }

//    public void Load_Keys_KE() {
//        String whole_120 = tmk_tpk_combined;
//        String[] firstsplit = whole_120.split(Pattern.quote("|"));
//        enc_tmk = firstsplit[0];
//        enc_tpk = firstsplit[1];
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat sdate_df = new SimpleDateFormat("yyyy-MM-dd");
//        curr_date = sdate_df.format(c);
//
//        SharedPreferences pref = PreferenceManager
//                .getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("etmk", enc_tmk);
//        editor.putString("etpk", enc_tpk);
//        editor.putString("last_kchange", curr_date);
//        editor.commit();
//
//        SecurityLayer.Log("enc_tmk", "enc_tmk >> " + enc_tmk);
//        SecurityLayer.Log("enc_tpk", "enc_tpk >> " + enc_tpk);
//        Load_TLK();
//        Load_Other_Keys();
//    }

//    public void Load_TLK() {
//        if (n900Device.isDeviceAlive()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        byte[] mainKey = null;
//                        mainKey = pinInput.loadMainKey(KekUsingType.PLAIN_KEY, 1, ISOUtils.hex2byte(TLK), null, -1);
//                        if (mainKey != null) {
//                            SecurityLayer.Log("TLK", "Load main key success!");
//                            //Toast.makeText(getActivity(), "Load main key success!",  Toast.LENGTH_SHORT).show();
//                        } else {
//                            SecurityLayer.Log("TLK", "Load main key failed!");
//                        }
//                    } catch (Exception e) {
//                        SecurityLayer.Log("TLK", "Load main key failed! = " + e.getMessage());
//                    }
//                }
//            }).start();
//        }
//    }
//
//    public void Load_Other_Keys() {
//        final int mkIndex = Const.MKIndexConst.DEFAULT_MK_INDEX;
//        if (n900Device.isDeviceAlive()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        byte[] tdkKey = null;
//                        byte[] tpkKey = null;
//
//                        result = pinInput.loadMainKey(KekUsingType.MAIN_KEY, 4, ISOUtils.hex2byte(TDK), null, 1);
//                        SecurityLayer.Log("loadMainKey", "1 dec tdk - " + ISOUtils.hexString(result));
//                        tdkKey = pinInput.loadWorkingKey(WorkingKeyType.DATAENCRYPT, mkIndex, 4, ISOUtils.hex2byte(TDK), null);
//                        SecurityLayer.Log("loadWorkingKey", "dec tdkKey result - " + ISOUtils.hexString(tdkKey));
//
//                        result = pinInput.loadMainKey(KekUsingType.MAIN_KEY, 3, ISOUtils.hex2byte(enc_tmk), null, 1);
//                        SecurityLayer.Log("pinInput", "2 dec tmk - " + ISOUtils.hexString(result));
//
//                        tpkKey = pinInput.loadWorkingKey(WorkingKeyType.PININPUT, 3, 2, ISOUtils.hex2byte(enc_tpk), null);
//                        SecurityLayer.Log("tpkKey", "dec tpkKey result - " + ISOUtils.hexString(tpkKey));
//                        Toast.makeText(getActivity(), "dec tpkKey result - " + ISOUtils.hexString(tpkKey),  Toast.LENGTH_LONG).show();
//
//                    } catch (Exception e) {
//                        SecurityLayer.Log("tpkKey!", " tdkKey >>> " + e.getMessage());
//                    }
//                }
//            }).start();
//        }
//    }

    public void setDialog(String title, String message) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(message)
                .canceledOnTouchOutside(false)
                .negativeText("Dismiss")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }






    public void Print_Reprint() {
        String whole_reprint_data = reprint_data;
        SecurityLayer.Log("whole_reprint_data", whole_reprint_data);
        String[] firstsplit = whole_reprint_data.split(Pattern.quote("^"));
        final String txn_type = firstsplit[0];

        if(txn_type.equals("CASH DEPOSIT")){
            try {
                Print_Cash_Deposit_Reprint();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if(txn_type.equals("AIRTIME")){
            Print_Airtime_Reprint();
        }
        if(txn_type.equals("CARD DEPOSIT") || txn_type.equals("CARD WITHDRAWAL")){
            try {
                Print_Card_Transaction_Reprint();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void Print_Cash_Deposit_Reprint() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        final String send_date, send_time;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);
        String txn_type="",acc_no="",amount="",acc_name="",refcode="",rpc_code="",narra="",auth_id="",rpc_message="",loct="", ednamee="", ednumbb="";
      try {
          String whole_reprint_data = reprint_data;
          SecurityLayer.Log("whole_reprint_data", whole_reprint_data);
          String[] firstsplit = whole_reprint_data.split(Pattern.quote("^"));
          txn_type= firstsplit[0];
           acc_no = firstsplit[1];
           amount = firstsplit[2];
           acc_name = firstsplit[3];
           refcode = firstsplit[4];
           rpc_code = firstsplit[5];
           narra = firstsplit[6];
           auth_id = firstsplit[7];
           rpc_message = firstsplit[8];
           loct = firstsplit[9];
          ednamee= firstsplit[10];
          ednumbb= firstsplit[11];
      } catch (Exception e) {
          e.printStackTrace();
      }
        try {
                recanno = maskString(recanno, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (printer.getStatus() != 0){
                    SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                    return;
                }
                if (printer.initPrinter() != 0) {
                    return ;
                }

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContext().getAssets().open("receipt_logo.bmp"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                printer.appendImage(bitmap);
                int fontSize = FontFamily.MIDDLE;
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(mTypeFacePath)){
                    bundle.putString(PrinterConfig.COMMON_TYPEFACE , mTypeFacePath);
                }
                bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
                printer.setConfig(bundle);

                printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
                printer.appendPrnStr("LOC: " + loct, fontSize, false);
                printer.appendPrnStr("ACC NO: " + acc_no, fontSize, false);
                printer.appendPrnStr("NAME: " + acc_name, fontSize, false);
                printer.appendPrnStr("TID: " + tid + "    MID: " + mid, fontSize, false);
                printer.appendPrnStr("DATE: " + send_date + "        TIME: " + send_time, fontSize, false);
                printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                if (rpc_code.equals("00")) {
                    printer.appendPrnStr("CASH DEPOSIT", fontSize, false);
                } else {
                    printer.appendPrnStr("CASH DEPOSIT(FAILED)", fontSize, false);
                }
                fontSize = FontFamily.BIG;
                printer.appendPrnStr("TOTAL: KES " + amount, fontSize, false);
                fontSize = FontFamily.MIDDLE;
                printer.appendPrnStr("Narration: " + narra, fontSize, false);
                printer.appendPrnStr("Sender Phone: " + ednumbb, fontSize, false);
                printer.appendPrnStr("Sender Name: " + ednamee, fontSize, false);
                if (rpc_code.equals("00")) {
                    fontSize = FontFamily.BIG;
                    printer.appendPrnStr("     APPROVED", fontSize, false);
                }
                fontSize = FontFamily.MIDDLE;

                printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                printer.appendPrnStr("Response Code: " + rpc_code, fontSize, false);
                if (refcode != null) {
                    printer.appendPrnStr("Ref ID: " + refcode, fontSize, false);
                }
                if (!rpc_code.equals("00")) {
                    printer.appendPrnStr("Reason: " + rpc_message, fontSize, false);
                }
                if (rpc_code.equals("00")) {
                    printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
                }
                printer.appendPrnStr("App Version: " + appvers , fontSize, false);
                printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
                printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getContext()), fontSize, false);
                printer.appendPrnStr("      **** MERCHANT COPY ****", fontSize, false);
                printer.appendPrnStr("\n\n", fontSize, false);
                printer.startPrint(new OnPrintListener.Stub() {
                    @Override
                    public void onPrintResult(int ret) throws RemoteException {
                        Log.d("onPrintResult", "onPrintResult = " + ret);
                        //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ApplicationClass.get().getGlobalVals().setReprintData("");

        }

    public void Print_Airtime_Reprint(){
        final String send_date, send_time;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);

        String whole_reprint_data = reprint_data;
        SecurityLayer.Log("whole_reprint_data", whole_reprint_data);
        String[] firstsplit = whole_reprint_data.split(Pattern.quote("^"));
        final String txn_type = firstsplit[0];
        final String mobile_no = firstsplit[1];
        final String amount = firstsplit[2];
        final String acc_name = firstsplit[3];
        final String telcoop = firstsplit[4];
        final String refcode = firstsplit[5];
        final String respcode = firstsplit[6];
        final String narration = firstsplit[7];
        final String auth_id = firstsplit[8];
        final String rpc_message = firstsplit[9];
//        if (n900Device.isDeviceAlive()) {
//            final Printer printer;
//            printer = n900Device.getPrinter();
//            printer.init();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SecurityLayer.Log("Running ", "running Print_Stanbic_Airtime_Receipt thread");
//                    if (printer.getStatus() == PrinterStatus.NORMAL) {
//                        try {
//                            StringBuffer scriptBuffer = new StringBuffer();
//                            String fontsPath = printer.getFontsPath(getActivity(), "arial.ttf", true);
//                            if(fontsPath != null) {
//                                scriptBuffer.append("!font "+fontsPath+"\n");//set ttf font path
//                                SecurityLayer.Log("Font Success", "Font Success");
//                                Map<String, Bitmap> map = new HashMap<String, Bitmap>();
//                                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.stanbic);
//                                String bmp1 = "logo";
//                                map.put(bmp1, bitmap1);
//                                scriptBuffer.append("*image c 380*92 path:" + bmp1 + "\n");//set image large threshold and  align left
//                                scriptBuffer.append("!asc n\n !gray 6\n");//   Set  body content font as medium.
//                                scriptBuffer.append("*text l AGENT:" + mer_name + "\n");
//                                scriptBuffer.append("*text l LOC: " + loc + "\n");
//                                scriptBuffer.append("*text l TID: " + tid + "    MID: " + mid + "\n");
//                                scriptBuffer.append("*text l DATE: " + send_date + "   TIME: " + send_time + "\n");
//                                scriptBuffer.append("*line" + "\n");// Print a dotted line
//                                if(respcode.equals("00")) {
//                                    scriptBuffer.append("*text c AIRTIME\n");
//                                }else{
//                                    scriptBuffer.append("*text c AIRTIME(FAILED)\n");
//                                }
//                                scriptBuffer.append("!asc nl\n !gray 9\n");// Set  font as large.
//                                scriptBuffer.append("*text c TOTAL: KES " + amount + "\n");
//                                scriptBuffer.append("!asc n\n !gray 6\n !yspace 6\n");//   Set  body content font as medium.
//                                scriptBuffer.append("*text l Telco: " + telcoop + "\n");
//                                scriptBuffer.append("*text l Mobile No: " + mobile_no + "\n");
//                                if(respcode.equals("00")) {
//                                    scriptBuffer.append("!asc nl\n !gray 9\n");// Set  font as large.
//                                    scriptBuffer.append("*text c AUTHORIZED\n");
//                                }
//                                scriptBuffer.append("!asc n\n !gray 6\n");//   Set  body content font as medium.
//                                scriptBuffer.append("*line" + "\n");// Print a dotted line
//                                scriptBuffer.append("*text l Response Code: " + respcode + "\n");
//                                /*if(refcode != null) {
//                                    scriptBuffer.append("*text l Reference: " + refcode + "\n");
//                                }*/
//                                if(!respcode.equals("00")) {
//                                    scriptBuffer.append("*text l Reason: " + rpc_message + "\n");
//                                }
//                                /*if(respcode.equals("00")) {
//                                    scriptBuffer.append("*text l AUTH ID: " + auth_id + "\n");
//                                }*/
//                                scriptBuffer.append("*text l App.Version: " + APP_VERSION + "\n");
//                                scriptBuffer.append("*line" + "\n");
//                                scriptBuffer.append("*text l SERVED BY: " + Utility.gettUtilUserId(getActivity())+ "\n");
//                                scriptBuffer.append("!asc n\n !gray 6\n !yspace 30\n");//   Set  body content font as medium.
//                                scriptBuffer.append("*text c ****  REPRINT  ****\n");
//                                scriptBuffer.append("*feedline 2\n");
//                                PrinterResult printerResult = printer.printByScript(PrintContext.defaultContext(),
//                                        scriptBuffer.toString(), map, 60, TimeUnit.SECONDS);
//                            }else{
//                                Log.e("Font Error", "Font Error");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.e("Printer error", "Printer Exception" + e);
//                        }
//                    }
//                }
//            }).start();
//        }else{
//            SecurityLayer.Log("devmanager", "device manager is not alive");
//        }
    }

    public void Print_Card_Transaction_Reprint() throws RemoteException {
        com.morefun.yapi.device.printer.Printer printer = mSDKManager.getPrinter();
        final String send_date, send_time;
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdate_df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat stime_df = new SimpleDateFormat("HH:mm");
        send_date = sdate_df.format(c);
        send_time = stime_df.format(c);
        String txn_type="",cardNo="",prn_amount="",acc_name="",rrn="",narration="",cholder_name="",auth_id="",rpc_message="",stan="",card_label="",aid="",tvr="",prn_fee_amount="";

        String whole_reprint_data = reprint_data;
        SecurityLayer.Log("whole_reprint_data", whole_reprint_data);
        try {
            String[] firstsplit = whole_reprint_data.split(Pattern.quote("^"));
             txn_type = firstsplit[0];
             cardNo = firstsplit[1];
             prn_amount = firstsplit[2];
             acc_name = firstsplit[3];
             rrn = firstsplit[4];
             rpc = firstsplit[5];
             narration = firstsplit[7];
             cholder_name = firstsplit[7];
             auth_id = firstsplit[8];
             rpc_message = firstsplit[9];
             stan = firstsplit[10];
             card_label = firstsplit[10];
             aid = firstsplit[11];
             tvr = firstsplit[12];
            loc=firstsplit[13];
             prn_fee_amount = null;
            if (txn_type.equals("CARD WITHDRAWAL")) {
                prn_fee_amount = firstsplit[14];
            }else if(txn_type.equals("BALANCE INQUIRY")){
                prn_fee_amount = firstsplit[14];
            }else if(txn_type.equals("MINI STATEMENT")){
                prn_fee_amount = firstsplit[14];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        ctyp = pref.getString("ctyp", "");
        if (ctyp != null){
            ctyp=hexToAscii(ctyp);
        }
        //auth_id = ApplicationClass.get().getGlobalVals().getSessAuth();
        amou= prn_amount;//ApplicationClass.get().getGlobalVals().getSessAmt();
        if (amou != null) {
            amou = Utility.returnNumberFormat(amou.substring(0, amou.length() - 2));
        }
        crdN=cardNo;//ApplicationClass.get().getGlobalVals().getSessPan();
        if (crdN != null) {
            crdN=Utility.MaskPan(ApplicationClass.get().getGlobalVals().getSessPan());
            try {
                recanno = maskString(acc_name, 0, 6, '*');
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (printer.getStatus() != 0) {
                SecurityLayer.Log("getStatus", "getStatus = " + (R.string.msg_nopaper));
                return;
            }
            if (printer.initPrinter() != 0) {
                return;
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContext().getAssets().open("receipt_logo.bmp"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            printer.appendImage(bitmap);
            int fontSize = FontFamily.MIDDLE;
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mTypeFacePath)) {
                bundle.putString(PrinterConfig.COMMON_TYPEFACE, mTypeFacePath);
            }else{

            }
            bundle.putInt(PrinterConfig.COMMON_GRAYLEVEL, 15);
            printer.setConfig(bundle);

            printer.appendPrnStr("AGENT: " + mer_name, fontSize, false);
            printer.appendPrnStr("LOC: " + loc, fontSize, false);
            printer.appendPrnStr("TID: " + tid + "  MID: " + mid, fontSize, false);
            printer.appendPrnStr("DATE: " + send_date + "    TIME: " + send_time, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("CARD #: " + crdN, fontSize, false);
            printer.appendPrnStr(cholder_name, fontSize, false);
            fontSize = FontFamily.BIG;

            printer.appendPrnStr(txn_type, fontSize, false);

                printer.appendPrnStr("TOTAL: KES " + amou, fontSize, false);

            fontSize = FontFamily.MIDDLE;
            if (txn_type.equals("CARD WITHDRAWAL")) {
                printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
            }else if(txn_type.equals("BALANCE INQUIRY")) {
                printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
            }else if(txn_type.equals("MINI STATEMENT")){
                printer.appendPrnStr("Fee: KES " + prn_fee_amount, fontSize, false);
            }

            fontSize = FontFamily.BIG;
            printer.appendPrnStr("           DUPLICATE", fontSize, false);
            fontSize = FontFamily.SMALL;
            printer.appendPrnStr("Card: "+ctyp, fontSize, false);
            printer.appendPrnStr("Aid: "+aid, fontSize, false);
//            printer.appendPrnStr("Tsi: "+tsi, fontSize, false)
            printer.appendPrnStr("Tsi: "+"E800", fontSize, false);

            fontSize = FontFamily.MIDDLE;
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("Response Code: " + rpc, fontSize, false);
            printer.appendPrnStr("Ref ID: " + rrn, fontSize, false);
            //if(tntp.equals("CARD WITHDRAWAL")) {
            if (rpc.equals("00")) {
                printer.appendPrnStr("Auth: " + auth_id, fontSize, false);
            }else{
//                printer.appendPrnStr("Reason: " + narration, fontSize, false);
                printer.appendPrnStr("Reason: " + rpc_message, fontSize, false);
            }
            // }
            printer.appendPrnStr("App Version: " + appvers, fontSize, false);
            printer.appendPrnStr("-----------------------------------------------------", fontSize, false);
            printer.appendPrnStr("SERVED BY: " + Utility.gettUtilUserId(getContext()), fontSize, false);
            printer.appendPrnStr("   **** MERCHANT COPY ****", fontSize, false);
            printer.appendPrnStr("\n\n", fontSize, false);
            printer.startPrint(new OnPrintListener.Stub() {
                @Override
                public void onPrintResult(int ret) throws RemoteException {
                    Log.d("onPrintResult", "onPrintResult = " + ret);
                    //listener.showMessage(ret == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
                }
            });


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //Clear data
        ApplicationClass.get().getGlobalVals().setReprintData("");

    }
    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}

