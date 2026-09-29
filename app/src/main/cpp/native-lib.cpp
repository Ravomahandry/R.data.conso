#include <jni.h>
#include <string>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <atomic>
#include <android/log.h>
#include <poll.h>

#define LOG_TAG "ARVO_NATIVE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Variables atomiques natives pour éviter les appels JNI dans la boucle
std::atomic<bool> native_global_blocked{false};
std::atomic<int> loop_generation{0};

extern "C" {

JNIEXPORT void JNICALL
Java_io_arvo_dataconso_network_RealPacketInterceptor_updateNativeState(
        JNIEnv *env, jobject thiz, jboolean global) {
    native_global_blocked.store(global);
}

JNIEXPORT void JNICALL
Java_io_arvo_dataconso_network_RealPacketInterceptor_stopNativeLoop(
        JNIEnv *env, jobject thiz) {
    loop_generation.fetch_add(1);
}

JNIEXPORT jint JNICALL
Java_io_arvo_dataconso_network_RealPacketInterceptor_beginNativeLoop(
        JNIEnv *env, jobject thiz) {
    return loop_generation.fetch_add(1) + 1;
}

JNIEXPORT void JNICALL
Java_io_arvo_dataconso_network_RealPacketInterceptor_runNativePacketLoop(
        JNIEnv *env, jobject thiz, jint fd, jboolean global, jint generation) {

    int tun_fd = fd;
    uint8_t buffer[32768];
    // Synchronisation forcée au démarrage de la boucle
    native_global_blocked.store(global);

    __android_log_print(ANDROID_LOG_INFO, "ARVO_NATIVE", "Engine Started: Global=%d", global);

    while (loop_generation.load() == generation) {
        pollfd descriptor{};
        descriptor.fd = tun_fd;
        descriptor.events = POLLIN;
        const int ready = poll(&descriptor, 1, 250);
        if (ready == 0) continue;
        if (ready < 0) {
            if (errno == EINTR) continue;
            __android_log_print(ANDROID_LOG_ERROR, "ARVO_NATIVE", "Poll error: %s", strerror(errno));
            break;
        }
        if ((descriptor.revents & (POLLERR | POLLHUP | POLLNVAL)) != 0) {
            if (loop_generation.load() != generation) break;
            __android_log_print(ANDROID_LOG_ERROR, "ARVO_NATIVE", "TUN descriptor became unavailable");
            break;
        }

        ssize_t nread = read(tun_fd, buffer, sizeof(buffer));

        if (nread < 0) {
            if (errno == EINTR) continue;
            if (errno == EAGAIN || errno == EWOULDBLOCK) {
                usleep(1000);
                continue;
            }
            __android_log_print(ANDROID_LOG_ERROR, "ARVO_NATIVE", "Read error: %s", strerror(errno));
            break;
        }
        if (nread == 0) {
            __android_log_print(ANDROID_LOG_WARN, "ARVO_NATIVE", "Read 0 bytes, closing loop");
            break;
        }

        // En mode sélectif / Ghost Mode / Firewall, tout paquet lu du TUN doit être droppé (black hole)
        // car le TUN ne fait pas de routing IP complet sans write, mais comme on route le trafic vers le TUN (0.0.0.0/0),
        // ne pas dropper signifie que le trafic reste bloqué dans le tampon du TUN (bloquant la connexion de l'app).
        // En faisant `continue` (drop), on vide le TUN et on bloque effectivement le trafic des apps ciblées.
        continue;
    }
}

}
