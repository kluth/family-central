package com.familyhub.wear.health

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.health.services.client.HealthServices
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.data.*
import com.familyhub.wear.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * HealthTrackingService
 * Background service for tracking health and fitness data
 * Uses Health Services API for passive monitoring and active workouts
 */
class HealthTrackingService : Service() {

    companion object {
        private const val TAG = "HealthTrackingService"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_ID = "health_tracking"
        private const val NOTIFICATION_CHANNEL_NAME = "Health Tracking"

        // Health data types
        private val PASSIVE_DATA_TYPES = setOf(
            DataType.STEPS_DAILY,
            DataType.HEART_RATE_BPM,
            DataType.CALORIES_DAILY
        )
    }

    private val binder = HealthServiceBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var healthServicesClient: androidx.health.services.client.HealthServicesClient
    private lateinit var passiveMonitoringClient: androidx.health.services.client.PassiveMonitoringClient

    private val _healthData = MutableStateFlow(HealthData())
    val healthData: StateFlow<HealthData> = _healthData.asStateFlow()

    private val passiveListenerCallback = object : PassiveListenerCallback {
        override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
            Log.d(TAG, "New data points received")

            dataPoints.getData(DataType.STEPS_DAILY).forEach { dataPoint ->
                val steps = dataPoint.value.asLong()
                Log.d(TAG, "Steps: $steps")
                _healthData.value = _healthData.value.copy(steps = steps.toInt())
            }

            dataPoints.getData(DataType.HEART_RATE_BPM).forEach { dataPoint ->
                val heartRate = dataPoint.value.asDouble()
                Log.d(TAG, "Heart rate: $heartRate bpm")
                _healthData.value = _healthData.value.copy(heartRate = heartRate.toInt())
            }

            dataPoints.getData(DataType.CALORIES_DAILY).forEach { dataPoint ->
                val calories = dataPoint.value.asDouble()
                Log.d(TAG, "Calories: $calories")
                _healthData.value = _healthData.value.copy(calories = calories.toInt())
            }
        }

        override fun onPermissionLost() {
            Log.w(TAG, "Permission lost for health tracking")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            Log.e(TAG, "Registration failed", throwable)
        }
    }

    inner class HealthServiceBinder : Binder() {
        fun getService(): HealthTrackingService = this@HealthTrackingService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        healthServicesClient = HealthServices.getClient(this)
        passiveMonitoringClient = healthServicesClient.passiveMonitoringClient

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        startPassiveMonitoring()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun startPassiveMonitoring() {
        serviceScope.launch {
            try {
                val passiveListenerConfig = PassiveListenerConfig.builder()
                    .setDataTypes(PASSIVE_DATA_TYPES)
                    .build()

                passiveMonitoringClient.setPassiveListenerCallback(
                    passiveListenerConfig,
                    passiveListenerCallback
                )

                Log.d(TAG, "Passive monitoring started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start passive monitoring", e)
            }
        }
    }

    private fun stopPassiveMonitoring() {
        serviceScope.launch {
            try {
                passiveMonitoringClient.clearPassiveListenerCallbackAsync()
                Log.d(TAG, "Passive monitoring stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop passive monitoring", e)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Ongoing health and fitness tracking"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("FamilyHub Health")
            .setContentText("Tracking your activity")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        stopPassiveMonitoring()
        serviceScope.cancel()
        super.onDestroy()
    }

    data class HealthData(
        val steps: Int = 0,
        val heartRate: Int = 0,
        val calories: Int = 0,
        val distance: Double = 0.0
    )
}

/**
 * WorkoutManager
 * Manages active workout sessions
 */
class WorkoutManager(private val context: android.content.Context) {

    companion object {
        private const val TAG = "WorkoutManager"
    }

    private val healthServicesClient = HealthServices.getClient(context)
    private val exerciseClient = healthServicesClient.exerciseClient

    private val _workoutState = MutableStateFlow<WorkoutState>(WorkoutState.Idle)
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()

    /**
     * Start a workout session
     */
    suspend fun startWorkout(workoutType: ExerciseType) {
        try {
            Log.d(TAG, "Starting workout: $workoutType")

            val config = ExerciseConfig.builder(workoutType)
                .setDataTypes(
                    setOf(
                        DataType.HEART_RATE_BPM,
                        DataType.DISTANCE,
                        DataType.CALORIES,
                        DataType.STEPS
                    )
                )
                .setIsAutoPauseAndResumeEnabled(true)
                .build()

            exerciseClient.startExerciseAsync(config).await()

            _workoutState.value = WorkoutState.Active(workoutType)
            Log.d(TAG, "Workout started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start workout", e)
            _workoutState.value = WorkoutState.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Pause current workout
     */
    suspend fun pauseWorkout() {
        try {
            exerciseClient.pauseExerciseAsync().await()
            _workoutState.value = WorkoutState.Paused
            Log.d(TAG, "Workout paused")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pause workout", e)
        }
    }

    /**
     * Resume paused workout
     */
    suspend fun resumeWorkout() {
        try {
            exerciseClient.resumeExerciseAsync().await()
            val currentState = _workoutState.value
            if (currentState is WorkoutState.Active) {
                _workoutState.value = currentState
            }
            Log.d(TAG, "Workout resumed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resume workout", e)
        }
    }

    /**
     * End current workout
     */
    suspend fun endWorkout() {
        try {
            exerciseClient.endExerciseAsync().await()
            _workoutState.value = WorkoutState.Idle
            Log.d(TAG, "Workout ended")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to end workout", e)
        }
    }

    sealed class WorkoutState {
        object Idle : WorkoutState()
        data class Active(val type: ExerciseType) : WorkoutState()
        object Paused : WorkoutState()
        data class Error(val message: String) : WorkoutState()
    }
}
