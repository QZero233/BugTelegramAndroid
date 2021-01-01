package com.qzero.telegram.http;

import android.content.Context;

import com.qzero.telegram.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

import okhttp3.OkHttpClient;
import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitHelper {

    private Logger log= LoggerFactory.getLogger(getClass());

    public String serverBaseUrl;

    private static RetrofitHelper instance;
    private Context context;

    private Retrofit mRetrofit;

    private RetrofitHelper(Context context) {
        this.context = context;
        initRetrofit();
    }

    public static RetrofitHelper getInstance(Context context) {
        if(instance==null)
            instance=new RetrofitHelper(context);
        return instance;
    }

    private void initRetrofit(){
        serverBaseUrl= context.getString(R.string.server_base_url);

        X509Certificate certificate = Certificates.decodeCertificatePem(
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIIDfzCCAmegAwIBAgIEO3kiLTANBgkqhkiG9w0BAQsFADBwMRAwDgYDVQQGEwdV\n" +
                        "bmtub3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYD\n" +
                        "VQQKEwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMRQwEgYDVQQDEwtCdWdUZWxl\n" +
                        "Z3JhbTAeFw0yMDEyMjcwMzI4NTRaFw0yMTAzMjcwMzI4NTRaMHAxEDAOBgNVBAYT\n" +
                        "B1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24xEDAO\n" +
                        "BgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xFDASBgNVBAMTC0J1Z1Rl\n" +
                        "bGVncmFtMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjING4Kt+vV2u\n" +
                        "k6Wfq1RfFswVEAGI3gbbzqAKhMmKu9e5eVZhoymLRdZw4RO/PON/27NS4gHb8cyC\n" +
                        "Eoscnl4rZqsPhpY5byRUr2UX0vR1v3ywQuhLKlgijIFcCNkIpN93aIaUcun7N3ff\n" +
                        "Rr2OvqkeO/YL3Vx+NRah2o5EGh5E2Hny9PSV3vujQS+1S5LgRyRpJLKTyJ/kJzZH\n" +
                        "0av9TXXL3XLP4E1H5QgHzK66DnxNbXSoxPpJ/J0T8DoPHH6dByWLCvKDl/i0X52E\n" +
                        "zCusdKMUIwMW7zch15H93GuyKTw7VZ9h443ebeigkHH15EBe1Tza7/5RaKh5Gusv\n" +
                        "oa/pp85whwIDAQABoyEwHzAdBgNVHQ4EFgQUvrNB1Jl8PomqE7nSeDdkRqWbaGEw\n" +
                        "DQYJKoZIhvcNAQELBQADggEBAIJb8sr3XJ0xD/SizVcYBGmVR15AfEsk8Y63fLjo\n" +
                        "vrebcl9D3MZOd8jWduhzbSBcNkgjJGbX12CoZtJDrCTXcMQjfjcYnemF5qrQ91zq\n" +
                        "h310PGAfafyY3NV99twN93hU8OEIdlR9bRrInw3NiVL6psr2Je8yV51x3v/llPFF\n" +
                        "MSDhQg/F9WYbyEGQJ+/YIA8m8TPl/y3VH9wEzvB68QxtI/d4lq1mBPa772abX2kP\n" +
                        "/NYSJiNzobtmJGZzlKPFMR7tmWbFcAucuMZzgNxscDO02psL+xDgJhUz+8kbuC9N\n" +
                        "RPvoSDlVuo84sbUyTiVbCnQY3c+qz25WVpQy5H75YQs44Wc=\n" +
                        "-----END CERTIFICATE-----");

        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(certificate)
                .build();

        OkHttpClient client=new OkHttpClient.Builder()
                .addNetworkInterceptor(new TokenInterceptor(context))
                .sslSocketFactory(certificates.sslSocketFactory(),certificates.trustManager())
                .hostnameVerifier(((hostname, session) -> {
                    log.trace("Verifying hostname "+hostname);
                    return hostname.equals(context.getString(R.string.server_ip));
                }))
                .build();

        mRetrofit=new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();
    }

    public <T> T getService(Class<T> cls) {
        return mRetrofit.create(cls);
    }

}
