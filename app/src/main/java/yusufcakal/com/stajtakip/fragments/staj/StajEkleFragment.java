package yusufcakal.com.stajtakip.fragments.staj;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import yusufcakal.com.stajtakip.R;
import yusufcakal.com.stajtakip.adapter.firma.CustomSpinnerAdapter;
import yusufcakal.com.stajtakip.pojo.Firma;
import yusufcakal.com.stajtakip.pojo.Staj;
import yusufcakal.com.stajtakip.webservices.interfaces.FirmalarListeleListener;
import yusufcakal.com.stajtakip.webservices.interfaces.FragmentListener;
import yusufcakal.com.stajtakip.webservices.interfaces.StajEkleListener;
import yusufcakal.com.stajtakip.webservices.services.FirmalarService;
import yusufcakal.com.stajtakip.webservices.services.StajEkleService;
import yusufcakal.com.stajtakip.webservices.util.SessionUtil;

/**
 * Created by Yusuf on 21.05.2018.
 */

public class StajEkleFragment extends android.support.v4.app.Fragment implements
        FirmalarListeleListener,
        View.OnClickListener,
        StajEkleListener{

    private List<Firma> firmaList = new ArrayList<>();
    private View view;
    private Spinner spinner;
    private Button btnBaslangicTarih, btnBitisTarih, btnStajEkle;
    private int firmaId;
    private String strStajBaslangicTarih, strStajBitisTarih;
    private FragmentListener fragmentListener;
    private Staj staj;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stajekle, container, false);

        spinner = view.findViewById(R.id.spinner);
        btnBaslangicTarih = view.findViewById(R.id.btnBaslangicTarih);
        btnBitisTarih = view.findViewById(R.id.btnBitisTarih);
        btnStajEkle = view.findViewById(R.id.btnStajEkle);
        btnBaslangicTarih.setOnClickListener(this);
        btnBitisTarih.setOnClickListener(this);
        btnStajEkle.setOnClickListener(this);

        FirmalarService firmalarService = new FirmalarService(getContext(), this);
        firmalarService.getFirmalar();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentListener = (FragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentListener = null;
    }

    @Override
    public void onSuccess(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            boolean resultFlag = jsonObject.getBoolean("result");
            if (resultFlag){
                JSONArray firmaListArray = jsonObject.getJSONArray("list");
                for (int i=0; i<firmaListArray.length(); i++){
                    JSONObject firmaObject = firmaListArray.getJSONObject(i);
                    int id = firmaObject.getInt("id");
                    int onay = firmaObject.getInt("onay");
                    String adi = firmaObject.getString("adi");
                    Firma firma = new Firma(onay, adi);
                    firma.setId(id);
                    firmaList.add(firma);
                }

                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, firmaList);
                customSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(customSpinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        firmaId = firmaList.get(i).getId();
                        Log.e("FİRMA ID", String.valueOf(firmaId));
                        Log.e("Bölüm ID", String.valueOf(SessionUtil.getBolumId(getContext())));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }

    private void datePickerBaslangic(){
        Calendar calendar = Calendar.getInstance();
        final  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btnBaslangicTarih.setText(dateFormatter.format(newDate.getTime()));
                strStajBaslangicTarih = dateFormatter.format(newDate.getTime());
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void datePickerBitis(){
        Calendar calendar = Calendar.getInstance();
        final  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                btnBitisTarih.setText(dateFormatter.format(newDate.getTime()));
                strStajBitisTarih = dateFormatter.format(newDate.getTime());
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnBaslangicTarih){
            datePickerBaslangic();
            //updateBaslangicLabel();
        }else if (view == btnBitisTarih){
            datePickerBitis();
            //updateBitisLabel();
        }else if (view == btnStajEkle){
            stajEkle();
            Log.e("TARİHLER", "BAŞLANGIÇ : " + staj.getBaslangicTarihi() + " BİTİŞ : " + staj.getBitisTarihi());
        }
    }

    private void stajEkle(){
        staj = new Staj();
        staj.setBaslangicTarihi(strStajBaslangicTarih);
        staj.setBitisTarihi(strStajBitisTarih);
        staj.setFirmaId(firmaId);

        StajEkleService stajEkleService = new StajEkleService(getContext(), this);
        stajEkleService.stajEkle(staj);

    }

    @Override
    public void onSuccessStaj(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("result")){
                fragmentListener.onStart(new StajlarFragment());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorStaj(VolleyError error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }
}
