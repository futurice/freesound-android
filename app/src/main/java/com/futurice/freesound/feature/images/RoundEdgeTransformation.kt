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

package com.futurice.freesound.feature.images

import android.graphics.*
import com.squareup.picasso.Transformation

internal class RoundEdgeTransformation : Transformation {

    override fun transform(source: Bitmap): Bitmap {

        val roundEdgeBitmap = Bitmap
                .createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)

        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val c = Canvas(roundEdgeBitmap)
        val shortestAxis = Math.min(source.width, source.height).toFloat()
        c.drawCircle(source.width / 2f,source.height / 2f,
                shortestAxis / 2f,
                paint)

        if (roundEdgeBitmap != source) {
            source.recycle()
        }
        return roundEdgeBitmap
    }

    override fun key(): String = "roundEdge"
}
