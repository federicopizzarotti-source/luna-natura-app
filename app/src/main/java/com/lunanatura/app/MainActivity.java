package com.lunanatura.app;
 
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private LinearLayout[] miniCards;
    private LinearLayout dotContainer;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        aggiornaConFaseLunare();
        findViewById(R.id.btnCalendario).setOnClickListener(v ->
            startActivity(new Intent(this, CalendarioActivity.class)));
        findViewById(R.id.btnAbout).setOnClickListener(v ->
            startActivity(new Intent(this, AboutActivity.class)));
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
        miniCards = new LinearLayout[]{
            findViewById(R.id.mc1), findViewById(R.id.mc2),
            findViewById(R.id.mc3), findViewById(R.id.mc4)
        };
        // Applica sfondo verde salvia alle mini card ortaggi
        for (LinearLayout mc : miniCards) {
            mc.setBackground(creaRoundDrawable(
                ContextCompat.getColor(this, R.color.mini_card_ortaggi_sfondo),
                ContextCompat.getColor(this, R.color.mini_card_ortaggi_bordo),
                dpToPx(8), 1));
        }
        // Applica sfondo ambra al box legna prossimo
        layoutLegnaProssimo.setBackground(creaRoundDrawable(
            ContextCompat.getColor(this, R.color.legna_prossimo_sfondo),
            ContextCompat.getColor(this, R.color.legna_prossimo_bordo),
            dpToPx(8), 1));
    }
 
    private void aggiornaConFaseLunare() {
        Date oggi = new Date();
        LunaCalcolo.InfoLuna info = LunaCalcolo.calcolaFase(oggi);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy",
            new Locale(getString(R.string.locale_code)));
        String dataStr = sdf.format(oggi);
        dataStr = dataStr.substring(0, 1).toUpperCase() + dataStr.substring(1);
        tvDataOggi.setText(dataStr);
        lunaView.setFase(info.fase, info.illuminazione);
        tvFaseNome.setText(getNomeFase(info.fase));
        tvFaseSottotitolo.setText(getSottotitoloFase(info.fase));
        tvCicloInfo.setText(getString(R.string.giorno_ciclo,
            info.giornoCiclo, Math.round(info.illuminazione * 100)));
        aggiornaDotsCliclo(info.giornoCiclo);
 
        ConsiglioData.ConsiglioSezione capelli =
            ConsiglioData.getConsiglioCapelli(info.fase, info.etaGiorni);
        tvCapelliPillola.setText(getStringCapelliPillola(info.fase));
        tvCapelliTesto.setText(getStringCapelliTesto(info.fase));
        applicaStilePillola(tvCapelliPillola, capelli.valutazionePillola);
 
        ConsiglioData.ConsiglioSezione semina =
            ConsiglioData.getConsiglioSemina(info.fase, info.etaGiorni);
        tvMiniCardLabel[0].setText(getString(R.string.mc_ortaggi_aerei));
        tvMiniCardLabel[1].setText(getString(R.string.mc_ortaggi_sotterranei));
        tvMiniCardLabel[2].setText(getString(R.string.mc_trapianto));
        tvMiniCardLabel[3].setText(getString(R.string.mc_raccolta));
        for (int i = 0; i < 4; i++) {
            tvMiniCardLabel[i].setTextColor(
                ContextCompat.getColor(this, R.color.mini_card_ortaggi_label));
            tvMiniCardVal[i].setText(getStringValutazione(semina.miniCardVal[i]));
            applicaColoreValore(tvMiniCardVal[i], semina.miniCardVal[i]);
        }
        tvSeminaTesto.setText(getStringSeminaTesto(info.fase));
 
        ConsiglioData.ConsiglioSezione legna =
            ConsiglioData.getConsiglioLegna(info.fase, info.etaGiorni);
        tvLegnaPillola.setText(getStringLegnaPillola(info.fase));
        tvLegnaTesto.setText(getStringLegnaTesto(info.fase));
        applicaStilePillola(tvLegnaPillola, legna.valutazionePillola);
        if (legna.prossimaMomentum != null) {
            layoutLegnaProssimo.setVisibility(View.VISIBLE);
            tvLegnaProssimo.setText(getStringLegnaProssimo(info.fase, info.etaGiorni));
        } else {
            layoutLegnaProssimo.setVisibility(View.GONE);
        }
    }
 
    // Helpers localizzazione
    public String getNomeFase(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA: return getString(R.string.fase_luna_nuova);
            case CRESCENTE_FALCE: case PRIMO_QUARTO: case CRESCENTE_GIBBOSA:
                return getString(R.string.fase_crescente);
            case LUNA_PIENA: return getString(R.string.fase_luna_piena);
            default: return getString(R.string.fase_calante);
        }
    }
 
    public String getSottotitoloFase(LunaCalcolo.FaseLunare fase) {
        switch (fase) {
            case LUNA_NUOVA:        return getString(R.string.sub_inizio_ciclo);
            case CRESCENTE_FALCE:   return getString(R.string.sub_falce_crescente);
            case PRIMO_QUARTO:      return getString(R.string.fase_crescente);
            case CRESCENTE_GIBBOSA: return getString(R.string.sub_gibbosa_crescente);
            case LUNA_PIENA:        return getString(R.string.sub_plenilunio);
            case CALANTE_GIBBOSA:   return getString(R.string.sub_gibbosa_calante);
            case ULTIMO_QUARTO:     return getString(R.string.fase_calante);
            default:                return getString(R.string.sub_falce_calante);
        }
    }
 
    public String getStringValutazione(ConsiglioData.Valutazione v) {
        switch (v) {
            case OTTIMO:       return getString(R.string.val_ottimo);
            case FAVOREVOLE:   return getString(R.string.val_favorevole);
            case BUONO:        return getString(R.string.val_buono);
            case NEUTRO:       return getString(R.string.val_neutro);
            case SCONSIGLIATO: return getString(R.string.val_sconsigliato);
            case EVITA:        return getString(R.string.val_evita);
            default:           return "";
        }
    }
 
    private String getStringCapelliPillola(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase))     return getString(R.string.capelli_pill_evita);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.capelli_pill_ottimo);
        if (LunaCalcolo.isPiena(fase))     return getString(R.string.capelli_pill_buono);
        return getString(R.string.capelli_pill_mantieni);
    }
 
    private String getStringCapelliTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase))     return getString(R.string.capelli_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.capelli_testo_crescente);
        if (LunaCalcolo.isPiena(fase))     return getString(R.string.capelli_testo_piena);
        return getString(R.string.capelli_testo_calante);
    }
 
    private String getStringSeminaTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase))     return getString(R.string.semina_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.semina_testo_crescente);
        if (LunaCalcolo.isPiena(fase))     return getString(R.string.semina_testo_piena);
        return getString(R.string.semina_testo_calante);
    }
 
    private String getStringLegnaPillola(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase) || LunaCalcolo.isCalante(fase))
            return getString(R.string.legna_pill_ideale);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.legna_pill_sconsigliato);
        return getString(R.string.legna_pill_evita);
    }
 
    private String getStringLegnaTesto(LunaCalcolo.FaseLunare fase) {
        if (LunaCalcolo.isNuova(fase))     return getString(R.string.legna_testo_nuova);
        if (LunaCalcolo.isCrescente(fase)) return getString(R.string.legna_testo_crescente);
        if (LunaCalcolo.isPiena(fase))     return getString(R.string.legna_testo_piena);
        return getString(R.string.legna_testo_calante);
    }
 
    private String getStringLegnaProssimo(LunaCalcolo.FaseLunare fase, double etaGiorni) {
        if (LunaCalcolo.isNuova(fase) || LunaCalcolo.isCalante(fase))
            return getString(R.string.legna_prossimo_adesso);
        return getString(R.string.legna_prossimo_giorni,
            LunaCalcolo.giorniACalante(etaGiorni));
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
            int colorRes = (i == dotAtToday) ? R.color.dot_oggi :
                           (i < dotAtToday)  ? R.color.dot_passato : R.color.dot_futuro;
            android.graphics.drawable.GradientDrawable d =
                new android.graphics.drawable.GradientDrawable();
            d.setColor(ContextCompat.getColor(this, colorRes));
            d.setCornerRadius(size / 2f);
            dot.setBackground(d);
            dotContainer.addView(dot);
        }
    }
 
    // Metodi statici condivisi con DettaglioGiornoActivity
    public static void applicaStilePillola(TextView tv, ConsiglioData.Valutazione v,
                                           float density, android.content.Context ctx) {
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(20 * density);
        int sfondo, bordo, testo;
        switch (v) {
            case OTTIMO: case FAVOREVOLE:
                sfondo = R.color.val_ottimo_sfondo; bordo = R.color.val_ottimo_bordo;
                testo  = R.color.val_ottimo_testo; break;
            case BUONO:
                sfondo = R.color.val_buono_sfondo; bordo = R.color.val_buono_bordo;
                testo  = R.color.val_buono_testo; break;
            case NEUTRO:
                sfondo = R.color.val_neutro_sfondo; bordo = R.color.val_neutro_bordo;
                testo  = R.color.val_neutro_testo; break;
            case SCONSIGLIATO:
                sfondo = R.color.val_warn_sfondo; bordo = R.color.val_warn_bordo;
                testo  = R.color.val_warn_testo; break;
            default:
                sfondo = R.color.val_evita_sfondo; bordo = R.color.val_evita_bordo;
                testo  = R.color.val_evita_testo; break;
        }
        bg.setColor(ContextCompat.getColor(ctx, sfondo));
        bg.setStroke(1, ContextCompat.getColor(ctx, bordo));
        tv.setTextColor(ContextCompat.getColor(ctx, testo));
        tv.setBackground(bg);
        tv.setPadding(Math.round(12 * density), Math.round(4 * density),
                      Math.round(12 * density), Math.round(4 * density));
    }
 
    public static void applicaColoreValore(TextView tv, ConsiglioData.Valutazione v,
                                           android.content.Context ctx) {
        int colorRes;
        switch (v) {
            case OTTIMO: case FAVOREVOLE: colorRes = R.color.val_ottimo_testo; break;
            case BUONO:                  colorRes = R.color.val_buono_testo; break;
            case NEUTRO:                 colorRes = R.color.val_neutro_testo; break;
            case SCONSIGLIATO:           colorRes = R.color.val_warn_testo; break;
            default:                     colorRes = R.color.val_evita_testo; break;
        }
        tv.setTextColor(ContextCompat.getColor(ctx, colorRes));
    }
 
    // Helper per creare GradientDrawable da colori
    public static android.graphics.drawable.GradientDrawable creaRoundDrawable(
            int coloreSfondo, int coloreBordo, int radius, int strokeWidth) {
        android.graphics.drawable.GradientDrawable d =
            new android.graphics.drawable.GradientDrawable();
        d.setColor(coloreSfondo);
        d.setCornerRadius(radius);
        d.setStroke(strokeWidth, coloreBordo);
        return d;
    }
 
    private void applicaStilePillola(TextView tv, ConsiglioData.Valutazione v) {
        applicaStilePillola(tv, v, getResources().getDisplayMetrics().density, this);
    }
 
    private void applicaColoreValore(TextView tv, ConsiglioData.Valutazione v) {
        applicaColoreValore(tv, v, this);
    }
 
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
