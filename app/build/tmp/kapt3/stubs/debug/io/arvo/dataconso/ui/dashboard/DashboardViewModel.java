package io.arvo.dataconso.ui.dashboard;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\b\u0010\u0013\u001a\u00020\u0012H\u0002J\u000e\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lio/arvo/dataconso/ui/dashboard/DashboardViewModel;", "Landroidx/lifecycle/ViewModel;", "getDashboardDataUseCase", "Lio/arvo/dataconso/domain/usecase/GetDashboardDataUseCase;", "consumptionPredictor", "Lio/arvo/dataconso/ai/ConsumptionPredictor;", "<init>", "(Lio/arvo/dataconso/domain/usecase/GetDashboardDataUseCase;Lio/arvo/dataconso/ai/ConsumptionPredictor;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lio/arvo/dataconso/ui/dashboard/DashboardState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "dataJob", "Lkotlinx/coroutines/Job;", "startObservingData", "", "observeRealTimeSpeeds", "handleIntent", "intent", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class DashboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final io.arvo.dataconso.domain.usecase.GetDashboardDataUseCase getDashboardDataUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final io.arvo.dataconso.ai.ConsumptionPredictor consumptionPredictor = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<io.arvo.dataconso.ui.dashboard.DashboardState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<io.arvo.dataconso.ui.dashboard.DashboardState> uiState = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job dataJob;
    
    @javax.inject.Inject()
    public DashboardViewModel(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.domain.usecase.GetDashboardDataUseCase getDashboardDataUseCase, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.ai.ConsumptionPredictor consumptionPredictor) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<io.arvo.dataconso.ui.dashboard.DashboardState> getUiState() {
        return null;
    }
    
    private final void startObservingData() {
    }
    
    private final void observeRealTimeSpeeds() {
    }
    
    public final void handleIntent(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.ui.dashboard.DashboardIntent intent) {
    }
}