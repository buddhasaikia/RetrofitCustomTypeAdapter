import android.content.Context;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Buddha Saikia on 05-06-2017.
 */

@Module
public class ApplicationModule {
    private String mBaseUrl;
    private Context mContext;

    public ApplicationModule(Context context, String baseUrl) {
        this.mContext = context;
        this.mBaseUrl = baseUrl;
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Singleton
    @Provides
    StethoInterceptor provideStthoInterceptor() {
        return new StethoInterceptor();
    }

    @Singleton
    @Provides
    GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Singleton
    @Provides
    @Named("ok-1")
    OkHttpClient provideOkHttpClient1(HttpLoggingInterceptor loggingInterceptor,
                                      StethoInterceptor stethoInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Singleton
    @Provides
    @Named("ok-2")
    OkHttpClient provideOkHttpClient2(HttpLoggingInterceptor loggingInterceptor,
                                      StethoInterceptor stethoInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    @Singleton
    @Provides
    RxJava2CallAdapterFactory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(@Named("ok-1") OkHttpClient okHttpClient,
                             GsonConverterFactory converterFactory,
                             RxJava2CallAdapterFactory adapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(Student.class, new StudentDeserializer()).create()))
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(okHttpClient)
                .build();
    }


    @Singleton
    @Provides
    Context provideContext() {
        return mContext;
    }

    @Singleton
    @Provides
    ErrorMessageFactory provideErrorMessageFactory(Context context) {
        return new ErrorMessageFactory(context);
    }

    @Singleton
    @Provides
    ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Singleton
    @Provides
    RemoteDataSource provideRemoteDataSource(ApiService apiService) {
        return new RemoteDataSource(apiService);
    }

    @Singleton
    @Provides
    RealmDB provideRealmDB() {
        return new RealmDB();
    }

    @Singleton
    @Provides
    LocalDataSource provideLocalDataSource(RealmDB realmDB) {
        return new LocalDataSource(realmDB);
    }

    @Singleton
    @Provides
    Repository provideRepository(Context context, RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        return new Repository(context, remoteDataSource, localDataSource);
    }
}
