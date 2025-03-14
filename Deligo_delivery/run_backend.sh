#!/bin/bash

PID_FILE="backend.pid"

start_backend() {
    echo "🚀 Starting Backend..."
    java -cp target/Deligo_delivery-1.0-SNAPSHOT.jar com.MainApp backend &
    echo $! > $PID_FILE
    echo "✅ Backend started with PID $(cat $PID_FILE)"
}

stop_backend() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat $PID_FILE)
        if kill -9 "$PID" 2>/dev/null; then
            echo "🛑 Backend stopped."
        else
            echo "⚠️ No running Backend process found."
        fi
        rm -f $PID_FILE
    else
        echo "⚠️ No PID file found. Backend might not be running."
    fi
}

case "$1" in
    start)
        start_backend
        ;;
    stop)
        stop_backend
        ;;
    restart)
        stop_backend
        start_backend
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac