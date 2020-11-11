package com.example.exchangerates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.exchangerates.entity.ExchangeRate;
import com.example.exchangerates.entity.ExchangeRateData;
import com.example.exchangerates.service.NetworkService;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView selectedDatePrivatBank, selectedDateNationalBank;
    private ImageView openDatePickerPrivatBank, openDatePickerNationalBank;
    private DatePickerDialog privatDatePickerDialog;
    private ExchangeRateData currentExchangeRateData;
    private Calendar calendar;
    private String currentDate;
    private ListView privatBankCurrency, nationalBankCurrency;
    private ArrayAdapter<String> adapterPB;
    private ArrayAdapter<String> adapterNB;
    private ArrayList<String> listElementsArrayListPB;
    private  ArrayList<String> listElementsArrayListNB;
    private int ind;
    private ArrayList<String> arrPrivatBalkCurrencyLabels;
    private int[] indexOFPricesNationalBank = {3, 5, 7, 8, 15, 16, 22};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDatePickerPrivatBank = findViewById(R.id.openDatePickerPrivatBank);
        openDatePickerNationalBank = findViewById(R.id.openDatePickerNationalBank);
        privatBankCurrency = findViewById(R.id.privatBankCurrency);
        nationalBankCurrency = findViewById(R.id.nationalBankCurrency);
        selectedDatePrivatBank = findViewById(R.id.selectedDatePrivatBank);
        selectedDateNationalBank = findViewById(R.id.selectedDateNationalBank);

        calendar = Calendar.getInstance();

        currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." +  calendar.get(Calendar.YEAR);
        selectedDateNationalBank.setText(currentDate);
        selectedDatePrivatBank.setText(currentDate);

        privatDatePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        privatDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

        calendar.set(2014, 11, 1);

        privatDatePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        openDatePickerPrivatBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privatDatePickerDialog.show();
            }
        });

        openDatePickerNationalBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privatDatePickerDialog.show();
            }
        });

        updateData();
    }

    private void updateData() {

        arrPrivatBalkCurrencyLabels = new ArrayList<>();

        getCurrencyPrices(currentDate);

        nationalBankCurrency.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        privatBankCurrency.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        privatBankCurrency.setSelector(android.R.color.darker_gray);

        listElementsArrayListPB = new ArrayList<>();
        listElementsArrayListNB = new ArrayList<>();

        adapterPB = new ArrayAdapter<>
                (MainActivity.this, android.R.layout.simple_list_item_1, listElementsArrayListPB);
        privatBankCurrency.setAdapter(adapterPB);

        adapterNB = new ArrayAdapter<>
                (MainActivity.this, android.R.layout.simple_list_item_1, listElementsArrayListNB);
        nationalBankCurrency.setAdapter(adapterNB);

        privatBankCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ind = arrPrivatBalkCurrencyLabels.indexOf(((String) parent.getItemAtPosition(position)).substring(0, 3));

                nationalBankCurrency.clearFocus();
                nationalBankCurrency.post(new Runnable() {
                    @Override
                    public void run() {
                        nationalBankCurrency.setSelection(indexOFPricesNationalBank[ind]);
                    }
                });
            }
        });
    }

    private void addItem (ExchangeRate exchangeRate, String bank) {

        if(bank.equals("privat")) {
            arrPrivatBalkCurrencyLabels.add(exchangeRate.getCurrency());
            listElementsArrayListPB.add(exchangeRate.getCurrency() + "             " + exchangeRate.getPurchaseRate() + "             " + exchangeRate.getSaleRate());
            adapterPB.notifyDataSetChanged();
        }
        else if(bank.equals("national")) {
            listElementsArrayListNB.add(exchangeRate.getCurrency() + "             " + exchangeRate.getPurchaseRateNB() + "             " + exchangeRate.getSaleRateNB());
            adapterNB.notifyDataSetChanged();
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {

            selectedDatePrivatBank.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
            selectedDateNationalBank.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
            updateData();
        }
    };

    private void getCurrencyPrices(String selectedDate) {

        NetworkService.getInstance()
                .getJSONApi()
                .getPost(selectedDate)
                .enqueue(new Callback<ExchangeRateData>() {
                    @Override
                    public void onResponse(@NonNull Call<ExchangeRateData> call, @NonNull Response<ExchangeRateData> response) {
                        currentExchangeRateData = response.body();

                        for(int i = 0; i<currentExchangeRateData.getExchangeRate().size(); i++){

                            if(currentExchangeRateData.getExchangeRate().get(i).getCurrency() != null) {
                                addItem(currentExchangeRateData.getExchangeRate().get(i), "national");
                            }

                            if((currentExchangeRateData.getExchangeRate().get(i).getPurchaseRate()) == 0.0D) {
                                continue;
                            }
                            addItem(currentExchangeRateData.getExchangeRate().get(i), "privat");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ExchangeRateData> call, @NonNull Throwable t) {

                    }
                });
    }
}
