package com.lunanatura.app;

public class ConsiglioData {

    public enum Valutazione {
        OTTIMO, FAVOREVOLE, BUONO, NEUTRO, SCONSIGLIATO, EVITA
    }

    public static class ConsiglioSezione {
        public Valutazione valutazionePillola;
        public String testoPillola;
        public String testoDescrizione;
        public String prossimaMomentum; // null se non applicabile
        // Per semina: 4 mini-card
        public String[] miniCardLabel;
        public Valutazione[] miniCardVal;
        public String testoExtra;
    }

    public static String valutazioneToString(Valutazione v) {
        switch (v) {
            case OTTIMO: return "Ottimo";
            case FAVOREVOLE: return "Favorevole";
            case BUONO: return "Buono";
            case NEUTRO: return "Neutro";
            case SCONSIGLIATO: return "Sconsigliato";
            case EVITA: return "Evita";
            default: return "";
        }
    }

    // ---------------------------------------------------------------
    // CAPELLI
    // ---------------------------------------------------------------
    public static ConsiglioSezione getConsiglioCapelli(LunaCalcolo.FaseLunare fase, double eta) {
        ConsiglioSezione c = new ConsiglioSezione();
        switch (fase) {
            case LUNA_NUOVA:
                c.valutazionePillola = Valutazione.EVITA;
                c.testoPillola = "Evita il taglio";
                c.testoDescrizione = "La luna nuova è un momento di pausa e rigenerazione. "
                    + "Il taglio in questi giorni tende a dare risultati poco soddisfacenti — "
                    + "i capelli crescono più lenti e il taglio dura meno. "
                    + "Preferisci trattamenti nutrienti e maschere.";
                break;
            case CRESCENTE_FALCE:
            case PRIMO_QUARTO:
            case CRESCENTE_GIBBOSA:
                c.valutazionePillola = Valutazione.OTTIMO;
                c.testoPillola = "Ottimo momento";
                c.testoDescrizione = "La luna crescente favorisce crescita rapida e vigorosa. "
                    + "Taglia oggi per capelli più folti e forti. "
                    + "Ideale anche per colorazioni — il colore risulta più intenso e duraturo.";
                break;
            case LUNA_PIENA:
                c.valutazionePillola = Valutazione.BUONO;
                c.testoPillola = "Buono";
                c.testoDescrizione = "Al plenilunio l'energia vitale è al massimo. "
                    + "Il taglio dà capelli luminosi e pieni di volume. "
                    + "Sconsigliata invece la colorazione — il capello è più poroso e assorbe in modo irregolare.";
                break;
            case CALANTE_GIBBOSA:
            case ULTIMO_QUARTO:
            case CALANTE_FALCE:
                c.valutazionePillola = Valutazione.BUONO;
                c.testoPillola = "Buono per mantenersi";
                c.testoDescrizione = "La luna calante rallenta la crescita: il taglio dura più a lungo "
                    + "e la lunghezza si mantiene meglio. Ideale se vuoi conservare il taglio attuale. "
                    + "Ottimo anche per trattamenti fortificanti e rigeneranti.";
                break;
        }
        return c;
    }

    // ---------------------------------------------------------------
    // SEMINA & RACCOLTA
    // ---------------------------------------------------------------
    public static ConsiglioSezione getConsiglioSemina(LunaCalcolo.FaseLunare fase, double eta) {
        ConsiglioSezione c = new ConsiglioSezione();
        c.miniCardLabel = new String[]{"Ortaggi aerei", "Ortaggi sotterranei", "Trapianto", "Raccolta"};

        switch (fase) {
            case LUNA_NUOVA:
                c.miniCardVal = new Valutazione[]{
                    Valutazione.NEUTRO, Valutazione.FAVOREVOLE, Valutazione.EVITA, Valutazione.EVITA
                };
                c.testoExtra = "Giorni di terra: la luna nuova richiama energia verso il basso. "
                    + "Buona semina per radici, bulbi e tuberi. "
                    + "Evita raccolte e trapianti — le piante hanno poca vitalità in superficie.";
                break;
            case CRESCENTE_FALCE:
            case PRIMO_QUARTO:
            case CRESCENTE_GIBBOSA:
                c.miniCardVal = new Valutazione[]{
                    Valutazione.FAVOREVOLE, Valutazione.SCONSIGLIATO, Valutazione.BUONO, Valutazione.NEUTRO
                };
                c.testoExtra = "Giorni di fuoco e aria: la linfa sale vigorosa. Ideale per semina "
                    + "di piante da frutto, ortaggi aerei e trapianti. "
                    + "Ortaggi sotterranei e tuberi preferiscono la luna calante.";
                break;
            case LUNA_PIENA:
                c.miniCardVal = new Valutazione[]{
                    Valutazione.OTTIMO, Valutazione.NEUTRO, Valutazione.BUONO, Valutazione.OTTIMO
                };
                c.testoExtra = "Il momento migliore per la raccolta: frutta, verdura e erbe aromatiche "
                    + "raccolte a luna piena hanno più sapore, più succo e si conservano meglio. "
                    + "Anche la semina di piante da frutto dà ottimi risultati.";
                break;
            case CALANTE_GIBBOSA:
            case ULTIMO_QUARTO:
            case CALANTE_FALCE:
                c.miniCardVal = new Valutazione[]{
                    Valutazione.NEUTRO, Valutazione.OTTIMO, Valutazione.EVITA, Valutazione.BUONO
                };
                c.testoExtra = "Giorni di terra e acqua: l'energia scende verso le radici. "
                    + "Momento eccellente per seminare carote, cipolle, aglio, patate e bulbi. "
                    + "Buono anche per la raccolta di conserve e prodotti da essiccare.";
                break;
        }
        return c;
    }

    // ---------------------------------------------------------------
    // POTATURA & LEGNA
    // ---------------------------------------------------------------
    public static ConsiglioSezione getConsiglioLegna(LunaCalcolo.FaseLunare fase, double eta) {
        ConsiglioSezione c = new ConsiglioSezione();
        int giorniACalante = LunaCalcolo.giorniACalante(eta);

        switch (fase) {
            case LUNA_NUOVA:
                c.valutazionePillola = Valutazione.OTTIMO;
                c.testoPillola = "Momento ideale";
                c.testoDescrizione = "Con la linfa al minimo, il legno tagliato ora è asciutto, "
                    + "compatto e resistente. Momento ideale per raccogliere legna da ardere, "
                    + "per potature drastiche e per i tagli che non vuoi stimolino ricrescita vigorosa.";
                c.prossimaMomentum = "Adesso — approfitta!";
                break;
            case CRESCENTE_FALCE:
            case PRIMO_QUARTO:
            case CRESCENTE_GIBBOSA:
                c.valutazionePillola = Valutazione.SCONSIGLIATO;
                c.testoPillola = "Sconsigliato";
                c.testoDescrizione = "La luna crescente spinge la linfa verso l'alto: il legno tagliato "
                    + "ora contiene più umidità e marcisce prima. "
                    + "Attendi la luna calante per tagli, potature importanti e raccolta di legna da ardere.";
                c.prossimaMomentum = "Tra " + giorniACalante + " giorni — luna calante";
                break;
            case LUNA_PIENA:
                c.valutazionePillola = Valutazione.EVITA;
                c.testoPillola = "Evita";
                c.testoDescrizione = "Al plenilunio la linfa è al massimo — le piante sanguinano molto "
                    + "se tagliate e le ferite faticano a cicatrizzarsi. "
                    + "Anche la legna raccolta ora è ricca d'acqua e brucia male. "
                    + "Aspetta ancora qualche giorno.";
                c.prossimaMomentum = "Tra " + giorniACalante + " giorni — luna calante";
                break;
            case CALANTE_GIBBOSA:
            case ULTIMO_QUARTO:
            case CALANTE_FALCE:
                c.valutazionePillola = Valutazione.OTTIMO;
                c.testoPillola = "Momento ideale";
                c.testoDescrizione = "La linfa è in ritirata verso le radici: il legno è compatto, "
                    + "la pianta soffre meno e le ferite cicatrizzano velocemente. "
                    + "Il momento migliore per potature importanti, tagli di formazione "
                    + "e raccolta di legna da ardere.";
                c.prossimaMomentum = "Adesso — approfitta!";
                break;
        }
        return c;
    }
}
