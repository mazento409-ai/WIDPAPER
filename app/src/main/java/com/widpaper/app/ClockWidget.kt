package com.widpaper.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

open class BaseWidget(private val layout: Int) : AppWidgetProvider() {
    override fun onUpdate(ctx: Context, m: AppWidgetManager, ids: IntArray) {
        for (id in ids) {
            val v = RemoteViews(ctx.packageName, layout)
            ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)?.let {
                v.setOnClickPendingIntent(R.id.root, PendingIntent.getActivity(ctx, 0, it, PendingIntent.FLAG_IMMUTABLE))
            }
            m.updateAppWidget(id, v)
        }
    }
}

class IosClock : BaseWidget(R.layout.l_ios)
class IosClockWide : BaseWidget(R.layout.l_ios)
class IosDate : BaseWidget(R.layout.l_date)
class XiaomiStack : BaseWidget(R.layout.l_stack_x)
class XiaomiWide : BaseWidget(R.layout.l_wide_x)
class SamsungWide : BaseWidget(R.layout.l_wide_s)
class SamsungSmall : BaseWidget(R.layout.l_s_small)
class OppoFlux : BaseWidget(R.layout.l_flux)
class OppoFluxWide : BaseWidget(R.layout.l_wide_o)
class OppoFolder : BaseWidget(R.layout.l_folder)
class OppoDate : BaseWidget(R.layout.l_date)
