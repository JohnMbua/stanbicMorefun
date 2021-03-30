package com.stanbicagent;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stanbicagent.R;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.NavDrawerPOJO;
import adapter.NavigationDrawerAdapter;
import adapter.adapter.NavDrawerItemSignIn;
import de.hdodenhof.circleimageview.CircleImageView;
import security.SecurityLayer;


public class SupFragmentDrawer extends Fragment implements View.OnClickListener {
File finalFile;
    private static String TAG = SupFragmentDrawer.class.getSimpleName();
TextView tv,home;

    RelativeLayout header;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
   static public List<NavDrawerPOJO> planetsList = new ArrayList<NavDrawerPOJO>();
    CircleImageView iv;
    ProgressDialog prgDialog,prgDialog2;
    public static TypedArray navMenuIcons,navIcpad;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    SessionManagement session;
    private  static  String[] titles = null;
    private  static int [] dricons = null;
private static ArrayList<String> vd = new ArrayList<String>();
    protected static ArrayList<Integer> vimges = new ArrayList<Integer>();
    protected static ArrayList<Integer> vicpad = new ArrayList<Integer>();
    private static Integer[] icons = null;
    private static Integer[] iconspad = null;
    String uploadFileName = null,userChoosenTask;
    int SELECT_FILE = 345;
    int REQUEST_CAMERA =3293;
    private FragmentDrawerListener drawerListener;

    public SupFragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItemSignIn> getData() {
        List<NavDrawerItemSignIn> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < vd.size(); i++) {
            NavDrawerItemSignIn navItem = new NavDrawerItemSignIn(vd.get(i),vimges.get(i),vicpad.get(i));

            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      titles = getActivity().getResources().getStringArray(R.array.nav_drawer_items);
       dricons = getActivity().getResources().getIntArray(R.array.nav_drawer_icons);
        session = new SessionManagement(getActivity());
vd.clear();
        vd.add("Home");
        vd.add("Top Up Agent");


        vd.add("Agent Performance");
        vd.add("My Profile");


        vd.add("Sign Out");








        vimges.clear();
        vimges.add(R.drawable.home);
        vimges.add(R.drawable.deposit);


        vimges.add(R.drawable.report);
        vimges.add(R.drawable.myprofile);
        vimges.add(R.drawable.ccare);

        vimges.add(R.drawable.logout);



vicpad.clear();
        vicpad.add(0);
        vicpad.add(0);
        vicpad.add(0);
        vicpad.add(0);
        vicpad.add(0);

     //   CheckServicesBool();

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);


        navIcpad = getResources()
                .obtainTypedArray(R.array.nav_pad_icons);
//CheckServicesBool();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        //This is the code to provide a sectioned list
        header = (RelativeLayout) layout.findViewById(R.id.nav_header_container);
        tv = (TextView) layout.findViewById(R.id.section_text);
        String usid = Utility.gettUtilUserId(getActivity());

     //   scroll = (ImageView) layout.findViewById(R.id.scroll);

     //   padl = (ImageView) layout.findViewById(R.id.pad);
        iv = (CircleImageView) layout.findViewById(R.id.profile_image);
      //  iv.setImageBitmap(null);
        iv.setOnClickListener(this);
        boolean checklg = false;
     //   padl.setBackgroundResource(0);
        checklg = session.checkLogin();
        HashMap<String, String> no = session.getMobNo();
      String  numb = no.get(SessionManagement.KEY_MOBILE);

        HashMap<String, Integer> nmob = session.getCurrTheme();
        int pos = nmob.get(SessionManagement.KEY_THEME);

        if(pos == 1){
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        if(pos == 2){

        }
        if(pos == 3){
            header.setBackgroundResource(R.drawable.goldimg);
        }
        if(pos == 4){
            header.setBackgroundResource(R.drawable.red);

        }
        // uploadFileName = numb;
        uploadFileName = numb;
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Loading....");
        prgDialog.setCancelable(false);
        if(checklg == true) {



        }else{
 }

       recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


            }
        });
        home = (TextView) layout.findViewById(R.id.home);
        home.setOnClickListener(this);
        tv.setOnClickListener(this);
        HashMap<String, String> dis = session.getDisp();
        String names = dis.get(SessionManagement.KEY_DISP);


        HashMap<String, String> sessname = session.getUserDetails();
        String sesn = sessname.get(SessionManagement.KEY_USERID);



        if(!(names == null || names.equals(" "))){
            names = names.replace("_"," ");
          /*  if(names.contains(" ")) {
                String fname = names.substring(0, names.indexOf(" "));
                String lname = names.substring(names.lastIndexOf(" "), names.length());
                SecurityLayer.Log("Fname is", fname);
                SecurityLayer.Log("Lname is", lname);

                tv.setText("Hi " + lname);
            }else{*/
                tv.setText("Hi " + names);

            }else{
                if(!(sesn == null)){
                    if(sesn.contains(" ")) {
                        String fname = sesn.substring(0,sesn.indexOf(" "));
                        String lname = sesn.substring(sesn.lastIndexOf(" "),sesn.length() );
                        SecurityLayer.Log(" SessFname is", fname);
                        SecurityLayer.Log("L Sessname is", lname);

                        tv.setText("Hi "+lname);
                    }else {
                        tv.setText("Hi " + sesn);
                    }
            }
        }
        recyclerView.setAdapter(adapter);
        //recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
             //  invalidateOptionsMenu();

                Utility.hideKeyboardFrom(getActivity(),drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
               //invalidateOptionsMenu();
                Utility.hideKeyboardFrom(getActivity(),drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
               // toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }
public void showDialog(){
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
            getActivity(),
            android.R.layout.select_dialog_singlechoice);
    arrayAdapter.add("Camera");
    arrayAdapter.add("Gallery");


    builderSingle.setNegativeButton(
            "cancel",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

    builderSingle.setAdapter(
            arrayAdapter,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                    } else if (which == 1) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                    }
                }
            });
    builderSingle.show();
}
    public Dialog imageAlertDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity()).setAdapter(new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_list_item_1,
                        new String[] { "Camera", "Gallery" }),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                        } else if (which == 1) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                        }
                    }
                });

        return builder.create();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result) {
                        boolean camresult= Utility.checkCameraPermission(getActivity());
                        if(camresult) {
                            cameraIntent();
                        }

                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result) {

                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo")) {

                            cameraIntent();

                    }

                } else {
                    //code for deny
                }
                break;
        }
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void SetDialog(String msg,String title){
        new MaterialDialog.Builder(getActivity())

                .title(title)
                .content(msg)

                .negativeText("Close")
                .show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                finalFile  = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis()+".jpg");
                FileOutputStream fo;
                try {
                    finalFile.createNewFile();
                    fo = new FileOutputStream(finalFile);
                    fo.write(bytes.toByteArray());
                    fo.close();
                   /* new Thread(new Runnable() {
                        public void run() {


                            uploadFile(finalFile);

                        }
                    }).start();*/
                   new AsyncUplImg().execute("");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    //    iv.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
       finalFile = new File(Environment.getExternalStorageDirectory(),
               System.currentTimeMillis()+".jpg");
        FileOutputStream fo;
        try {
            finalFile.createNewFile();
            fo = new FileOutputStream(finalFile);
            fo.write(bytes.toByteArray());
            fo.close();
           /* new Thread(new Runnable() {
                public void run() {


                    uploadFile(finalFile);

                }
            }).start();*/
              new AsyncUplImg().execute();
           /* getActivity().finish();
            Toast.makeText(
                    getActivity(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(),FMobActivity.class));*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

     //   iv.setImageBitmap(thumbnail);



    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.home){

       drawerListener.onDrawerItemSelected(view, 40);
            mDrawerLayout.closeDrawer(containerView);
        }

        if(view.getId() == R.id.txt2) {

            drawerListener.onDrawerItemSelected(view, 41);
            mDrawerLayout.closeDrawer(containerView);
        }

        if(view.getId() == R.id.txt3) {

            drawerListener.onDrawerItemSelected(view, 42);
            mDrawerLayout.closeDrawer(containerView);
        }
        if(view.getId() == R.id.txt4) {


            drawerListener.onDrawerItemSelected(view, 43);
            mDrawerLayout.closeDrawer(containerView);
        }
        if(view.getId() == R.id.txt5) {

            drawerListener.onDrawerItemSelected(view, 44);
            mDrawerLayout.closeDrawer(containerView);
        }
        if(view.getId() == R.id.profile_image) {
           /* drawerListener.onDrawerItemSelected(view, 40);
            mDrawerLayout.closeDrawer(containerView);*/
       //  imageAlertDialog();
          //  SetDialog("Test","Test");
          //  showDialog();
            selectImage();
        }
        if(view.getId() == R.id.section_text) {
            drawerListener.onDrawerItemSelected(view, 40);
            mDrawerLayout.closeDrawer(containerView);
            Fragment fragment = null;
            String title = null;
            boolean checklg = false;
            checklg = session.checkLogin();

            if (fragment != null) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //  String tag = Integer.toString(title);
                fragmentTransaction.replace(R.id.container_body, fragment,title);
                fragmentTransaction.addToBackStack(title);
                fragmentTransaction.commit();
                ((MainActivity)getActivity())
                        .setActionBarTitle(title);
            }
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }



    class AsyncUplImg extends AsyncTask<String, String, String> {
        Bitmap bmp = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog.show();
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(String... f_url) {


            return "34";
        }

        @Override
        protected void onPostExecute(String file_url) {
prgDialog.dismiss();
            Toast.makeText(
                    getActivity(),
                    "Image Set Successfully",
                    Toast.LENGTH_LONG).show();
            getActivity().finish();

            startActivity(new Intent(getActivity(),FMobActivity.class));

        }
    }




}
