package com.payu.payuui.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Interfaces.ValueAddedServiceApiListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.payu.india.Tasks.ValueAddedServiceTask;
import com.payu.payuui.Adapter.PagerAdapter;
import com.payu.payuui.Adapter.SavedCardItemFragmentAdapter;
import com.payu.payuui.Fragment.CreditDebitFragment;
import com.payu.payuui.Fragment.SavedCardItemFragment;
import com.payu.payuui.R;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;
import com.payu.payuui.Widget.SwipeTab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;


public class PayUBaseActivity extends FragmentActivity implements  PaymentRelatedDetailsListener,ValueAddedServiceApiListener, View.OnClickListener {

    public Bundle bundle;
    ArrayList<String> paymentOptionsList = new ArrayList<String>();
    ArrayList<String> paymentOptionsSet = new ArrayList<String>();
    PayuConfig payuConfig;
    PaymentParams mPaymentParams;
    PayuHashes mPayUHashes;
    PayuResponse mPayuResponse;
    PayuUtils mPayuUtils;
    PayuResponse valueAddedResponse;
    private int mTabCount;
    public PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private TextView amountTextView;
    private TextView transactionIdTextView;
    private Button payNowButton;
    private Spinner spinnerNetbanking;
    private String bankCode;
    private ArrayList<PaymentDetails> netBankingList;
    private PostData postData;
    private ValueAddedServiceTask valueAddedServiceTask;
    private ArrayList<StoredCard> savedCards;
    private Boolean smsPermission;
    private PostData mPostData;
    HashMap<String, String> oneClickCardTokens;
    private int storeOneClickHash;
    ImageView backfrompayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payu_base);


        (payNowButton = (Button) findViewById(R.id.button_pay_now)).setOnClickListener(this);
        backfrompayment = (ImageView) findViewById(R.id.backfrompayment);
        backfrompayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bundle = getIntent().getExtras();

        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

        mPayuUtils = new PayuUtils();


        // TODO add null pointer check here
//        mPaymentDefaultParams = bundle.getParcelable(PayuConstants.PAYMENT_DEFAULT_PARAMS);
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS); // Todo change the name to PAYMENT_PARAMS
        mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);

        storeOneClickHash = bundle.getInt(PayuConstants.STORE_ONE_CLICK_HASH);
        smsPermission = bundle.getBoolean(PayuConstants.SMS_PERMISSION);

        oneClickCardTokens = (HashMap<String, String>) bundle.getSerializable(PayuConstants.ONE_CLICK_CARD_TOKENS);


        (amountTextView = (TextView) findViewById(R.id.textview_amount)).setText(SdkUIConstants.AMOUNT + ": " + mPaymentParams.getAmount());
        (transactionIdTextView = (TextView) findViewById(R.id.textview_txnid)).setText(SdkUIConstants.TXN_ID + ": " + mPaymentParams.getTxnId());


        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());


        merchantWebService.setHash(mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash());
        Log.d("mPayUHashesOne",mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash()+"");
        // Dont fetch the data if calling activity is PaymentActivity

        // fetching for the first time.
        if (null == savedInstanceState) { // dont fetch the data if its been called from payment activity.
            PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // ok we got the post params, let make an api call to payu to fetch the payment related details
                payuConfig.setData(postData.getResult());

//                 lets set the visibility of progress bar
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
                paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
            } else {
              //  Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
//                 close the progress bar
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onValueAddedServiceApiResponse(PayuResponse payuResponse) {
        valueAddedResponse = payuResponse;
        if(mPayuResponse != null)
            setupViewPagerAdapter(mPayuResponse, valueAddedResponse);
    }

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;

        if(valueAddedResponse != null)
            setupViewPagerAdapter(mPayuResponse, valueAddedResponse);

        MerchantWebService valueAddedWebService = new MerchantWebService();
        valueAddedWebService.setKey(mPaymentParams.getKey());
        valueAddedWebService.setCommand(PayuConstants.VAS_FOR_MOBILE_SDK);
        valueAddedWebService.setHash(mPayUHashes.getVasForMobileSdkHash());
        Log.d("mPayUHashesTwo",mPayUHashes.getVasForMobileSdkHash()+"");
        valueAddedWebService.setVar1(PayuConstants.DEFAULT);
        valueAddedWebService.setVar2(PayuConstants.DEFAULT);
        valueAddedWebService.setVar3(PayuConstants.DEFAULT);

        if((postData = new MerchantWebServicePostParams(valueAddedWebService).getMerchantWebServicePostParams()) != null&& postData.getCode() == PayuErrors.NO_ERROR ){
            payuConfig.setData(postData.getResult());
            valueAddedServiceTask = new ValueAddedServiceTask(this);
            valueAddedServiceTask.execute(payuConfig);
        }else{
          //  Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }


    }



    private void setupViewPagerAdapter(final PayuResponse payuResponse, PayuResponse valueAddedResponse) {

        if (payuResponse.isResponseAvailable() && payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) { // ok we are good to go
       //     Toast.makeText(this, payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();

            if (payuResponse.isStoredCardsAvailable()) {
                paymentOptionsList.add(SdkUIConstants.SAVED_CARDS);

            }

            if (payuResponse.isCreditCardAvailable() || payuResponse.isDebitCardAvailable()) {
                paymentOptionsList.add(SdkUIConstants.CREDIT_DEBIT_CARDS);
            }

            if (payuResponse.isNetBanksAvailable()) { // okay we have net banks now.
                paymentOptionsList.add(SdkUIConstants.NET_BANKING);
            }

            if (payuResponse.isPaisaWalletAvailable() && payuResponse.getPaisaWallet().get(0).getBankCode().contains(PayuConstants.PAYUW)) {
                paymentOptionsList.add(SdkUIConstants.PAYU_MONEY);
            }

        } else {
            Toast.makeText(this, "Something went wrong : " + payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), paymentOptionsList, payuResponse, valueAddedResponse, storeOneClickHash, oneClickCardTokens);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        slidingTabLayout.setDistributeEvenly(false);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabLayout.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (paymentOptionsList.get(position)) {
                    case SdkUIConstants.SAVED_CARDS:
                        ViewPager myViewPager = (ViewPager) findViewById(R.id.pager_saved_card);
                        int currentPosition = ((ViewPager) findViewById(R.id.pager_saved_card)).getCurrentItem();
                        savedCards = payuResponse.getStoredCards();
                        if (savedCards.size() == 0) {
                            payNowButton.setEnabled(false);
                            break;
                        }
//                        if (savedCards.get(currentPosition).getEnableOneClickPayment() == 1 && !mPayuUtils.getFromSharedPreferences(PayUBaseActivity.this, savedCards.get(currentPosition).getCardToken()).contentEquals(PayuConstants.DEFAULT)) {
//                            payNowButton.setEnabled(true);
                        /*if (storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_MOBILE && savedCards.get(currentPosition).getEnableOneClickPayment() == 1 && !mPayuUtils.getFromSharedPreferences(PayUBaseActivity.this, savedCards.get(currentPosition).getCardToken()).contentEquals(PayuConstants.DEFAULT)) {
                            payNowButton.setEnabled(true);
                        } else*/
                        if (storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && savedCards.get(currentPosition).getEnableOneClickPayment() == 1 && savedCards.get(currentPosition).getOneTapCard() == 1) {
                            payNowButton.setEnabled(true);
                        } else if (savedCards.get(currentPosition).getCardType().equals("SMAE")) {
                            payNowButton.setEnabled(true);
                        } else {
                            SavedCardItemFragmentAdapter mSaveAdapter = (SavedCardItemFragmentAdapter) myViewPager.getAdapter();
                            SavedCardItemFragment mSaveFragment = mSaveAdapter.getFragment(currentPosition);

                            if (mSaveFragment != null && mSaveFragment.cvvValidation()) {
                                payNowButton.setEnabled(true);
                            } else if (mSaveFragment == null) {


                            } else {
                                payNowButton.setEnabled(false);
                            }
                        }
                        break;
                    case SdkUIConstants.CREDIT_DEBIT_CARDS:
                        PagerAdapter mPagerAdapter = (PagerAdapter) viewPager.getAdapter();
                        CreditDebitFragment tempCreditDebitFragment = (CreditDebitFragment) mPagerAdapter.getFragment(position);
                        tempCreditDebitFragment.checkData();
                        break;
                    case SdkUIConstants.NET_BANKING:
                        payNowButton.setEnabled(true);
                        hide_keyboard();
                        break;
                    case SdkUIConstants.PAYU_MONEY:
                        payNowButton.setEnabled(true);
                        hide_keyboard();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.progress_bar).setVisibility(View.GONE);

    }
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_pay_now) {

            mPostData = null;

            mPaymentParams.setHash(mPayUHashes.getPaymentHash());
            Log.d("mPayUHashesThree",mPayUHashes.getPaymentHash()+"");

            switch (paymentOptionsList.get(viewPager.getCurrentItem())) {
                case SdkUIConstants.SAVED_CARDS:
                    makePaymentByStoredCard();
                    break;
                case SdkUIConstants.CREDIT_DEBIT_CARDS:
                    makePaymentByCreditCard();
                    break;
                case SdkUIConstants.NET_BANKING:
                    makePaymentByNB();
                    break;
                case SdkUIConstants.CASH_CARDS:
                    break;
                case SdkUIConstants.EMI:
                    break;
                case SdkUIConstants.PAYU_MONEY:
                    makePaymentByPayUMoney();
                    break;
            }



            if (mPostData.getCode() == PayuErrors.NO_ERROR) {
                payuConfig.setData(mPostData.getResult());

                Intent intent = new Intent(this, PaymentsActivity.class);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH, storeOneClickHash);
                intent.putExtra(PayuConstants.SMS_PERMISSION, smsPermission);
                startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
            } else {
                Toast.makeText(this, mPostData.getResult(), Toast.LENGTH_LONG).show();

            }
        }


    }

    private void makePaymentByPayUMoney() {

        try {
            mPostData = new PaymentPostParams(mPaymentParams, PayuConstants.PAYU_MONEY).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void makePaymentByCreditCard() {
        CheckBox saveCardCheckBox = (CheckBox) findViewById(R.id.check_box_save_card);
        CheckBox enableOneClickPaymentCheckBox = (CheckBox) findViewById(R.id.check_box_enable_oneclick_payment);

        if (saveCardCheckBox.isChecked()) {
            mPaymentParams.setStoreCard(1);
        } else {
            mPaymentParams.setStoreCard(0);
        }

        if (storeOneClickHash == 1 && enableOneClickPaymentCheckBox.isChecked()) {
            mPaymentParams.setEnableOneClickPayment(1);// TODO set flag for save cvv
        } else {
            mPaymentParams.setEnableOneClickPayment(0);
        }


        // lets try to get the post params

        mPaymentParams.setCardNumber(((EditText) findViewById(R.id.edit_text_card_number)).getText().toString().replace(" ", ""));
        mPaymentParams.setNameOnCard(((EditText) findViewById(R.id.edit_text_name_on_card)).getText().toString());
        mPaymentParams.setExpiryMonth(((EditText) findViewById(R.id.edit_text_expiry_month)).getText().toString());
        mPaymentParams.setExpiryYear(((EditText) findViewById(R.id.edit_text_expiry_year)).getText().toString());
        mPaymentParams.setCvv(((EditText) findViewById(R.id.edit_text_card_cvv)).getText().toString());

        if (mPaymentParams.getStoreCard() == 1 && !((EditText) findViewById(R.id.edit_text_card_label)).getText().toString().equals(""))
            mPaymentParams.setCardName(((EditText) findViewById(R.id.edit_text_card_label)).getText().toString());
        else if(mPaymentParams.getStoreCard() == 1 && ((EditText) findViewById(R.id.edit_text_card_label)).getText().toString().equals(""))
            mPaymentParams.setCardName(((EditText) findViewById(R.id.edit_text_name_on_card)).getText().toString());

        try {
            mPostData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void makePaymentByNB() {

        spinnerNetbanking = (Spinner) findViewById(R.id.spinner);

        ArrayList<PaymentDetails> netBankingList = mPayuResponse.getNetBanks();


        bankCode = netBankingList.get(spinnerNetbanking.getSelectedItemPosition()).getBankCode();

        mPaymentParams.setBankCode(bankCode);

        try {
            mPostData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void makePaymentByStoredCard() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_saved_card);
        StoredCard selectedStoredCard = mPayuResponse.getStoredCards().get(viewPager.getCurrentItem());
        SavedCardItemFragmentAdapter mSaveAdapter = (SavedCardItemFragmentAdapter) viewPager.getAdapter();
        SavedCardItemFragment mSaveFragment = mSaveAdapter.getFragment(viewPager.getCurrentItem());
        String cvv = mSaveFragment.getCvv();

        // lets try to get the post params
        selectedStoredCard.setCvv(cvv); // make sure that you set the cvv also


        mPaymentParams.setCardToken(selectedStoredCard.getCardToken());
        mPaymentParams.setNameOnCard(selectedStoredCard.getNameOnCard());
        mPaymentParams.setCardName(selectedStoredCard.getCardName());
        mPaymentParams.setExpiryMonth(selectedStoredCard.getExpiryMonth());
        mPaymentParams.setExpiryYear(selectedStoredCard.getExpiryYear());

        String merchantHash;
        if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER && selectedStoredCard.getOneTapCard() == 1 ) {
            merchantHash = oneClickCardTokens.get(selectedStoredCard.getCardToken());
        }
        /*else if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_MOBILE) {
            merchantHash = mPayuUtils.getFromSharedPreferences(PayUBaseActivity.this, selectedStoredCard.getCardToken());
        }*/
        else {
            merchantHash = PayuConstants.DEFAULT;
        }

        if(selectedStoredCard.getEnableOneClickPayment() == 1 && !merchantHash.contentEquals(PayuConstants.DEFAULT)){
            mPaymentParams.setCardCvvMerchant(merchantHash);
        }else{
            mPaymentParams.setCvv(cvv);
        }

        if(mSaveFragment.isEnableOneClickPaymentChecked()){
            mPaymentParams.setEnableOneClickPayment(1);
        }

        try {
            mPostData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }

    }

    public void hide_keyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(PayUBaseActivity.this);
        builder.setTitle("Cancel Transaction");
        builder.setMessage("Pressing back would cancel your current transaction. Proceed to cancel?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

}
