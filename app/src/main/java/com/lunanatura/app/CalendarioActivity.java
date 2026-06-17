package com.lunanatura.app;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
 
public class CalendarioActivity extends AppCompatActivity {
 
    private TextView tvMeseAnno;
    private GridLayout gridCalendario;
    private Calendar calendarioCorrente;
    private int oggiGiorno, oggiMese, oggiAnno;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
 
        tvMeseAnno = findViewById(R.id.tvMeseAnno);
        gridCalendario = findViewById(R.id.gridCalendario);
 
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView btnMesePrecedente = findViewById(R.id.btnMesePrecedente);
        TextView btnMeseSuccessivo = findViewById(R.id.btnMeseSuccessivo);
 
        btnBack.setOnClickListener(v -> finish());
 
        // Sfondo verde salvia per pulsanti cambio mese da colors.xml
        android.graphics.drawable.GradientDrawable bgBtn =
            MainActivity.creaRoundDrawable(
                ContextCompat.getColor(this, R.color.btn_mese_sfondo),
                ContextCompat.getColor(this, R.color.btn_mese_bordo),
                dpToPx(8), 1);
        btnMesePrecedente.setBackground(bgBtn);
        btnMeseSuccessivo.setBackground(bgBtn);
        btnMesePrecedente.setTextColor(ContextCompat.getColor(this, R.color.btn_mese_testo));
        btnMeseSuccessivo.setTextColor(ContextCompat.getColor(this, R.color.btn_mese_testo));
 
        Calendar oggi = Calendar.getInstance();
        oggiGiorno = oggi.get(Calendar.DAY_OF_MONTH);
        oggiMese = oggi.get(Calendar.MONTH);
        oggiAnno = oggi.get(Calendar.YEAR);
 
        calendarioCorrente = Calendar.getInstance();
        calendarioCorrente.set(Calendar.DAY_OF_MONTH, 1);
 
        btnMesePrecedente.setOnClickListener(v -> {
            calendarioCorrente.add(Calendar.MONTH, -1);
            aggiornaMese();
        });
        btnMeseSuccessivo.setOnClickListener(v -> {
            calendarioCorrente.add(Calendar.MONTH, 1);
            aggiornaMese();
        });
 
        aggiornaMese();
    }
 
    private void aggiornaMese() {
        String localeCode = getString(R.string.locale_code);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale(localeCode));
        String meseStr = sdf.format(calendarioCorrente.getTime());
        meseStr = meseStr.substring(0, 1).toUpperCase() + meseStr.substring(1);
        tvMeseAnno.setText(meseStr);
 
        gridCalendario.removeAllViews();
 
        // Intestazioni giorni settimana localizzate
        Calendar tmp = (Calendar) calendarioCorrente.clone();
        SimpleDateFormat sdfGiorno = new SimpleDateFormat("EEE", new Locale(localeCode));
        tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            String label = sdfGiorno.format(tmp.getTime());
            label = label.length() >= 2 ? label.substring(0, 2) : label;
            label = label.substring(0, 1).toUpperCase() + label.substring(1);
            TextView tv = new TextView(this);
            tv.setText(label);
            tv.setTextSize(13);
            tv.setTextColor(ContextCompat.getColor(this, R.color.testo_muto));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = dpToPx(36);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            gridCalendario.addView(tv);
            tmp.add(Calendar.DAY_OF_WEEK, 1);
        }
 
        Calendar cal = (Calendar) calendarioCorrente.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int primoGiorno = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (primoGiorno == Calendar.SUNDAY) ? 6 : primoGiorno - 2;
 
        for (int i = 0; i < offset; i++) {
            View empty = new View(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = dpToPx(56);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            empty.setLayoutParams(lp);
            gridCalendario.addView(empty);
        }
 
        int giorniNelMese = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int meseCorrente = cal.get(Calendar.MONTH);
        int annoCorrente = cal.get(Calendar.YEAR);
 
        for (int giorno = 1; giorno <= giorniNelMese; giorno++) {
            cal.set(Calendar.DAY_OF_MONTH, giorno);
            LunaCalcolo.InfoLuna info = LunaCalcolo.calcolaFase(cal.getTime());
 
            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
 
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = dpToPx(56);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
            cell.setLayoutParams(lp);
 
            boolean isOggi = (giorno == oggiGiorno
                && meseCorrente == oggiMese
                && annoCorrente == oggiAnno);
 
            // Colori celle da colors.xml
            cell.setBackground(MainActivity.creaRoundDrawable(
                ContextCompat.getColor(this,
                    isOggi ? R.color.cal_oggi_sfondo : R.color.cal_cella_sfondo),
                ContextCompat.getColor(this,
                    isOggi ? R.color.cal_oggi_bordo : R.color.cal_cella_bordo),
                dpToPx(8), isOggi ? 2 : 1));
 
            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(getFaseEmoji(info.fase));
            tvEmoji.setTextSize(18);
            tvEmoji.setGravity(Gravity.CENTER);
 
            TextView tvGiorno = new TextView(this);
            tvGiorno.setText(String.valueOf(giorno));
            tvGiorno.setTextSize(11);
            tvGiorno.setGravity(Gravity.CENTER);
            tvGiorno.setTextColor(ContextCompat.getColor(this,
                isOggi ? R.color.testo_principale : R.color.testo_muto));
 
            cell.addView(tvEmoji);
            cell.addView(tvGiorno);
 
            final int gf = giorno, mf = meseCorrente, af = annoCorrente;
            cell.setOnClickListener(v -> {
                Intent intent = new Intent(this, DettaglioGiornoActivity.class);
                intent.putExtra("giorno", gf);
                intent.putExtra("mese", mf);
                intent.putExtra("anno", af);
                startActivity(intent);
            });
 
            gridCalendario.addView(cell);
        }
    }
 
    private String getFaseEmoji(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA:        return "🌑";
            case CRESCENTE_FALCE:   return "🌒";
            case PRIMO_QUARTO:      return "🌓";
            case CRESCENTE_GIBBOSA: return "🌔";
            case LUNA_PIENA:        return "🌕";
            case CALANTE_GIBBOSA:   return "🌖";
            case ULTIMO_QUARTO:     return "🌗";
            case CALANTE_FALCE:     return "🌘";
            default:                return "🌑";
        }
    }
 
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
