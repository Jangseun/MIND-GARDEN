package com.mindgarden.app.garden;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.mindgarden.app.R;

import java.util.ArrayList;
import java.util.List;

public class GardenSceneView extends View {
    private static final float CANVAS_WIDTH = 360f;
    private static final float CANVAS_HEIGHT = 400f;
    private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Bed> beds = new ArrayList<>();
    private final List<Prop> props = new ArrayList<>();
    private int passionCount;
    private int careCount;
    private int consistencyCount;
    private int responsibilityCount;
    private float scale = 1f;
    private float offsetX;
    private float offsetY;

    public GardenSceneView(Context context) {
        super(context);
        initialize();
    }

    public GardenSceneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GardenSceneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        beds.add(new Bed("responsibility", GardenRules.KEYWORD_RESPONSIBILITY,
                18, 152, 120, 78, 0xFFD8C9E9));
        beds.add(new Bed("passion", GardenRules.KEYWORD_PASSION,
                220, 152, 120, 78, 0xFFF4DDA5));
        beds.add(new Bed("care", GardenRules.KEYWORD_CARE,
                12, 258, 145, 105, 0xFFE9B9BE));
        beds.add(new Bed("consistency", GardenRules.KEYWORD_CONSISTENCY,
                202, 258, 145, 105, 0xFFC9D8AF));
        props.add(new Prop("fence", 40, 120, 280, 65, "하얀 울타리", 10));
        props.add(new Prop("fountain", 143, 160, 74, 90, "작은 분수", 25));
        props.add(new Prop("bench", 52, 212, 86, 70, "작은 나무 벤치", 5));
        props.add(new Prop("lamp", 270, 205, 50, 100, "작은 정원등", 15));
        props.add(new Prop("sign", 15, 322, 65, 55, "나무 이름표", 3));
        setContentDescription(getContext().getString(R.string.garden_illustration));
    }

    public void setGardenState(int passion, int care, int consistency, int responsibility,
                               List<GardenRules.Structure> newlyUnlocked) {
        passionCount = passion;
        careCount = care;
        consistencyCount = consistency;
        responsibilityCount = responsibility;
        invalidate();
        for (GardenRules.Structure structure : newlyUnlocked) {
            Toast.makeText(getContext(),
                    getContext().getString(R.string.structure_unlocked,
                            structure.name, structure.message),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scale = Math.min(getWidth() / CANVAS_WIDTH, getHeight() / CANVAS_HEIGHT);
        offsetX = (getWidth() - CANVAS_WIDTH * scale) / 2f;
        offsetY = (getHeight() - CANVAS_HEIGHT * scale) / 2f;
        int save = canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        Drawable background = drawable("garden_base");
        if (background == null) {
            drawPlaceholderBackground(canvas);
        } else {
            drawDrawable(canvas, background, 0, 0, 360, 400);
        }

        drawProp(canvas, props.get(0));
        for (Bed bed : beds.subList(0, 2)) {
            drawBed(canvas, bed);
        }
        drawProp(canvas, props.get(1));
        drawProp(canvas, props.get(2));
        drawProp(canvas, props.get(3));
        for (Bed bed : beds.subList(2, 4)) {
            drawBed(canvas, bed);
        }
        drawProp(canvas, props.get(4));
        canvas.restoreToCount(save);
    }

    private void drawPlaceholderBackground(Canvas canvas) {
        canvas.drawColor(0xFFDCEBF0);
        PAINT.setColor(0xFFF5EBCF);
        canvas.drawCircle(48, 47, 21, PAINT);
        PAINT.setColor(0xFFB8CFAB);
        canvas.drawOval(new RectF(-30, 94, 210, 230), PAINT);
        PAINT.setColor(0xFFA8C69D);
        canvas.drawOval(new RectF(132, 82, 402, 234), PAINT);
        PAINT.setColor(0xFF8DAE83);
        canvas.drawOval(new RectF(-35, 177, 395, 454), PAINT);

        Path path = new Path();
        path.moveTo(165, 400);
        path.cubicTo(191, 352, 155, 316, 188, 282);
        path.cubicTo(220, 247, 185, 218, 174, 193);
        path.cubicTo(167, 174, 177, 153, 183, 142);
        path.cubicTo(195, 175, 208, 203, 211, 228);
        path.cubicTo(217, 267, 199, 282, 200, 307);
        path.cubicTo(201, 345, 221, 370, 220, 400);
        path.close();
        PAINT.setColor(0xFFE8D8B8);
        canvas.drawPath(path, PAINT);
        PAINT.setColor(0x88FFFFFF);
        PAINT.setStyle(Paint.Style.STROKE);
        PAINT.setStrokeWidth(2);
        canvas.drawPath(path, PAINT);
        PAINT.setStyle(Paint.Style.FILL);
    }

    private void drawBed(Canvas canvas, Bed bed) {
        int count = countFor(bed.keyword);
        int level = GardenRules.flowerLevel(count);
        if (level == 0) {
            PAINT.setColor(0xFF9C7658);
            canvas.drawOval(new RectF(bed.x, bed.y + bed.height * .67f,
                    bed.x + bed.width, bed.y + bed.height), PAINT);
            PAINT.setColor(0x55805B43);
            canvas.drawOval(new RectF(bed.x + 5, bed.y + bed.height * .72f,
                    bed.x + bed.width - 5, bed.y + bed.height * .93f), PAINT);
            return;
        }
        Drawable flower = drawable("garden_" + bed.assetKey + "_lv" + level);
        if (flower != null) {
            drawDrawable(canvas, flower, bed.x, bed.y, bed.width, bed.height);
        } else {
            drawFlowerPlaceholder(canvas, bed, level);
        }
    }

    private void drawFlowerPlaceholder(Canvas canvas, Bed bed, int level) {
        float ground = bed.y + bed.height * .86f;
        int flowers = level == 1 ? 2 : level == 2 ? 5 : level == 3 ? 9 : 14;
        PAINT.setColor(0xFF66845D);
        PAINT.setStrokeWidth(2.5f);
        for (int i = 0; i < flowers; i++) {
            float t = (i + 1f) / (flowers + 1f);
            float x = bed.x + t * bed.width;
            float stem = bed.height * (0.25f + ((i * 7) % 4) * .075f);
            canvas.drawLine(x, ground, x, ground - stem, PAINT);
            PAINT.setColor(bed.flowerColor);
            canvas.drawCircle(x, ground - stem - 3, 5 + level * .35f, PAINT);
            PAINT.setColor(0xFFE9D67B);
            canvas.drawCircle(x, ground - stem - 3, 2.2f, PAINT);
            PAINT.setColor(0xFF66845D);
        }
    }

    private void drawProp(Canvas canvas, Prop prop) {
        if (!isUnlocked(prop.id)) {
            return;
        }
        Drawable image = drawable("garden_" + prop.id);
        if (image != null) {
            drawDrawable(canvas, image, prop.x, prop.y, prop.width, prop.height);
        } else {
            drawPlaceholderProp(canvas, prop);
        }
    }

    private void drawPlaceholderProp(Canvas canvas, Prop prop) {
        PAINT.setColor(0xFF71866A);
        PAINT.setStrokeWidth(3);
        if (prop.id.equals("fence")) {
            PAINT.setColor(0xFFF4F0E3);
            PAINT.setStrokeWidth(3);
            canvas.drawLine(prop.x, prop.y + prop.height * .45f,
                    prop.x + prop.width, prop.y + prop.height * .45f, PAINT);
            canvas.drawLine(prop.x, prop.y + prop.height * .78f,
                    prop.x + prop.width, prop.y + prop.height * .78f, PAINT);
            for (int x = (int) prop.x; x <= prop.x + prop.width; x += 22) {
                canvas.drawLine(x, prop.y + 5, x, prop.y + prop.height - 2, PAINT);
                Path tip = new Path();
                tip.moveTo(x - 5, prop.y + 8);
                tip.lineTo(x, prop.y);
                tip.lineTo(x + 5, prop.y + 8);
                tip.close();
                canvas.drawPath(tip, PAINT);
            }
        } else if (prop.id.equals("bench")) {
            PAINT.setColor(0xFF9A704C);
            canvas.drawRoundRect(new RectF(prop.x + 7, prop.y + 20,
                    prop.x + prop.width - 7, prop.y + 37), 6, 6, PAINT);
            canvas.drawLine(prop.x + 18, prop.y + 38, prop.x + 14, prop.y + 60, PAINT);
            canvas.drawLine(prop.x + prop.width - 18, prop.y + 38,
                    prop.x + prop.width - 14, prop.y + 60, PAINT);
        } else if (prop.id.equals("lamp")) {
            PAINT.setColor(0xFF846F50);
            canvas.drawLine(prop.x + prop.width / 2, prop.y + 33,
                    prop.x + prop.width / 2, prop.y + prop.height, PAINT);
            PAINT.setColor(0xFFF5D987);
            canvas.drawCircle(prop.x + prop.width / 2, prop.y + 25, 16, PAINT);
            PAINT.setColor(0xFFFFF3C8);
            canvas.drawCircle(prop.x + prop.width / 2, prop.y + 25, 8, PAINT);
        } else if (prop.id.equals("fountain")) {
            PAINT.setColor(0xFFB9C9C0);
            canvas.drawOval(new RectF(prop.x + 4, prop.y + 54,
                    prop.x + prop.width - 4, prop.y + prop.height), PAINT);
            PAINT.setColor(0xFF9DC5CA);
            canvas.drawOval(new RectF(prop.x + 13, prop.y + 58,
                    prop.x + prop.width - 13, prop.y + prop.height - 8), PAINT);
            PAINT.setColor(0xFFD6C7A9);
            canvas.drawRect(prop.x + 30, prop.y + 28, prop.x + 44, prop.y + 62, PAINT);
            canvas.drawOval(new RectF(prop.x + 16, prop.y + 23,
                    prop.x + 58, prop.y + 38), PAINT);
        } else {
            PAINT.setColor(0xFFB68D63);
            canvas.drawRoundRect(new RectF(prop.x + 3, prop.y + 4,
                    prop.x + prop.width - 3, prop.y + prop.height - 4),
                    5, 5, PAINT);
            PAINT.setColor(0xFF465947);
            PAINT.setTextSize(8);
            PAINT.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("마음정원", prop.x + 10, prop.y + 31, PAINT);
        }
    }

    private void drawDrawable(Canvas canvas, Drawable drawable,
                              float x, float y, float width, float height) {
        int save = canvas.save();
        canvas.translate(x, y);
        drawable.setBounds(0, 0, (int) width, (int) height);
        drawable.draw(canvas);
        canvas.restoreToCount(save);
    }

    private Drawable drawable(String name) {
        int id = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
        return id == 0 ? null : getResources().getDrawable(id, getContext().getTheme());
    }

    private int countFor(String keyword) {
        if (GardenRules.KEYWORD_PASSION.equals(keyword)) return passionCount;
        if (GardenRules.KEYWORD_CARE.equals(keyword)) return careCount;
        if (GardenRules.KEYWORD_CONSISTENCY.equals(keyword)) return consistencyCount;
        return responsibilityCount;
    }

    private boolean isUnlocked(String id) {
        for (GardenRules.Structure structure : GardenRules.STRUCTURES) {
            if (structure.id.equals(id)) {
                return UnlockRepository.isUnlocked(getContext(), structure);
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() != android.view.MotionEvent.ACTION_UP) {
            return true;
        }
        float x = (event.getX() - offsetX) / scale;
        float y = (event.getY() - offsetY) / scale;
        for (int i = props.size() - 1; i >= 0; i--) {
            Prop prop = props.get(i);
            if (contains(prop.x, prop.y, prop.width, prop.height, x, y)) {
                String message = isUnlocked(prop.id)
                        ? getContext().getString(R.string.structure_info, prop.name)
                        : getContext().getString(R.string.structure_locked, prop.name, prop.threshold);
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                return true;
            }
        }
        for (int i = beds.size() - 1; i >= 0; i--) {
            Bed bed = beds.get(i);
            if (contains(bed.x, bed.y, bed.width, bed.height, x, y)) {
                int count = countFor(bed.keyword);
                int level = GardenRules.flowerLevel(count);
                new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getString(R.string.flower_bed_info,
                                bed.keyword, count, level))
                        .setPositiveButton(R.string.ok, null)
                        .show();
                return true;
            }
        }
        return true;
    }

    private boolean contains(float x, float y, float width, float height,
                             float touchX, float touchY) {
        return touchX >= x && touchX <= x + width
                && touchY >= y && touchY <= y + height;
    }

    private static final class Bed {
        final String assetKey;
        final String keyword;
        final float x;
        final float y;
        final float width;
        final float height;
        final int flowerColor;

        Bed(String assetKey, String keyword, float x, float y,
            float width, float height, int flowerColor) {
            this.assetKey = assetKey;
            this.keyword = keyword;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.flowerColor = flowerColor;
        }
    }

    private static final class Prop {
        final String id;
        final float x;
        final float y;
        final float width;
        final float height;
        final String name;
        final int threshold;

        Prop(String id, float x, float y, float width, float height,
             String name, int threshold) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.name = name;
            this.threshold = threshold;
        }
    }
}
