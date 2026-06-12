package com.lunanatura.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;

public class LunaView extends View {

    private double illuminazione = 0.5;
    private LunaCalcolo.FaseLunare fase = LunaCalcolo.FaseLunare.PRIMO_QUARTO;

    private Paint paintSfondo, paintLuce, paintCrater, paintGlow;

    public LunaView(Context context) { super(context); init(); }
    public LunaView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public LunaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paintSfondo = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSfondo.setColor(ContextCompat.getColor(getContext(), R.color.luna_sfondo));
        paintLuce = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLuce.setColor(ContextCompat.getColor(getContext(), R.color.luna_luce));
        paintCrater = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCrater.setColor(ContextCompat.getColor(getContext(), R.color.luna_crateri));
        paintCrater.setAlpha(80);
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
        int w = getWidth(); int h = getHeight();
        float cx = w / 2f; float cy = h / 2f;
        float r = Math.min(w, h) / 2f * 0.78f;

        int glowColor = ContextCompat.getColor(getContext(), R.color.luna_ombra);
        RadialGradient glow = new RadialGradient(cx, cy, r * 1.4f,
            new int[]{(glowColor & 0x00FFFFFF) | 0x60000000, 0x00000000},
            new float[]{0f, 1f}, Shader.TileMode.CLAMP);
        paintGlow.setShader(glow);
        canvas.drawCircle(cx, cy, r * 1.4f, paintGlow);

        canvas.drawCircle(cx, cy, r, paintSfondo);

        if (LunaCalcolo.isNuova(fase)) {
            paintCrater.setAlpha(40);
            canvas.drawCircle(cx, cy, r, paintCrater);
            return;
        }

        int sfondoColor = ContextCompat.getColor(getContext(), R.color.luna_sfondo);
        Path path = new Path();
        if (LunaCalcolo.isCrescente(fase)) {
            path.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.drawPath(path, paintLuce);
            Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
            cut.setColor(sfondoColor);
            float offset = r * (float)(1 - illuminazione * 2);
            canvas.drawOval(cx - r + offset, cy - r, cx + offset, cy + r, cut);
        } else if (LunaCalcolo.isPiena(fase)) {
            canvas.drawCircle(cx, cy, r, paintLuce);
        } else {
            path.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.drawPath(path, paintLuce);
            Paint cut = new Paint(Paint.ANTI_ALIAS_FLAG);
            cut.setColor(sfondoColor);
            float offset = r * (float)(illuminazione * 2 - 1);
            canvas.drawOval(cx + offset, cy - r, cx + r + offset, cy + r, cut);
        }

        paintCrater.setAlpha(60);
        canvas.drawCircle(cx - r * 0.25f, cy - r * 0.3f,  r * 0.09f, paintCrater);
        canvas.drawCircle(cx + r * 0.3f,  cy - r * 0.1f,  r * 0.06f, paintCrater);
        canvas.drawCircle(cx + r * 0.1f,  cy + r * 0.35f, r * 0.08f, paintCrater);
        canvas.drawCircle(cx - r * 0.4f,  cy + r * 0.2f,  r * 0.05f, paintCrater);
        canvas.drawCircle(cx + r * 0.4f,  cy + r * 0.3f,  r * 0.04f, paintCrater);
    }
}
