package io.arvo.dataconso;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000N\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aD\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u001a(\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0003\u001a\u0010\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001a*\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u0019\u001a\u00020\u001aH\u0007\u00a8\u0006\u001b"}, d2 = {"HistoryScreen", "", "records", "", "Lio/arvo/dataconso/HistoryRecord;", "selectedSource", "Lio/arvo/dataconso/NetworkSource;", "colors", "Lio/arvo/dataconso/ui/DataConsColors;", "isPremium", "", "onExportPdf", "Lkotlin/Function0;", "StatMiniTile", "label", "", "value", "color", "", "modifier", "Landroidx/compose/ui/Modifier;", "PremiumLockedHistoryCard", "DailyAppUsageDetails", "selectedDateMillis", "", "mainVm", "Lio/arvo/dataconso/MainViewModel;", "app_debug"})
public final class HistoryScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HistoryScreen(@org.jetbrains.annotations.NotNull()
    java.util.List<io.arvo.dataconso.HistoryRecord> records, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.NetworkSource selectedSource, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.ui.DataConsColors colors, boolean isPremium, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onExportPdf) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StatMiniTile(java.lang.String label, java.lang.String value, java.lang.Object color, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PremiumLockedHistoryCard(io.arvo.dataconso.ui.DataConsColors colors) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void DailyAppUsageDetails(long selectedDateMillis, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.NetworkSource selectedSource, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.ui.DataConsColors colors, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.MainViewModel mainVm) {
    }
}