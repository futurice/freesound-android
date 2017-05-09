/*
 * Copyright 2017 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.feature.images;

import com.squareup.picasso.Transformation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

final class RoundEdgeTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {

        Bitmap roundEdgeBitmap = Bitmap
                .createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Canvas c = new Canvas(roundEdgeBitmap);
        int shortestAxis = Math.min(source.getWidth(), source.getHeight());
        c.drawCircle(source.getWidth() / 2, source.getHeight() / 2, (float) shortestAxis / 2f,
                     paint);

        if (roundEdgeBitmap != source) {
            source.recycle();
        }
        return roundEdgeBitmap;
    }

    @Override
    public String key() {
        return "roundEdge";
    }
}
