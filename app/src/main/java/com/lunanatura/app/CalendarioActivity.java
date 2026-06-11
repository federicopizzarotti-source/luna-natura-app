package com.lunanatura.app;
 
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
        ImageButton btnMesePrecedente = findViewById(R.id.btnMesePrecedente);
        ImageButton btnMeseSuccessivo = findViewById(R.id.btnMeseSuccessivo);
 
        btnBack.setOnClickListener(v -> finish());
 
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
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("it", "IT"));
        String meseStr = sdf.format(calendarioCorrente.getTime());
        meseStr = meseStr.substring(0, 1).toUpperCase() + meseStr.substring(1);
        tvMeseAnno.setText(meseStr);
 
        gridCalendario.removeAllViews();
 
        // Intestazioni giorni settimana
        String[] giorni = {"Lu", "Ma", "Me", "Gi", "Ve", "Sa", "Do"};
        for (String g : giorni) {
            TextView tv = new TextView(this);
            tv.setText(g);
            tv.setTextSize(13);
            tv.setTextColor(Color.parseColor("#7a6a58"));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = dpToPx(36);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            gridCalendario.addView(tv);
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
 
            boolean isOggi = (giorno == oggiGiorno && meseCorrente == oggiMese && annoCorrente == oggiAnno);
 
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setCornerRadius(dpToPx(8));
            if (isOggi) {
                bg.setColor(Color.parseColor("#2e2820"));
                bg.setStroke(1, Color.parseColor("#c4a882"));
            } else {
                bg.setColor(Color.parseColor("#1e1a14"));
                bg.setStroke(1, Color.parseColor("#2a2218"));
            }
            cell.setBackground(bg);
 
            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(getFaseEmoji(info.fase));
            tvEmoji.setTextSize(18);
            tvEmoji.setGravity(Gravity.CENTER);
 
            TextView tvGiorno = new TextView(this);
            tvGiorno.setText(String.valueOf(giorno));
            tvGiorno.setTextSize(11);
            tvGiorno.setGravity(Gravity.CENTER);
            tvGiorno.setTextColor(isOggi ? Color.parseColor("#e8d5a8") : Color.parseColor("#7a6a58"));
 
            cell.addView(tvEmoji);
            cell.addView(tvGiorno);
 
            // Click: apri dettaglio giorno
            final int giornoFinale = giorno;
            final int meseFinale = meseCorrente;
            final int annoFinale = annoCorrente;
            cell.setOnClickListener(v -> {
                Intent intent = new Intent(this, DettaglioGiornoActivity.class);
                intent.putExtra("giorno", giornoFinale);
                intent.putExtra("mese", meseFinale);
                intent.putExtra("anno", annoFinale);
                startActivity(intent);
            });
 
            gridCalendario.addView(cell);
        }
    }
 
    private String getFaseEmoji(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA: return "🌑";
            case CRESCENTE_FALCE: return "🌒";
            case PRIMO_QUARTO: return "🌓";
            case CRESCENTE_GIBBOSA: return "🌔";
            case LUNA_PIENA: return "🌕";
            case CALANTE_GIBBOSA: return "🌖";
            case ULTIMO_QUARTO: return "🌗";
            case CALANTE_FALCE: return "🌘";
            default: return "🌑";
        }
    }
 
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
