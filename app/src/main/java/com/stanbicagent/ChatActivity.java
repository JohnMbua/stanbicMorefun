package com.stanbicagent;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.stanbicagent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.ChatAdapter;
import adapter.ChatMessage;
import model.Chat;
import model.GetChatData;
import rest.ApiClient;
import rest.ApiInterface;
import rest.ApiSecurityClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import security.SecurityLayer;

import static com.stanbicagent.ApplicationConstants.CHANNEL_ID;

public class ChatActivity extends BaseActivity {
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory = new ArrayList<ChatMessage>();
    ProgressDialog pDialog;
    String stracno, stramo, strefno, strdatee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading");
        pDialog.setCancelable(false);
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        initControls();

        if (!(getIntent().getExtras() == null)) {
            stracno = getIntent().getExtras().getString("txaco");
            stramo = getIntent().getExtras().getString("txamo");
            strefno = getIntent().getExtras().getString("txref");
            strdatee = getIntent().getExtras().getString("txdate");
            messageET.setText("Account No: " + stracno + " Amount " + stramo + " Reference Number " + strefno + " Date " + strdatee);

        }

    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);


        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        //   loadDummyHistory();
        LoadChats();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
/*
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(false);

                messageET.setText("");

                displayMessage(chatMessage);*/


                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                String endpoint = "chat/savechat.action";


                String usid = Utility.gettUtilUserId(getApplicationContext());
                String agentid = Utility.gettUtilAgentId(getApplicationContext());
                String mobnoo = Utility.gettUtilMobno(getApplicationContext());
                String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo + "/" + messageText;
                /* /chat/savechat.action/{channel}/{userId}/{merchantId}/{mobileNumber}/{message}*/
                String urlparams = "";
                try {
                    urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
                    //SecurityLayer.Log("cbcurl",url);
                    SecurityLayer.Log("RefURL", urlparams);
                    SecurityLayer.Log("refurl", urlparams);
                    SecurityLayer.Log("params", params);
                } catch (Exception e) {
                    SecurityLayer.Log("encryptionerror", e.toString());
                }


                ApiInterface apiService2 =
                        ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


                Call<String> call = apiService2.setGenericRequestRaw(urlparams);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        /*SecurityLayer.Log("Chat Resp", response.body());
                        SecurityLayer.Log("response..:", response.body());*/
                        JSONObject obj = null;


                        //obj = Utility.onresp(obj,getActivity());
                        try {
                            obj = new JSONObject(response.body());

                            obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SecurityLayer.Log("decrypted_response", obj.toString());


                        String respcode = obj.optString("responseCode");
                        String responsemessage = obj.optString("message");


//                                    SecurityLayer.Log("Respnse getResults",datas.toString());
                        if (respcode.equals("00")) {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId(122);//dummy
                            chatMessage.setMessage(messageText);
                            chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                            chatMessage.setMe(false);

                            messageET.setText("");

                            displayMessage(chatMessage);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    responsemessage,
                                    Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Log error here since request failed
                        SecurityLayer.Log("Throwable error", t.toString());
                        Toast.makeText(
                                getApplicationContext(),
                                "There was an error processing your request",
                                Toast.LENGTH_LONG).show();

                    }
                });


            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void LoadChats() {

        String endpoint = "chat/loadchat.action";


        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        String params = CHANNEL_ID + usid + "/" + agentid + "/" + mobnoo;


        String urlparams = "";
        try {
            urlparams = SecurityLayer.genURLCBC(params, endpoint, getApplicationContext());
            //SecurityLayer.Log("cbcurl",url);
            SecurityLayer.Log("RefURL", urlparams);
            SecurityLayer.Log("refurl", urlparams);
            SecurityLayer.Log("params", params);
        } catch (Exception e) {
            SecurityLayer.Log("encryptionerror", e.toString());
        }


        ApiInterface apiService =
                ApiSecurityClient.getClient(getApplicationContext()).create(ApiInterface.class);


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
                    obj = SecurityLayer.decryptTransaction(obj, getApplicationContext());
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

                                    if (servdata.length() > 0) {
                                        int s = 1;
                                        JSONObject json_data = null;
                                        for (int i = 0; i < servdata.length(); i++) {
                                            json_data = servdata.getJSONObject(i);
                                            String id = json_data.optString("id");
                                            String getmessage = json_data.optString("message");
                                            String getcreatedtime = json_data.optString("creationDate");
                                            String getrespsmessage = json_data.optString("responseMessage");
                                            String getresptime = json_data.optString("responseTime");
                                            String makerid = json_data.optString("makerId");
                                            if (Utility.isNotNull(getmessage) || !(getmessage.equals(""))) {

                          /* ChatMessage msg = new ChatMessage();
                           msg.setId(1);
                           msg.setMe(false);
                           msg.setMessage("Want a raise a dispute in a recent transaction");
                           msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                           chatHistory.add(msg);*/

                                                SecurityLayer.Log("Sender msg", getmessage);

                                                ChatMessage msg = new ChatMessage();
                                                msg.setId(s);
                                                msg.setMe(false);
                                                msg.setMessage(getmessage);
                                                msg.setDate(getcreatedtime);
                                                chatHistory.add(msg);
                                                s++;
                                            }
                                            if (Utility.isNotNull(getrespsmessage) && !(getrespsmessage.equals("null"))) {
                                                ChatMessage msgg = new ChatMessage();
                                                msgg.setId(s);
                                                msgg.setMe(true);
                                                msgg.setMessage(getrespsmessage + " -" + makerid);
                                                msgg.setDate(getresptime);
                                                chatHistory.add(msgg);
                                                s++;

                                                SecurityLayer.Log("Response msg", getrespsmessage);
                                            }

                                        }

                                        messagesContainer.setAdapter(adapter);

                                        for (int i = 0; i < chatHistory.size(); i++) {
                                            ChatMessage message = chatHistory.get(i);
                                            displayMessage(message);
                                        }

                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No chats to show",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {
                                if (!(getApplicationContext() == null)) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error on your request",
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        } else {
                            if (!(getApplicationContext() == null)) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "There was an error on your request",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                        // prgDialog2.dismiss();

                    }


                } catch (JSONException e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // TODO Auto-generated catch block
                    if (!(getApplicationContext() == null)) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getText(R.string.conn_error), Toast.LENGTH_LONG).show();
                        // SecurityLayer.Log(e.toString());
                    }

                } catch (Exception e) {
                    SecurityLayer.Log("encryptionJSONException", e.toString());
                    // SecurityLayer.Log(e.toString());
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                // Log error here since request failed
                SecurityLayer.Log("throwable error", t.toString());

                if (!(getApplicationContext() == null)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error on your request",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void loadDummyHistory() {
        pDialog.show();
        chatHistory = new ArrayList<ChatMessage>();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String usid = Utility.gettUtilUserId(getApplicationContext());
        String agentid = Utility.gettUtilAgentId(getApplicationContext());
        String mobnoo = Utility.gettUtilMobno(getApplicationContext());
        Call<Chat> call = apiService.getChat("1", usid, agentid, "0000");
        call.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(Call<Chat> call, Response<Chat> response) {

                List<GetChatData> dataaa = response.body().getResults();

//                                    SecurityLayer.Log("Respnse getResults",datas.toString());

                if (!(dataaa == null)) {
                    int s = 1;
                    for (int i = 0; i < dataaa.size(); i++) {
                        String id = dataaa.get(i).getid();
                        String getmessage = dataaa.get(i).getmessage();
                        String getcreatedtime = dataaa.get(i).getcreationDate();
                        String getrespsmessage = dataaa.get(i).getresponseMessage();
                        String getresptime = dataaa.get(i).getresponseTime();
                        String makerid = dataaa.get(i).getmakerId();
                        if (Utility.isNotNull(getmessage)) {

                          /* ChatMessage msg = new ChatMessage();
                           msg.setId(1);
                           msg.setMe(false);
                           msg.setMessage("Want a raise a dispute in a recent transaction");
                           msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                           chatHistory.add(msg);*/

                            SecurityLayer.Log("Sender msg", getmessage);

                            ChatMessage msg = new ChatMessage();
                            msg.setId(s);
                            msg.setMe(false);
                            msg.setMessage(getmessage);
                            msg.setDate(getcreatedtime);
                            chatHistory.add(msg);
                            s++;
                        }
                        if (Utility.isNotNull(getrespsmessage)) {
                            ChatMessage msgg = new ChatMessage();
                            msgg.setId(s);
                            msgg.setMe(true);
                            msgg.setMessage(getrespsmessage + " -" + makerid);
                            msgg.setDate(getresptime);
                            chatHistory.add(msgg);
                            s++;

                            SecurityLayer.Log("Response msg", getrespsmessage);
                        }

                    }
                    adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
                    messagesContainer.setAdapter(adapter);

                    for (int i = 0; i < chatHistory.size(); i++) {
                        ChatMessage message = chatHistory.get(i);
                        displayMessage(message);
                    }

                } else {

                }


                pDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable t) {
                // Log error here since request failed
                SecurityLayer.Log("Throwable error", t.toString());
                Toast.makeText(
                        getApplicationContext(),
                        "There was an error processing your request",
                        Toast.LENGTH_LONG).show();

            }
        });


        pDialog.dismiss();


    }

}
