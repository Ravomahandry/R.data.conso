package io.arvo.dataconso

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Moteur de Téléphonie ARVO (Phase 4.1)
 * Gère l'identification des cartes SIM et la détection du flux de données actif.
 */
@Singleton
class TelephonyRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val _activeSubscriptions = MutableStateFlow<List<ArvoSimInfo>>(emptyList())
    val activeSubscriptions: StateFlow<List<ArvoSimInfo>> = _activeSubscriptions.asStateFlow()

    data class ArvoSimInfo(
        val subscriptionId: Int,
        val slotIndex: Int,
        val carrierName: String,
        val isDefaultData: Boolean,
        val displayName: String
    )

    @SuppressLint("MissingPermission")
    fun refreshSimInfo() {
        try {
            val subs = subscriptionManager.activeSubscriptionInfoList ?: emptyList()
            val defaultDataId = SubscriptionManager.getDefaultDataSubscriptionId()

            _activeSubscriptions.value = subs.map { info ->
                ArvoSimInfo(
                    subscriptionId = info.subscriptionId,
                    slotIndex = info.simSlotIndex,
                    carrierName = info.carrierName.toString(),
                    isDefaultData = info.subscriptionId == defaultDataId,
                    displayName = "SIM ${info.simSlotIndex + 1}: ${info.displayName}"
                )
            }.sortedBy { it.slotIndex }
        } catch (e: Exception) {
            _activeSubscriptions.value = emptyList()
        }
    }

    /**
     * Récupère le SubscriberId (IMSI) pour une SIM spécifique si la permission est présente.
     * Note: Sur Android 10+, renvoie souvent null pour les apps tierces, on utilise alors le SubId.
     */
    @SuppressLint("MissingPermission")
    fun getSubscriberIdForSim(slotIndex: Int): String? {
        val subs = subscriptionManager.activeSubscriptionInfoList ?: return null
        val target = subs.find { it.simSlotIndex == slotIndex } ?: return null
        
        return try {
            val specificTelephony = telephonyManager.createForSubscriptionId(target.subscriptionId)
            specificTelephony.subscriberId // Peut être null (Android 10+ privacy)
        } catch (e: Exception) {
            null
        }
    }

    fun getDefaultDataSubId(): Int {
        return SubscriptionManager.getDefaultDataSubscriptionId()
    }
}
