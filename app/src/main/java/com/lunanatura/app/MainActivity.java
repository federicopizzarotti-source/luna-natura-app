package com.lunanatura.app;
 
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
 
public class MainActivity extends AppCompatActivity {
 
    private LunaView lunaView;
    private TextView tvFaseNome, tvFaseSottotitolo, tvCicloInfo, tvDataOggi;
    private TextView tvCapelliPillola, tvCapelliTesto;
    private TextView tvLegnaPillola, tvLegnaTesto, tvLegnaProssimo;
    private LinearLayout layoutLegnaProssimo;
    private TextView tvSeminaTesto;
    private TextView[] tvMiniCardLabel, tvMiniCardVal;
    private LinearLayout dotContainer;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        aggiornaConFaseLunare();
        findViewById(R.id.btnCalendario).setOnClickListener(v ->
            startActivity(new Intent(this, CalendarioActivity.class))
        );
    }
 
    private void initViews() {
        lunaView = findViewById(R.id.lunaView);
        tvFaseNome = findViewById(R.id.tvFaseNome);
        tvFaseSottotitolo = findViewById(R.id.tvFaseSottotitolo);
        tvCicloInfo = findViewById(R.id.tvCicloInfo);
        tvDataOggi = findViewById(R.id.tvDataOggi);
        tvCapelliPillola = findViewById(R.id.tvCapelliPillola);
        tvCapelliTesto = findViewById(R.id.tvCapelliTesto);
        tvLegnaPillola = findViewById(R.id.tvLegnaPillola);
        tvLegnaTesto = findViewById(R.id.tvLegnaTesto);
        tvLegnaProssimo = findViewById(R.id.tvLegnaProssimo);
        layoutLegnaProssimo = findViewById(R.id.layoutLegnaProssimo);
        tvSeminaTesto = findViewById(R.id.tvSeminaTesto);
        dotContainer = findViewById(R.id.dotContainer);
        tvMiniCardLabel = new TextView[]{
            findViewById(R.id.tvMC1Label), findViewById(R.id.tvMC2Label),
            findViewById(R.id.tvMC3Label), findViewById(R.id.tvMC4Label)
        };
        tvMiniCardVal = new TextView[]{
            findViewById(R.id.tvMC1Val), findViewById(R.id.tvMC2Val),
            findViewById(R.id.tvMC3Val), findViewById(R.id.tvMC4Val)
        };
    }
 
    private void aggiornaConFaseLunare() {
        Date oggi = new Date();
        LunaCalcolo.InfoLuna info = LunaCalcolo.calcolaFase(oggi);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("it", "IT"));
        String dataStr = sdf.format(oggi);
        dataStr = dataStr.substring(0, 1).toUpperCase() + dataStr.substring(1);
        tvDataOggi.setText(dataStr);
        lunaView.setFase(info.fase, info.illuminazione);
        tvFaseNome.setText(info.nomeBreve);
        tvFaseSottotitolo.setText(info.sottotitolo);
        tvCicloInfo.setText("Giorno " + info.giornoCiclo + " del ciclo · " +
            Math.round(info.illuminazione * 100) + "% illuminata");
        aggiornaDotsCliclo(info.giornoCiclo);
        ConsiglioData.ConsiglioSezione capelli = ConsiglioData.getConsiglioCapelli(info.fase, info.etaGiorni);
        tvCapelliPillola.setText(capelli.testoPillola);
        tvCapelliTesto.setText(capelli.testoDescrizione);
        applicaStilePillola(tvCapelliPillola, capelli.valutazionePillola);
        ConsiglioData.ConsiglioSezione semina = ConsiglioData.getConsiglioSemina(info.fase, info.etaGiorni);
        for (int i = 0; i < 4; i++) {
            tvMiniCardLabel[i].setText(semina.miniCardLabel[i]);
            tvMiniCardVal[i].setText(ConsiglioData.valutazioneToString(semina.miniCardVal[i]));
            applicaColoreValore(tvMiniCardVal[i], semina.miniCardVal[i]);
        }
        tvSeminaTesto.setText(semina.testoExtra);
        ConsiglioData.ConsiglioSezione legna = ConsiglioData.getConsiglioLegna(info.fase, info.etaGiorni);
        tvLegnaPillola.setText(legna.testoPillola);
        tvLegnaTesto.setText(legna.testoDescrizione);
        applicaStilePillola(tvLegnaPillola, legna.valutazionePillola);
        if (legna.prossimaMomentum != null) {
            layoutLegnaProssimo.setVisibility(View.VISIBLE);
            tvLegnaProssimo.setText(legna.prossimaMomentum);
        } else {
            layoutLegnaProssimo.setVisibility(View.GONE);
        }
    }
 
    private void aggiornaDotsCliclo(int giornoCiclo) {
        dotContainer.removeAllViews();
        int totalDots = 15;
        int dotAtToday = (int) Math.round((giornoCiclo / 29.5f) * totalDots);
        for (int i = 0; i < totalDots; i++) {
            View dot = new View(this);
            int size = (i == dotAtToday) ? dpToPx(12) : dpToPx(7);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            dot.setLayoutParams(lp);
            int color = (i == dotAtToday) ? Color.parseColor("#e8d5a8") :
                        (i < dotAtToday)  ? Color.parseColor("#7a6a58") :
                                            Color.parseColor("#3a3028");
            android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
            d.setColor(color);
            d.setCornerRadius(size / 2f);
            dot.setBackground(d);
            dotContainer.addView(dot);
        }
    }
 
    // Metodi statici condivisi con DettaglioGiornoActivity
    public static void applicaStilePillola(TextView tv, ConsiglioData.Valutazione v, float density) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(20 * density);
        switch (v) {
            case OTTIMO:
            case FAVOREVOLE:
                bg.setColor(Color.parseColor("#1e2e18"));
                bg.setStroke(1, Color.parseColor("#3d5a30"));
                tv.setTextColor(Color.parseColor("#7ab85a")); break;
            case BUONO:
                bg.setColor(Color.parseColor("#1e2a20"));
                bg.setStroke(1, Color.parseColor("#2d4830"));
                tv.setTextColor(Color.parseColor("#5aa870")); break;
            case NEUTRO:
                bg.setColor(Color.parseColor("#252018"));
                bg.setStroke(1, Color.parseColor("#403828"));
                tv.setTextColor(Color.parseColor("#9a8a68")); break;
            case SCONSIGLIATO:
                bg.setColor(Color.parseColor("#2e2510"));
                bg.setStroke(1, Color.parseColor("#5a4520"));
                tv.setTextColor(Color.parseColor("#c49a40")); break;
            case EVITA:
                bg.setColor(Color.parseColor("#2e1a18"));
                bg.setStroke(1, Color.parseColor("#5a2820"));
                tv.setTextColor(Color.parseColor("#c05040")); break;
        }
        tv.setBackground(bg);
        tv.setPadding(Math.round(12 * density), Math.round(4 * density),
                      Math.round(12 * density), Math.round(4 * density));
    }
 
    public static void applicaColoreValore(TextView tv, ConsiglioData.Valutazione v) {
        switch (v) {
            case OTTIMO:
            case FAVOREVOLE: tv.setTextColor(Color.parseColor("#7ab85a")); break;
            case BUONO:      tv.setTextColor(Color.parseColor("#5aa870")); break;
            case NEUTRO:     tv.setTextColor(Color.parseColor("#c4a882")); break;
            case SCONSIGLIATO: tv.setTextColor(Color.parseColor("#c49a40")); break;
            case EVITA:      tv.setTextColor(Color.parseColor("#c05040")); break;
        }
    }
 
    private void applicaStilePillola(TextView tv, ConsiglioData.Valutazione v) {
        applicaStilePillola(tv, v, getResources().getDisplayMetrics().density);
    }
 
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
