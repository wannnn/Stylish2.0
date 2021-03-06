package app.waynechen.stylish.data.source.remote;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import app.waynechen.stylish.MainMvpController;
import app.waynechen.stylish.R;
import app.waynechen.stylish.Stylish;
import app.waynechen.stylish.api.StylishApiHelper;
import app.waynechen.stylish.api.exception.StylishException;
import app.waynechen.stylish.api.exception.StylishInvalidTokenException;
import app.waynechen.stylish.data.CheckOutInfo;
import app.waynechen.stylish.data.source.StylishDataSource;
import app.waynechen.stylish.data.source.bean.GetMarketingHots;
import app.waynechen.stylish.data.source.bean.GetProductList;
import app.waynechen.stylish.data.source.task.GetUserProfileTask;
import app.waynechen.stylish.data.source.task.UserSignInTask;
import app.waynechen.stylish.util.Constants;

import java.io.IOException;

/**
 * Created by Wayne Chen on Feb. 2019.
 */
public class StylishRemoteDataSource implements StylishDataSource {

    public static final int DEFAULT       = 0x06;
    public static final int SUCCESS       = 0x07;
    public static final int ERROR         = 0x08;
    public static final int INVALID_TOKEN = 0x09;

    private static StylishRemoteDataSource INSTANCE;

    public static StylishRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StylishRemoteDataSource();
        }
        return INSTANCE;
    }

    private StylishRemoteDataSource() {}

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getHotsList(@NonNull GetHotsListCallback callback) {

        new AsyncTask<Void, Void, GetMarketingHots>() {

            private String mErrorMessage = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected GetMarketingHots doInBackground(Void... voids) {

                GetMarketingHots bean = null;

                try {
                    bean = StylishApiHelper.getMarketingHots();
                } catch (IOException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                } catch (StylishInvalidTokenException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                } catch (StylishException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                }
                return bean;
            }

            @Override
            protected void onPostExecute(GetMarketingHots bean) {
                super.onPostExecute(bean);

                if (bean != null) {

                    callback.onCompleted(bean);
                } else if (!"".equals(mErrorMessage)) {

                    callback.onError(mErrorMessage);
                } else {

                    Log.d(Constants.TAG, "GetMarketingHots fail");
                }
            }
        }.execute();
    }

    @Override
    public void getProductList(@MainMvpController.CatalogItem String itemType, int paging,
                               @NonNull GetProductListCallback callback) {

        new AsyncTask<Void, Void, GetProductList>() {

            private String mErrorMessage = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected GetProductList doInBackground(Void... voids) {

                GetProductList bean = null;

                try {
                    bean = StylishApiHelper.getProductList(itemType, paging);
                } catch (IOException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                } catch (StylishInvalidTokenException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                } catch (StylishException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                }
                return bean;
            }

            @Override
            protected void onPostExecute(GetProductList bean) {
                super.onPostExecute(bean);

                if (bean != null) {

                    callback.onCompleted(bean);
                } else if (!"".equals(mErrorMessage)) {

                    callback.onError(mErrorMessage);
                } else {

                    Log.d(Constants.TAG, "GetProductList fail");
                }
            }

        }.execute();
    }

    @Override
    public void postUserSignIn(@NonNull String token,
                               @NonNull UserSignInCallback callback) {

        new UserSignInTask(token, callback).execute();
    }

    @Override
    public void getUserProfile(@NonNull String token, @NonNull GetUserProfileCallback callback) {

        new GetUserProfileTask(token, callback).execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void postOrderCheckOutCallback(@NonNull String token, @NonNull CheckOutInfo checkOutInfo, @NonNull OrderCheckOutCallback callback) {

        new AsyncTask<Void, Void, String>() {

            private int mStatus = DEFAULT;
            private String mErrorMessage = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                String bean = null;

                try {
                    bean = StylishApiHelper.postOrderCheckOut(token, checkOutInfo);
                    mStatus = SUCCESS;

                } catch (IOException e) {

                    mErrorMessage = e.getMessage();
                    mStatus = ERROR;
                    e.printStackTrace();

                } catch (StylishInvalidTokenException e) {

                    mErrorMessage = e.getMessage();
                    mStatus = INVALID_TOKEN;
                    e.printStackTrace();

                } catch (StylishException e) {

                    mErrorMessage = e.getMessage();
                    mStatus = ERROR;
                    e.printStackTrace();
                }
                return bean;
            }

            @Override
            protected void onPostExecute(String bean) {
                super.onPostExecute(bean);

                if (bean != null && mStatus == SUCCESS) {

                    callback.onCompleted(bean);

                } else if (!"".equals(mErrorMessage)) {

                    if (mStatus == INVALID_TOKEN) {

                        callback.onInvalidToken(mErrorMessage);
                    } else {

                        callback.onError(mErrorMessage);
                    }
                } else {

                    Log.d(Constants.TAG, "postOrderCheckOut fail");
                    callback.onError(Constants.GENERAL_ERROR);
                }
            }
        }.execute();
    }

    @Override
    public String getUserInformation(@NonNull String key) {
        return null;
    }
}
