package com.lunanatura.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class LunaView extends View {

    private double illuminazione = 0.5;
    private LunaCalcolo.FaseLunare fase = LunaCalcolo.FaseLunare.PRIMO_QUARTO;

    private Paint paintSfondo;
    private Paint paintLuce;
    private Paint paintOmbra;
    private Paint paintCrater;
    private Paint paintGlow;

    public LunaView(Context context) {
        super(context);
        init();
    }

    public LunaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LunaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        paintSfondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSfondo.setColor(Color.parseColor("#2a2318"));

        paintLuce = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLuce.setColor(Color.parseColor("#f0dfa8"));

        paintOmbra = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOmbra.setColor(Color.parseColor("#1a1410"));
        paintOmbra.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        paintCrater = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCrater.setColor(Color.parseColor("#d4c490"));
        paintCrater.setAlpha(60);

        paintGlow = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setFase(LunaCalcolo.FaseLunare fase, double illuminazione) {
        this.fase = fase;
        this.illuminazione = illuminazione;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float r = Math.min(w, h) / 2f * 0.78f;

        // Glow esterno
        RadialGradient glow = new RadialGradient(cx, cy, r * 1.4f,
            new int[]{Color.parseColor("#40e8d090"), Color.TRANSPARENT},
            new float[]{0f, 1f},
            Shader.TileMode.CLAMP);
        paintGlow.setShader(glow);
        canvas.drawCircle(cx, cy, r * 1.4f, paintGlow);

        // Cerchio base scuro
        canvas.drawCircle(cx, cy, r, paintSfondo);

        if (LunaCalcolo.isNuova(fase)) {
            // Luna nuova: quasi buio con alone
            paintCrater.setAlpha(30);
            canvas.drawCircle(cx, cy, r, paintCrater);
            return;
        }

        // Disegno della luna illuminata
        // Calcola la forma dell'illuminazione con Path
        float terminator = (float)(illuminazione * 2 - 1); // -1 = tutto buio, +1 = tutto chiaro

        Path path = new Path();

        if (LunaCalcolo.isCrescente(fase) || LunaCalcolo.isNuova(fase)) {
            // Lato destro illuminato (crescente)
            path.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.drawPath(path, paintLuce);
            // Ombra che copre parte sinistra
            Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
            cut.setColor(Color.parseColor("#1a1410"));
            float offset = r * (float)(1 - illuminazione * 2);
            canvas.drawOval(cx - r + offset, cy - r, cx + offset, cy + r, cut);
        } else if (LunaCalcolo.isPiena(fase)) {
            // Luna piena: cerchio completo
            canvas.drawCircle(cx, cy, r, paintLuce);
        } else {
            // Calante: lato sinistro illuminato
            path.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.drawPath(path, paintLuce);
            Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
            cut.setColor(Color.parseColor("#1a1410"));
            float offset = r * (float)(illuminazione * 2 - 1);
            canvas.drawOval(cx + offset, cy - r, cx + r + offset, cy + r, cut);
        }

        // Crateri decorativi
        paintCrater.setAlpha(50);
        canvas.drawCircle(cx - r * 0.25f, cy - r * 0.3f, r * 0.09f, paintCrater);
        canvas.drawCircle(cx + r * 0.3f, cy - r * 0.1f, r * 0.06f, paintCrater);
        canvas.drawCircle(cx + r * 0.1f, cy + r * 0.35f, r * 0.08f, paintCrater);
        canvas.drawCircle(cx - r * 0.4f, cy + r * 0.2f, r * 0.05f, paintCrater);
        canvas.drawCircle(cx + r * 0.4f, cy + r * 0.3f, r * 0.04f, paintCrater);
    }
}
