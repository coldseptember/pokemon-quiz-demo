#!/bin/bash

# Monkey Test Script - Pokemon Quiz (10 minutes)
# Runs adb monkey against com.example.pokemonquiz and saves the log.

PACKAGE="com.example.pokemonquiz"
DURATION_MINUTES=10
LOG_DIR="./monkey_logs"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/monkey_$TIMESTAMP.log"

mkdir -p "$LOG_DIR"

echo "=============================================="
echo "  Pokemon Quiz - Monkey Test ($DURATION_MINUTES min)"
echo "=============================================="
echo ""

# 1. Check adb
if ! command -v adb &> /dev/null; then
    echo "[ERROR] adb not found. Install Android SDK platform-tools first."
    exit 1
fi

# 2. Check device
DEVICE_COUNT=$(adb devices | grep -v "List of devices" | grep "device$" | wc -l | tr -d ' ')
if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "[ERROR] No device connected. Plug in a device or start an emulator."
    exit 1
fi

DEVICE_NAME=$(adb devices | grep "device$" | head -1 | awk '{print $1}')
echo "[OK] Device: $DEVICE_NAME"

# 3. Check app installed
if ! adb shell pm list packages "$PACKAGE" | grep -q "$PACKAGE"; then
    echo "[ERROR] $PACKAGE is not installed on the device."
    exit 1
fi
echo "[OK] App: $PACKAGE"

# 4. Clear logcat first for cleaner crash detection
adb logcat -c
echo "[OK] Logcat cleared"

# 5. Calculate event count: 10 min / 0.3s throttle ≈ 2000 events
THROTTLE=300
EVENTS=$((DURATION_MINUTES * 60 * 1000 / THROTTLE))

echo ""
echo "----------------------------------------------"
echo "  Starting monkey test..."
echo "  Duration:  ${DURATION_MINUTES} minutes"
echo "  Throttle:  ${THROTTLE}ms"
echo "  Events:    ~${EVENTS}"
echo "  Log file:  ${LOG_FILE}"
echo "----------------------------------------------"
echo ""

# 6. Start logcat in background (capture crashes/ANRs)
adb logcat -v time *:E > "$LOG_DIR/crash_$TIMESTAMP.log" 2>&1 &
LOGCAT_PID=$!

# 7. Run monkey
START_TIME=$(date +%s)

adb shell monkey \
    -p "$PACKAGE" \
    --throttle $THROTTLE \
    --ignore-crashes \
    --ignore-timeouts \
    --ignore-security-exceptions \
    --monitor-native-crashes \
    --kill-process-after-error \
    --pct-touch 40 \
    --pct-motion 15 \
    --pct-trackball 0 \
    --pct-nav 5 \
    --pct-majornav 10 \
    --pct-syskeys 5 \
    --pct-appswitch 10 \
    --pct-anyevent 15 \
    -v \
    $EVENTS \
    2>&1 | tee "$LOG_FILE"

EXIT_CODE=${PIPESTATUS[0]}

END_TIME=$(date +%s)
ELAPSED=$((END_TIME - START_TIME))
ELAPSED_FMT=$(printf "%02d:%02d" $((ELAPSED/60)) $((ELAPSED%60)))

echo ""
echo "----------------------------------------------"
echo "  Monkey test finished"
echo "  Elapsed: ${ELAPSED_FMT}"
echo "  Exit code: $EXIT_CODE"
echo "----------------------------------------------"

# 8. Kill logcat
kill $LOGCAT_PID 2>/dev/null

# 9. Summary
echo ""
echo "=============================================="
echo "  Summary"
echo "=============================================="

# Count crashes
CRASH_COUNT=$(grep -c "FATAL EXCEPTION\|AndroidRuntime.*FATAL" "$LOG_DIR/crash_$TIMESTAMP.log" 2>/dev/null || echo 0)

# Count ANRs
ANR_COUNT=$(grep -c "ANR in" "$LOG_DIR/crash_$TIMESTAMP.log" 2>/dev/null || echo 0)

# Monkey summary
MONKEY_RESULT=$(tail -5 "$LOG_FILE" 2>/dev/null)

echo "  Crashes (FATAL): $CRASH_COUNT"
echo "  ANRs:            $ANR_COUNT"
echo "  Duration:        ${ELAPSED_FMT}"
echo ""
echo "  Full log:        $LOG_FILE"
echo "  Crash log:       $LOG_DIR/crash_$TIMESTAMP.log"
echo "=============================================="

# 10. Exit with monkey's exit code
if [ "$CRASH_COUNT" -gt 0 ] || [ "$ANR_COUNT" -gt 0 ]; then
    echo ""
    echo "[WARN] Crashes or ANRs detected. Check above log files."
    exit 1
else
    echo ""
    echo "[OK] No crashes or ANRs detected."
    exit 0
fi
