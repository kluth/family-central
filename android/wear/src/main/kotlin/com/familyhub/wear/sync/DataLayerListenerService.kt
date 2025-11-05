package com.familyhub.wear.sync

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.tasks.await

/**
 * DataLayerListenerService
 * Listens for data changes from the phone app and syncs to watch
 * Handles real-time updates for tasks, messages, and shopping lists
 */
class DataLayerListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "DataLayerListener"

        // Data paths
        private const val TASKS_PATH = "/familyhub/tasks"
        private const val MESSAGES_PATH = "/familyhub/messages"
        private const val SHOPPING_PATH = "/familyhub/shopping"

        // Capability for detecting phone app
        private const val PHONE_APP_CAPABILITY = "familyhub_phone_app"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: ${dataEvents.count} events received")

        dataEvents.forEach { event ->
            when (event.type) {
                DataEvent.TYPE_CHANGED -> {
                    val dataItem = event.dataItem
                    when (dataItem.uri.path) {
                        TASKS_PATH -> handleTasksUpdate(dataItem)
                        MESSAGES_PATH -> handleMessagesUpdate(dataItem)
                        SHOPPING_PATH -> handleShoppingUpdate(dataItem)
                        else -> Log.d(TAG, "Unknown path: ${dataItem.uri.path}")
                    }
                }
                DataEvent.TYPE_DELETED -> {
                    Log.d(TAG, "DataItem deleted: ${event.dataItem.uri}")
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path}")

        when (messageEvent.path) {
            "/familyhub/sync_request" -> {
                // Phone is requesting a full sync
                Log.d(TAG, "Sync request received from phone")
                requestFullSync()
            }
            "/familyhub/task_completed" -> {
                // Task was completed on phone
                val taskId = String(messageEvent.data)
                Log.d(TAG, "Task completed on phone: $taskId")
                updateTaskStatus(taskId, true)
            }
            else -> {
                Log.d(TAG, "Unknown message path: ${messageEvent.path}")
            }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "Capability changed: ${capabilityInfo.name}")

        if (capabilityInfo.name == PHONE_APP_CAPABILITY) {
            val phoneNodes = capabilityInfo.nodes
            if (phoneNodes.isNotEmpty()) {
                Log.d(TAG, "Phone app detected on ${phoneNodes.size} node(s)")
                requestFullSync()
            } else {
                Log.d(TAG, "Phone app not available")
            }
        }
    }

    private fun handleTasksUpdate(dataItem: DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val tasksJson = dataMap.getString("tasks_json")

            Log.d(TAG, "Tasks updated: $tasksJson")

            // In a real implementation, this would:
            // 1. Parse the JSON
            // 2. Update local database/cache
            // 3. Notify UI to refresh
            // 4. Update complications and tiles

            updateComplicationsAndTiles()
        } catch (e: Exception) {
            Log.e(TAG, "Error handling tasks update", e)
        }
    }

    private fun handleMessagesUpdate(dataItem: DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val messagesJson = dataMap.getString("messages_json")
            val unreadCount = dataMap.getInt("unread_count", 0)

            Log.d(TAG, "Messages updated: $unreadCount unread")

            // In a real implementation, this would:
            // 1. Parse the JSON
            // 2. Update local database/cache
            // 3. Show notification for new messages
            // 4. Update complications

            if (unreadCount > 0) {
                // Show notification
                showMessageNotification(unreadCount)
            }

            updateComplicationsAndTiles()
        } catch (e: Exception) {
            Log.e(TAG, "Error handling messages update", e)
        }
    }

    private fun handleShoppingUpdate(dataItem: DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val itemsJson = dataMap.getString("items_json")
            val completedCount = dataMap.getInt("completed_count", 0)
            val totalCount = dataMap.getInt("total_count", 0)

            Log.d(TAG, "Shopping list updated: $completedCount/$totalCount completed")

            // In a real implementation, this would:
            // 1. Parse the JSON
            // 2. Update local database/cache
            // 3. Notify UI to refresh
            // 4. Update tiles

            updateComplicationsAndTiles()
        } catch (e: Exception) {
            Log.e(TAG, "Error handling shopping update", e)
        }
    }

    private fun requestFullSync() {
        Log.d(TAG, "Requesting full sync from phone")
        // In a real implementation, send message to phone app requesting full data sync
    }

    private fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        Log.d(TAG, "Updating task $taskId to completed=$isCompleted")
        // In a real implementation, update local cache and UI
        updateComplicationsAndTiles()
    }

    private fun showMessageNotification(unreadCount: Int) {
        // In a real implementation, create and show a notification
        // using NotificationCompat.Builder with appropriate channel
        Log.d(TAG, "Would show notification for $unreadCount unread messages")
    }

    private fun updateComplicationsAndTiles() {
        // In a real implementation, this would:
        // 1. Request complication updates using ComplicationDataSourceUpdateRequester
        // 2. Request tile updates using TileService.getUpdater()
        Log.d(TAG, "Updating complications and tiles")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }
}

/**
 * DataSyncManager
 * Helper class for sending data from watch to phone
 */
object DataSyncManager {
    private const val TAG = "DataSyncManager"

    /**
     * Send task completion status to phone
     */
    suspend fun sendTaskCompletion(
        context: android.content.Context,
        taskId: String,
        isCompleted: Boolean
    ) {
        try {
            val messageClient = Wearable.getMessageClient(context)
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()

            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    "/familyhub/task_completed",
                    "$taskId:$isCompleted".toByteArray()
                ).await()

                Log.d(TAG, "Sent task completion to ${node.displayName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending task completion", e)
        }
    }

    /**
     * Send shopping item check status to phone
     */
    suspend fun sendShoppingItemCheck(
        context: android.content.Context,
        itemId: String,
        isChecked: Boolean
    ) {
        try {
            val messageClient = Wearable.getMessageClient(context)
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()

            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    "/familyhub/shopping_checked",
                    "$itemId:$isChecked".toByteArray()
                ).await()

                Log.d(TAG, "Sent shopping item check to ${node.displayName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending shopping item check", e)
        }
    }

    /**
     * Request full data sync from phone
     */
    suspend fun requestSync(context: android.content.Context) {
        try {
            val messageClient = Wearable.getMessageClient(context)
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()

            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    "/familyhub/sync_request",
                    ByteArray(0)
                ).await()

                Log.d(TAG, "Sent sync request to ${node.displayName}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting sync", e)
        }
    }
}
