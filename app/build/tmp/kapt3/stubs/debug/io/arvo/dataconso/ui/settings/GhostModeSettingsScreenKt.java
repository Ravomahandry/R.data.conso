package io.arvo.dataconso.ui.settings;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000P\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a[\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u00a2\u0006\u0002\u0010\r\u001a$\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00072\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0018\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0007\u001a7\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0007\u00a2\u0006\u0004\b \u0010!\u001a&\u0010\"\u001a\u00020\u00012\u0006\u0010#\u001a\u00020\u00052\u0006\u0010$\u001a\u00020\u00072\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0007\u001a\b\u0010&\u001a\u00020\u0001H\u0007\u00a8\u0006\'"}, d2 = {"GhostModeSettingsScreen", "", "settings", "error/NonExistentClass", "currentProvider", "Lio/arvo/dataconso/DnsProvider;", "isGhostModeEnabled", "", "onProviderSelected", "Lkotlin/Function1;", "onToggleGhostMode", "onBack", "Lkotlin/Function0;", "(Lerror/NonExistentClass;Lio/arvo/dataconso/DnsProvider;ZLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "GhostModeStatusCard", "isEnabled", "onToggle", "GhostModeStatsRow", "trackers", "", "dataSaved", "", "StatCard", "title", "", "value", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "color", "Landroidx/compose/ui/graphics/Color;", "modifier", "Landroidx/compose/ui/Modifier;", "StatCard-42QJj7c", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;JLandroidx/compose/ui/Modifier;)V", "DnsProviderItem", "provider", "isSelected", "onSelect", "SecurityNote", "app_debug"})
public final class GhostModeSettingsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void GhostModeSettingsScreen(@org.jetbrains.annotations.NotNull()
    error.NonExistentClass settings, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.DnsProvider currentProvider, boolean isGhostModeEnabled, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super io.arvo.dataconso.DnsProvider, kotlin.Unit> onProviderSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onToggleGhostMode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GhostModeStatusCard(boolean isEnabled, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onToggle) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GhostModeStatsRow(int trackers, double dataSaved) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DnsProviderItem(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.DnsProvider provider, boolean isSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SecurityNote() {
    }
}