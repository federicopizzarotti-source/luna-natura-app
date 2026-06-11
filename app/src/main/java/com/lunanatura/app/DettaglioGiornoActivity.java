package com.lunanatura.app;
 
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
 
public class DettaglioGiornoActivity extends AppCompatActivity {
 
    private LunaView lunaView;
    private TextView tvFaseNome, tvFaseSottotitolo, tvCicloInfo, tvDataOggi;
    private TextView tvCapelliPillola, tvCapelliTesto;
    private TextView tvLegnaPillola, tvLegnaTesto, tvLegnaProssimo;
    private LinearLayout layoutLegnaProssimo;
    private TextView tvSeminaTesto;
    private TextView[] tvMiniCardLabel, tvMiniCardVal;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        initViews();
 
        // Nascondi titolo app
        findViewById(R.id.tvAppTitolo).setVisibility(View.GONE);
 
        // Aggiungi pulsante ← Indietro nella topBar
        LinearLayout topBar = findViewById(R.id.topBar);
        TextView btnBack = new TextView(this);
        btnBack.setText("← Indietro");
        btnBack.setTextColor(Color.parseColor("#c4a882"));
        btnBack.setTextSize(13);
        btnBack.setOnClickListener(v -> finish());
        topBar.addView(btnBack, 0);
 
        // Nasconde i dots del ciclo
        View dotContainer = findViewById(R.id.dotContainer);
        if (dotContainer != null) dotContainer.setVisibility(View.GONE);
 
        // Pulsante in fondo: torna a oggi
        TextView btnAzione = findViewById(R.id.btnCalendario);
        btnAzione.setText("🌙  Torna al giorno di oggi");
        btnAzione.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
 
        // Recupera data dal calendario
        int giorno = getIntent().getIntExtra("giorno", 1);
        int mese   = getIntent().getIntExtra("mese", 0);
        int anno   = getIntent().getIntExtra("anno", Calendar.getInstance().get(Calendar.YEAR));
        Calendar cal = Calendar.getInstance();
        cal.set(anno, mese, giorno);
 
        aggiornaConData(cal);
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
        tvMiniCardLabel = new TextView[]{
            findViewById(R.id.tvMC1Label), findViewById(R.id.tvMC2Label),
            findViewById(R.id.tvMC3Label), findViewById(R.id.tvMC4Label)
        };
        tvMiniCardVal = new TextView[]{
            findViewById(R.id.tvMC1Val), findViewById(R.id.tvMC2Val),
            findViewById(R.id.tvMC3Val), findViewById(R.id.tvMC4Val)
        };
    }
 
    private void aggiornaConData(Calendar cal) {
        LunaCalcolo.InfoLuna info = LunaCalcolo.calcolaFase(cal.getTime());
 
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("it", "IT"));
        String dataStr = sdf.format(cal.getTime());
        dataStr = dataStr.substring(0, 1).toUpperCase() + dataStr.substring(1);
        tvDataOggi.setText(dataStr);
        tvDataOggi.setVisibility(View.VISIBLE);
 
        lunaView.setFase(info.fase, info.illuminazione);
        tvFaseNome.setText(info.nomeBreve);
        tvFaseSottotitolo.setText(info.sottotitolo);
        tvCicloInfo.setText("Giorno " + info.giornoCiclo + " del ciclo · " +
            Math.round(info.illuminazione * 100) + "% illuminata");
 
        ConsiglioData.ConsiglioSezione capelli = ConsiglioData.getConsiglioCapelli(info.fase, info.etaGiorni);
        tvCapelliPillola.setText(capelli.testoPillola);
        tvCapelliTesto.setText(capelli.testoDescrizione);
        MainActivity.applicaStilePillola(tvCapelliPillola, capelli.valutazionePillola,
            getResources().getDisplayMetrics().density);
 
        ConsiglioData.ConsiglioSezione semina = ConsiglioData.getConsiglioSemina(info.fase, info.etaGiorni);
        for (int i = 0; i < 4; i++) {
            tvMiniCardLabel[i].setText(semina.miniCardLabel[i]);
            tvMiniCardVal[i].setText(ConsiglioData.valutazioneToString(semina.miniCardVal[i]));
            MainActivity.applicaColoreValore(tvMiniCardVal[i], semina.miniCardVal[i]);
        }
        tvSeminaTesto.setText(semina.testoExtra);
 
        ConsiglioData.ConsiglioSezione legna = ConsiglioData.getConsiglioLegna(info.fase, info.etaGiorni);
        tvLegnaPillola.setText(legna.testoPillola);
        tvLegnaTesto.setText(legna.testoDescrizione);
        MainActivity.applicaStilePillola(tvLegnaPillola, legna.valutazionePillola,
            getResources().getDisplayMetrics().density);
        if (legna.prossimaMomentum != null) {
            layoutLegnaProssimo.setVisibility(View.VISIBLE);
            tvLegnaProssimo.setText(legna.prossimaMomentum);
        } else {
            layoutLegnaProssimo.setVisibility(View.GONE);
        }
    }
}
