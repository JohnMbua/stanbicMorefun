package rest;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.stanbicagent.ApplicationConstants;
import com.stanbicagent.R;
import com.stanbicagent.SessionManagement;
import com.stanbicagent.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import security.SecurityLayer;


public class ApiSecurityClient {
    public static final String BASE_URL = ApplicationConstants.NET_URL;
    private static Retrofit retrofit = null;

    public static Retrofit getUnsafeClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            OkHttpClient okHttpClient = builder
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
          //  return okHttpClient;

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(okHttpClient)
                        .build();
            }

            return retrofit;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Retrofit getClient(Context ct) {
        OkHttpClient okHttpClient = null;
        if((ApplicationConstants.PROD_ENV.equals("Y"))) {
            final String userid = Utility.gettUtilUserId(ct);
            int count = 0;
            SessionManagement sess = new SessionManagement(ct);
           final String appid =  Utility.getNewAppID(ct);
            final String baseimage = Utility.getBase64Image(ct);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                                          @Override
                                          public okhttp3.Response intercept(Chain chain) throws IOException {
                                              Request original = chain.request();
                                              Request request = null;
                                              String appidnew = appid;
                                              String useridnew = userid;
                                              if(appid == null){
                                                  appidnew = "N";
                                              }
                                              if(userid == null){
                                                  useridnew = "N";
                                              }

                                              if(!(baseimage.equals("N"))) {


                                                  request = original.newBuilder()
                                                          .header("man", useridnew)
                                                          .header("serial", appidnew)
                                                          .header("base64", baseimage)
                                                          .method(original.method(), original.body())
                                                          .build();
                                              }else{
                                                  request = original.newBuilder()
                                                          .header("man", useridnew)
                                                          .header("serial", appidnew)

                                                          .method(original.method(), original.body())
                                                          .build();
                                              }

                                              return chain.proceed(request);
                                          }
                                      });
        builder.readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS);


        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

// Can be Level.BASIC, Level.HEADERS, or Level.BODY
// See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(ApplicationConstants.HOSTNAME, "sha256/rqU8h4fgcUQ/pRFO98oK5FD8k9zcSWDoRMDke2hjaQc=")
                .add(ApplicationConstants.HOSTNAME, "sha256/5kJvNEMw0KjrCAu7eXY5HZdvyCS13BbA0VJG1RSP91w=")
                .add(ApplicationConstants.HOSTNAME, "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")

                .build();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        builder.certificatePinner(certificatePinner);
            SSLContext sslcontext = null;
            SSLSocketFactory NoSSLv3Factory = null;
            try {
                sslcontext = SSLContext.getInstance("TLSv1.2");
                sslcontext.init(null, null, null);
               NoSSLv3Factory = new NoSSLv3SocketFactory();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory sslSocketFactory = null;
            try {
                sslSocketFactory = new TLSSocketFactory();

            } catch (KeyManagementException ignored) {

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            okHttpClient = builder.build();

        /*    ConnectionSpec spec = new
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .buid
                    ();*/

           /* okHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .build();*/

           // okHttpClient = builder.build();
            SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS");

          /*  OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .cache(null)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                enableTls12OnPreLollipop(client).build();
                SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS version 22");
            }else{*/


          /*  SSLSocketFactory sslSocketFactory = null;
            try {
                sslSocketFactory = new TLSSocketFactory();

            } catch (KeyManagementException ignored) {

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
            } catch (GeneralSecurityException e) {
                throw new AssertionError(); // The system has no TLS. Just give up.
            }*/

                SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS version after 24");


          //  }






       }else   if((ApplicationConstants.PROD_ENV.equals("N"))) {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager

                SSLSocketFactory sslSocketFactory = null;
                try {
                    sslSocketFactory = new TLSSocketFactory();

                } catch (KeyManagementException ignored) {

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
              //  final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                final String userid = Utility.gettUtilUserId(ct);
                final String baseimage = Utility.getBase64Image(ct);
                System.out.println("baseimage [" + baseimage+ "]");
                int count = 0;
                SessionManagement sess = new SessionManagement(ct);
                final String appid = Utility.getNewAppID(ct);


                builder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = null;
                        String appidnew = appid;
                        String useridnew = userid;
                        if(appid == null){
                            appidnew = "N";
                        }
                        if(userid == null){
                            useridnew = "N";
                        }
                                if(!(baseimage.equals("N"))) {


                                    request = original.newBuilder()
                                            .header("man", useridnew)
                                            .header("serial", appidnew)
                               .header("base64", baseimage)
                                            .method(original.method(), original.body())
                                            .build();
                                }else{
                                    request = original.newBuilder()
                                            .header("man", useridnew)
                                            .header("serial", appidnew)

                                            .method(original.method(), original.body())
                                            .build();
                                }


                        return chain.proceed(request);
                    }
                });
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
                okHttpClient = builder
                        .connectTimeout(240, TimeUnit.SECONDS)
                        .writeTimeout(240, TimeUnit.SECONDS)
                        .readTimeout(240, TimeUnit.SECONDS)
                        .build();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else   if((ApplicationConstants.PROD_ENV.equals("PREPROD"))) {

            SecurityLayer.Log("ProdGhana");
            OkHttpClient client = null;

            CertificateFactory cf = null;
            InputStream cert = null;
            Certificate ca = null;
            SSLContext sslContext = null;
            try {
                cf = CertificateFactory.getInstance("X.509");
                cert = ct.getResources().openRawResource(R.raw.zenithcert); // Place your 'my_cert.crt' file in `res/raw`

                ca = cf.generateCertificate(cert);
                cert.close();

                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

                logging.setLevel(HttpLoggingInterceptor.Level.BODY);


                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory())
                        .addInterceptor(logging)
                        .connectTimeout(240, TimeUnit.SECONDS)
                        .writeTimeout(240, TimeUnit.SECONDS)
                        .readTimeout(240, TimeUnit.SECONDS)
                        .build();

            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
                e.printStackTrace();
            }

            okHttpClient = client;

        }
        else   if((ApplicationConstants.PROD_ENV.equals("DEV"))) {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager

                SSLSocketFactory sslSocketFactory = null;
                try {
                    sslSocketFactory = new TLSSocketFactory();

                } catch (KeyManagementException ignored) {

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //  final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                final String userid = Utility.gettUtilUserId(ct);
                final String baseimage = Utility.getBase64Image(ct);
                System.out.println("baseimage [" + baseimage+ "]");
                int count = 0;
                SessionManagement sess = new SessionManagement(ct);
                final String appid = Utility.getNewAppID(ct);


                builder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = null;
                        String appidnew = appid;
                        String useridnew = userid;
                        if(appid == null){
                            appidnew = "N";
                        }
                        if(userid == null){
                            useridnew = "N";
                        }
                        if(!(baseimage.equals("N"))) {


                            request = original.newBuilder()
                                    .header("man", useridnew)
                                    .header("serial", appidnew)
                                    .header("base64", baseimage)
                                    .method(original.method(), original.body())
                                    .build();
                        }else{
                            request = original.newBuilder()
                                    .header("man", useridnew)
                                    .header("serial", appidnew)

                                    .method(original.method(), original.body())
                                    .build();
                        }


                        return chain.proceed(request);
                    }
                });
                // Install the all-trusting trust manager
                final SSLContext sslContextdev = SSLContext.getInstance("SSL");
                sslContextdev.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactorydev = sslContextdev.getSocketFactory();


                builder.sslSocketFactory(sslSocketFactorydev);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });


              /*  builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });*/
                okHttpClient = builder
                        .connectTimeout(240, TimeUnit.SECONDS)
                        .writeTimeout(240, TimeUnit.SECONDS)
                        .readTimeout(240, TimeUnit.SECONDS)
                        .build();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getClientBase64(Context ct, final String base64) {
        OkHttpClient okHttpClient = null;
        if((ApplicationConstants.PROD_ENV.equals("Y"))) {
            final String userid = Utility.gettUtilUserId(ct);
            int count = 0;
            SessionManagement sess = new SessionManagement(ct);
            final String appid = Utility.getNewAppID(ct);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("man", userid)
                            .header("serial", appid)
                            .header("base64", base64)
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
            builder.readTimeout(240, TimeUnit.SECONDS).connectTimeout(240, TimeUnit.SECONDS).writeTimeout(240, TimeUnit.SECONDS);


            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

// Can be Level.BASIC, Level.HEADERS, or Level.BODY
// See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add(ApplicationConstants.HOSTNAME, "sha256/rqU8h4fgcUQ/pRFO98oK5FD8k9zcSWDoRMDke2hjaQc=")
                    .add(ApplicationConstants.HOSTNAME, "sha256/5kJvNEMw0KjrCAu7eXY5HZdvyCS13BbA0VJG1RSP91w=")
                    .add(ApplicationConstants.HOSTNAME, "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")

                    .build();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.networkInterceptors().add(httpLoggingInterceptor);
            builder.certificatePinner(certificatePinner);
            SSLContext sslcontext = null;
            SSLSocketFactory NoSSLv3Factory = null;
            try {
                sslcontext = SSLContext.getInstance("TLSv1.2");
                sslcontext.init(null, null, null);
                NoSSLv3Factory = new NoSSLv3SocketFactory();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory sslSocketFactory = null;
            try {
                sslSocketFactory = new TLSSocketFactory();

            } catch (KeyManagementException ignored) {

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            okHttpClient = builder.build();

        /*    ConnectionSpec spec = new
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .buid
                    ();*/

           /* okHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .build();*/

            // okHttpClient = builder.build();
            SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS");

          /*  OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .cache(null)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                enableTls12OnPreLollipop(client).build();
                SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS version 22");
            }else{*/


          /*  SSLSocketFactory sslSocketFactory = null;
            try {
                sslSocketFactory = new TLSSocketFactory();

            } catch (KeyManagementException ignored) {

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
            } catch (GeneralSecurityException e) {
                throw new AssertionError(); // The system has no TLS. Just give up.
            }*/

            SecurityLayer.Log("OkHttpTLSCompat", "Vefore TLS version after 24");


            //  }






        }else {
            try {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager

                SSLSocketFactory sslSocketFactory = null;
                try {
                    sslSocketFactory = new TLSSocketFactory();

                } catch (KeyManagementException ignored) {

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //  final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                final String userid = Utility.gettUtilUserId(ct);

                final String appid =  Utility.getNewAppID(ct);

                builder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        System.out.println("base 64 shown [" + base64 + "]");
                        Request request = original.newBuilder()
                                .header("man", userid)
                                .header("serial", appid)
                                .header("Cookie","Dummy Base64")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });
                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                okHttpClient = builder
                        .connectTimeout(240, TimeUnit.SECONDS)
                        .writeTimeout(240, TimeUnit.SECONDS)
                        .readTimeout(240, TimeUnit.SECONDS)
                        .build();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }
    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            Log.i("OkHttpTLSCompat", "Inside enableTS");
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);


                client.connectionSpecs(specs);
            } catch (Exception exc) {
                SecurityLayer.Log("OkHttpTLSCompatError while setting TLS 1.2", exc.toString());
            }
        }

        return client;
    }
}