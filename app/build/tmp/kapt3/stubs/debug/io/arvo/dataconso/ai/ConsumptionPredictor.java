package io.arvo.dataconso.ai;

/**
 * Moteur de prédiction ARVO basé sur LSTM.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u0006\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0002J\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lio/arvo/dataconso/ai/ConsumptionPredictor;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "loadModel", "", "predict7Days", "", "", "history", "app_debug"})
public final class ConsumptionPredictor {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private org.tensorflow.lite.Interpreter interpreter;
    
    @javax.inject.Inject()
    public ConsumptionPredictor(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final void loadModel() {
    }
    
    /**
     * Prédit la consommation pour les 7 prochains jours.
     * @param history Historique des derniers jours (en Mo)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.Double> predict7Days(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Double> history) {
        return null;
    }
}