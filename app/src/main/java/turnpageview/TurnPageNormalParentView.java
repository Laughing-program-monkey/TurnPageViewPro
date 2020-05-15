package turnpageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class TurnPageNormalParentView extends TurnPageParentView {
    public TurnPageNormalParentView(Context context) {
        super(context);
    }

    public TurnPageNormalParentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }
    protected  void drawPageEffect(Canvas canvas){};

    protected  void drawEffectMiddle(Canvas canvas,Paint mPaint){};

    protected  void drawEffectSingle(Canvas canvas,Paint mPaint){};
    @Override
    protected void onDraw(Canvas canvas) {
        drawPageEffect(canvas);
        super.onDraw(canvas);
    }

}
