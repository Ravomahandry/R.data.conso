package io.arvo.dataconso

import io.arvo.dataconso.util.FormatUtils
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class HotspotFormattingTest {
    @Test
    fun formatsBytesWithBThroughGb() {
        val originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
        try {
            assertEquals("0 B", FormatUtils.formatHotspotDataSize(0))
            assertEquals("512 B", FormatUtils.formatHotspotDataSize(512))
            assertEquals("1.0 KB", FormatUtils.formatHotspotDataSize(1_024))
            assertEquals("1.0 MB", FormatUtils.formatHotspotDataSize(1_048_576))
            assertEquals("1.0 GB", FormatUtils.formatHotspotDataSize(1_073_741_824))
            assertEquals("0 B", FormatUtils.formatHotspotDataSize(-1))
        } finally {
            Locale.setDefault(originalLocale)
        }
    }
}
