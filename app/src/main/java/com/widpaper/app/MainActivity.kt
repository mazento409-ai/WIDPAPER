
package com.widpaper.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

open class BaseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {

            val views = RemoteViews(
                context.packageName,
                R.layout.widget_clock
            )

            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(context.packageName)

            launchIntent?.let {
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(
                    R.id.root,
                    pendingIntent
                )
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

class ClockWidget : BaseWidget()

class IosClock : BaseWidget()
class IosClockWide : BaseWidget()
class IosDate : BaseWidget()

class XiaomiStack : BaseWidget()
class XiaomiWide : BaseWidget()

class SamsungWide : BaseWidget()
class SamsungSmall : BaseWidget()

class OppoFlux : BaseWidget()
class OppoFluxWide : BaseWidget()
class OppoFolder : BaseWidget()
class OppoDate : BaseWidget()
