package com.lunanatura.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        // Adatta topBar
        findViewById(R.id.tvAppTitolo).setVisibility(View.GONE);
        LinearLayout topBar = findViewById(R.id.topBar);
        TextView btnBack = new TextView(this);
        btnBack.setText(getString(R.string.btn_indietro));
        btnBack.setTextColor(ContextCompat.getColor(this, R.color.testo_principale));
        btnBack.setTextSize(13);
        btnBack.setOnClickListener(v -> finish());
        topBar.addView(btnBack, 0);

        // Nasconde dots e pulsante About
        View dotContainer = findViewById(R.id.dotContainer);
        if (dotContainer != null) dotContainer.setVisibility(View.GONE);
        findViewById(R.id.btnAbout).setVisibility(View.GONE);

        // Pulsante torna a oggi
        TextView btnAzione = findViewById(R.id.btnCalendario);
        btnAzione.setText(getString(R.string.btn_oggi));
        btnAzione.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

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
        MainActivity main = new MainActivity();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale(getString(R.string.locale_code)));
        String dataStr = sdf.format(cal.getTime());
        dataStr = dataStr.substring(0, 1).toUpperCase() + dataStr.substring(1);
        tvDataOggi.setText(dataStr);
        tvDataOggi.setVisibility(View.VISIBLE);

        lunaView.setFase(info.fase, info.illuminazione);
        tvFaseNome.setText(getNomeFase(info.fase));
        tvFaseSottotitolo.setText(getSottotitoloFase(info.fase));
        tvCicloInfo.setText(getString(R.string.giorno_ciclo, info.giornoCiclo, Math.round(info.illuminazione * 100)));

        ConsiglioData.ConsiglioSezione capelli = ConsiglioData.getConsiglioCapelli(info.fase, info.etaGiorni);
        tvCapelliPillola.setText(getStringCapelliPillola(info.fase));
        tvCapelliTesto.setText(getStringCapelliTesto(info.fase));
        MainActivity.applicaStilePillola(tvCapelliPillola, capelli.valutazionePillola,
            getResources().getDisplayMetrics().density, this);

        ConsiglioData.ConsiglioSezione semina = ConsiglioData.getConsiglioSemina(info.fase, info.etaGiorni);
        tvMiniCardLabel[0].setText(getString(R.string.mc_ortaggi_aerei));
        tvMiniCardLabel[1].setText(getString(R.string.mc_ortaggi_sotterranei));
        tvMiniCardLabel[2].setText(getString(R.string.mc_trapianto));
        tvMiniCardLabel[3].setText(getString(R.string.mc_raccolta));
        for (int i = 0; i < 4; i++) {
            tvMiniCardVal[i].setText(getStringValutazione(semina.miniCardVal[i]));
            MainActivity.applicaColoreValore(tvMiniCardVal[i], semina.miniCardVal[i], this);
        }
        tvSeminaTesto.setText(getStringSeminaTesto(info.fase));

        ConsiglioData.ConsiglioSezione legna = ConsiglioData.getConsiglioLegna(info.fase, info.etaGiorni);
        tvLegnaPillola.setText(getStringLegnaPillola(info.fase));
        tvLegnaTesto.setText(getStringLegnaTesto(info.fase));
        MainActivity.applicaStilePillola(tvLegnaPillola, legna.valutazionePillola,
            getResources().getDisplayMetrics().density, this);
        if (legna.prossimaMomentum != null) {
            layoutLegnaProssimo.setVisibility(View.VISIBLE);
            tvLegnaProssimo.setText(getStringLegnaProssimo(info.fase, info.etaGiorni));
        } else {
            layoutLegnaProssimo.setVisibility(View.GONE);
        }
    }

    private String getNomeFase(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA: return getString(R.string.fase_luna_nuova);
            case CRESCENTE_FALCE: case PRIMO_QUARTO: case CRESCENTE_GIBBOSA: return getString(R.string.fase_crescente);
            case LUNA_PIENA: return getString(R.string.fase_luna_piena);
            default: return getString(R.string.fase_calante);
        }
    }

    private String getSottotitoloFase(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA: return getString(R.string.sub_inizio_ciclo);
            case CRESCENTE_FALCE: return getString(R.string.sub_falce_crescente);
            case PRIMO_QUARTO: return getString(R.string.fase_crescente);
            case CRESCENTE_GIBBOSA: return getString(R.string.sub_gibbosa_crescente);
            case LUNA_PIENA: return getString(R.string.sub_plenilunio);
            case CALANTE_GIBBOSA: return getString(R.string.sub_gibbosa_calante);
            case ULTIMO_QUARTO: return getString(R.string.fase_calante);
            default: return getString(R.string.sub_falce_calante);
        }
    }

    private String getStringValutazione(ConsiglioData.Valutazione v) {
        switch (v) {
            case OTTIMO: return getString(R.string.val_ottimo);
            case FAVOREVOLE: return getString(R.string.val_favorevole);
            case BUONO: return getString(R.string.val_buono);
            case NEUTRO: return getString(R.string.val_neutro);
            case SCONSIGLIATO: return getString(R.string.val_sconsigliato);
            case EVITA: return getString(R.string.val_evita);
            default: return "";
        }
    }

    private String getStringCapelliPillola(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase)) return getString(R.string.capelli_pill_evita);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.capelli_pill_ottimo);
        if (LunaCalcolo.isPiena(fase)) return getString(R.string.capelli_pill_buono);
        return getString(R.string.capelli_pill_mantieni);
    }

    private String getStringCapelliTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase)) return getString(R.string.capelli_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.capelli_testo_crescente);
        if (LunaCalcolo.isPiena(fase)) return getString(R.string.capelli_testo_piena);
        return getString(R.string.capelli_testo_calante);
    }

    private String getStringSeminaTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase)) return getString(R.string.semina_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.semina_testo_crescente);
        if (LunaCalcolo.isPiena(fase)) return getString(R.string.semina_testo_piena);
        return getString(R.string.semina_testo_calante);
    }

    private String getStringLegnaPillola(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase) || LunaCalcolo.isCalante(fase)) return getString(R.string.legna_pill_ideale);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.legna_pill_sconsigliato);
        return getString(R.string.legna_pill_evita);
    }

    private String getStringLegnaTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase)) return getString(R.string.legna_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.legna_testo_crescente);
        if (LunaCalcolo.isPiena(fase)) return getString(R.string.legna_testo_piena);
        return getString(R.string.legna_testo_calante);
    }

    private String getStringLegnaProssimo(LunaCalcolo.FaseLunare fase, double etaGiorni) {
        if (LunaCalcolo.isNuova(fase) || LunaCalcolo.isCalante(fase))
            return getString(R.string.legna_prossimo_adesso);
        return getString(R.string.legna_prossimo_giorni, LunaCalcolo.giorniACalante(etaGiorni));
    }
}
