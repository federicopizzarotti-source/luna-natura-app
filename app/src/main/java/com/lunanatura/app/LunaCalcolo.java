package com.lunanatura.app;

import java.util.Calendar;
import java.util.Date;

public class LunaCalcolo {

    public enum FaseLunare {
        LUNA_NUOVA,
        CRESCENTE_FALCE,
        PRIMO_QUARTO,
        CRESCENTE_GIBBOSA,
        LUNA_PIENA,
        CALANTE_GIBBOSA,
        ULTIMO_QUARTO,
        CALANTE_FALCE
    }

    public static class InfoLuna {
        public double etaGiorni;
        public double illuminazione;
        public FaseLunare fase;
        public String nomeBreve;
        public String nomeFase;
        public String sottotitolo;
        public int giornoCiclo;
    }

    // Algoritmo basato sull'equazione di Jean Meeus "Astronomical Algorithms"
    public static InfoLuna calcolaFase(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);

        int anno = cal.get(Calendar.YEAR);
        int mese = cal.get(Calendar.MONTH) + 1;
        int giorno = cal.get(Calendar.DAY_OF_MONTH);

        // Calcolo Julian Day Number
        if (mese <= 2) {
            anno -= 1;
            mese += 12;
        }
        int A = anno / 100;
        int B = 2 - A + A / 4;
        double JD = (int)(365.25 * (anno + 4716)) + (int)(30.6001 * (mese + 1)) + giorno + B - 1524.5;

        // Età della luna
        double daySince = JD - 2451549.5;
        double newMoons = daySince / 29.53058853;
        double etaGiorni = (newMoons - Math.floor(newMoons)) * 29.53058853;

        // Illuminazione (0.0 - 1.0)
        double illuminazione = (1 - Math.cos(2 * Math.PI * etaGiorni / 29.53058853)) / 2;

        InfoLuna info = new InfoLuna();
        info.etaGiorni = etaGiorni;
        info.illuminazione = illuminazione;
        info.giornoCiclo = (int) Math.round(etaGiorni) + 1;
        if (info.giornoCiclo > 29) info.giornoCiclo = 29;

        // Determinazione fase
        if (etaGiorni < 1.85) {
            info.fase = FaseLunare.LUNA_NUOVA;
            info.nomeBreve = "Luna Nuova";
            info.sottotitolo = "Inizio del ciclo";
        } else if (etaGiorni < 7.38) {
            info.fase = FaseLunare.CRESCENTE_FALCE;
            info.nomeBreve = "Luna Crescente";
            info.sottotitolo = "Falce crescente";
        } else if (etaGiorni < 8.84) {
            info.fase = FaseLunare.PRIMO_QUARTO;
            info.nomeBreve = "Primo Quarto";
            info.sottotitolo = "Luna crescente";
        } else if (etaGiorni < 14.77) {
            info.fase = FaseLunare.CRESCENTE_GIBBOSA;
            info.nomeBreve = "Luna Crescente";
            info.sottotitolo = "Gibbosa crescente";
        } else if (etaGiorni < 16.61) {
            info.fase = FaseLunare.LUNA_PIENA;
            info.nomeBreve = "Luna Piena";
            info.sottotitolo = "Plenilunio";
        } else if (etaGiorni < 23.14) {
            info.fase = FaseLunare.CALANTE_GIBBOSA;
            info.nomeBreve = "Luna Calante";
            info.sottotitolo = "Gibbosa calante";
        } else if (etaGiorni < 24.61) {
            info.fase = FaseLunare.ULTIMO_QUARTO;
            info.nomeBreve = "Ultimo Quarto";
            info.sottotitolo = "Luna calante";
        } else {
            info.fase = FaseLunare.CALANTE_FALCE;
            info.nomeBreve = "Luna Calante";
            info.sottotitolo = "Falce calante";
        }

        return info;
    }

    public static boolean isCrescente(FaseLunare fase) {
        return fase == FaseLunare.CRESCENTE_FALCE ||
               fase == FaseLunare.PRIMO_QUARTO ||
               fase == FaseLunare.CRESCENTE_GIBBOSA;
    }

    public static boolean isNuova(FaseLunare fase) {
        return fase == FaseLunare.LUNA_NUOVA;
    }

    public static boolean isPiena(FaseLunare fase) {
        return fase == FaseLunare.LUNA_PIENA;
    }

    public static boolean isCalante(FaseLunare fase) {
        return fase == FaseLunare.CALANTE_GIBBOSA ||
               fase == FaseLunare.ULTIMO_QUARTO ||
               fase == FaseLunare.CALANTE_FALCE;
    }

    // Calcola quanti giorni mancano alla prossima luna calante
    public static int giorniACalante(double etaGiorni) {
        double inizioCalante = 16.61;
        if (etaGiorni < inizioCalante) {
            return (int) Math.ceil(inizioCalante - etaGiorni);
        } else {
            return (int) Math.ceil(29.53 - etaGiorni + inizioCalante);
        }
    }

    // Calcola quanti giorni mancano alla prossima luna piena
    public static int giorniAPiena(double etaGiorni) {
        double inizioPiena = 14.77;
        if (etaGiorni < inizioPiena) {
            return (int) Math.ceil(inizioPiena - etaGiorni);
        } else {
            return (int) Math.ceil(29.53 - etaGiorni + inizioPiena);
        }
    }
}
