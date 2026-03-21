package com.nativeknights.leetflow.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.widget.RemoteViews
import com.nativeknights.leetflow.MainActivity
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.data.models.GraphQLRequest
import com.nativeknights.leetflow.data.remote.LeetCodeClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeetCodeStatsWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val result = goAsync()
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                try {
                    refreshWidget(context, appWidgetManager, appWidgetId)
                } finally {
                    result.finish()
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val result = goAsync()
                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    try {
                        refreshWidget(context, appWidgetManager, appWidgetId)
                    } finally {
                        result.finish()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.nativeknights.leetflow.WIDGET_REFRESH"

        private val STATS_QUERY = """
            query getUserData(${'$'}username: String!) {
              allQuestionsCount { difficulty count }
              matchedUser(username: ${'$'}username) {
                profile { ranking }
                submitStats { acSubmissionNum { difficulty count submissions } }
                userCalendar { streak totalActiveDays submissionCalendar }
              }
            }
        """.trimIndent()

        suspend fun refreshWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.getSharedPreferences("leetflow_lc_prefs", Context.MODE_PRIVATE)
            val username = prefs.getString("lc_username", "") ?: ""

            val views = RemoteViews(context.packageName, R.layout.widget_leetcode_stats)

            // Always set up the open-app intent on root
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)

            // Always set up the refresh intent on the refresh button
            val refreshIntent = Intent(context, LeetCodeStatsWidget::class.java).apply {
                action = ACTION_REFRESH
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId + 1000,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)

            if (username.isBlank()) {
                showEmptyState(views, "⚡\nOpen LeetFlow to set your username")
                appWidgetManager.updateAppWidget(appWidgetId, views)
                return
            }

            try {
                views.setTextViewText(R.id.widget_username, "@$username")

                val resp = LeetCodeClient.service.getUserStats(
                    GraphQLRequest(STATS_QUERY, mapOf("username" to username))
                )

                val matchedUser = resp.data?.matchedUser
                if (matchedUser == null) {
                    showEmptyState(views, "User \"$username\" not found")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    return
                }

                val questionCounts = resp.data?.allQuestionsCount ?: emptyList()
                val submissions = matchedUser.submitStats?.acSubmissionNum ?: emptyList()

                fun solvedFor(diff: String) = submissions.find { it.difficulty == diff }?.count ?: 0
                fun totalFor(diff: String) = questionCounts.find { it.difficulty == diff }?.count ?: 0

                val totalSolved = solvedFor("All")
                val totalProblems = totalFor("All")
                val easySolved = solvedFor("Easy")
                val mediumSolved = solvedFor("Medium")
                val hardSolved = solvedFor("Hard")
                val streak = matchedUser.userCalendar?.streak ?: 0
                val activeDays = matchedUser.userCalendar?.totalActiveDays ?: 0

                // Draw the ring bitmap
                val density = context.resources.displayMetrics.density
                val sizePx = (80 * density).toInt().coerceAtLeast(200)
                val ringBitmap = drawRing(
                    totalSolved = totalSolved,
                    totalProblems = totalProblems,
                    easy = easySolved,
                    medium = mediumSolved,
                    hard = hardSolved,
                    sizePx = sizePx
                )

                // Update content views
                views.setViewVisibility(R.id.widget_content, View.VISIBLE)
                views.setViewVisibility(R.id.widget_empty, View.GONE)

                views.setImageViewBitmap(R.id.widget_ring, ringBitmap)
                views.setTextViewText(R.id.widget_easy_count, easySolved.toString())
                views.setTextViewText(R.id.widget_medium_count, mediumSolved.toString())
                views.setTextViewText(R.id.widget_hard_count, hardSolved.toString())
                views.setTextViewText(R.id.widget_streak, "🔥 $streak day streak")
                views.setTextViewText(R.id.widget_active_days, "📅 $activeDays active")

                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                views.setTextViewText(R.id.widget_status, "Updated $timeStr")

            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host", true) == true ->
                        "No internet connection"
                    e.message?.contains("timeout", true) == true ->
                        "Request timed out"
                    else -> "Failed to load stats"
                }
                showEmptyState(views, errorMsg)
                views.setTextViewText(R.id.widget_status, "Tap ↻ to retry")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun showEmptyState(views: RemoteViews, message: String) {
            views.setViewVisibility(R.id.widget_content, View.GONE)
            views.setViewVisibility(R.id.widget_empty, View.VISIBLE)
            views.setTextViewText(R.id.widget_empty_text, message)
            views.setTextViewText(R.id.widget_status, "Tap ↻ to load")
        }

        fun drawRing(
            totalSolved: Int,
            totalProblems: Int,
            easy: Int,
            medium: Int,
            hard: Int,
            sizePx: Int
        ): Bitmap {
            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val strokeWidth = sizePx * 0.11f
            val halfStroke = strokeWidth / 2f
            val oval = RectF(halfStroke, halfStroke, sizePx - halfStroke, sizePx - halfStroke)

            val cx = sizePx / 2f
            val cy = sizePx / 2f

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                strokeCap = Paint.Cap.ROUND
            }

            // Background track arc
            paint.color = Color.parseColor("#1F2937")
            canvas.drawArc(oval, 135f, 270f, false, paint)

            // Draw colored arcs only when there are total problems to avoid division by zero
            if (totalProblems > 0) {
                val scale = 270f / totalProblems.toFloat()

                var startAngle = 135f

                // Easy arc
                val easySweep = easy * scale
                if (easySweep > 0f) {
                    paint.color = Color.parseColor("#34D399")
                    canvas.drawArc(oval, startAngle, easySweep, false, paint)
                    startAngle += easySweep
                }

                // Medium arc
                val mediumSweep = medium * scale
                if (mediumSweep > 0f) {
                    paint.color = Color.parseColor("#FBBF24")
                    canvas.drawArc(oval, startAngle, mediumSweep, false, paint)
                    startAngle += mediumSweep
                }

                // Hard arc
                val hardSweep = hard * scale
                if (hardSweep > 0f) {
                    paint.color = Color.parseColor("#F87171")
                    canvas.drawArc(oval, startAngle, hardSweep, false, paint)
                }
            }

            // Center text — solved count
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
            }

            // Solved number
            textPaint.color = Color.WHITE
            textPaint.textSize = sizePx * 0.22f
            textPaint.isFakeBoldText = true
            canvas.drawText(
                totalSolved.toString(),
                cx,
                cy + sizePx * 0.06f,
                textPaint
            )

            // "solved" label
            textPaint.color = Color.parseColor("#6B7280")
            textPaint.textSize = sizePx * 0.10f
            textPaint.isFakeBoldText = false
            canvas.drawText(
                "solved",
                cx,
                cy + sizePx * 0.22f,
                textPaint
            )

            return bitmap
        }
    }
}
