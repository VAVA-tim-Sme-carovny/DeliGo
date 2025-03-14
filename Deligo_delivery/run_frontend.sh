#!/bin/bash

PID_FILE="frontend.pid"

start_frontend() {
    echo "🚀 Starting Frontend..."
    java --module-path lib --add-modules javafx.controls,javafx.fxml -cp target/Deligo_delivery-1.0-SNAPSHOT.jar com.MainApp frontend &
    echo $! > $PID_FILE
    echo "✅ Frontend started with PID $(cat $PID_FILE)"
}

stop_frontend() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat $PID_FILE)
        if kill -9 "$PID" 2>/dev/null; then
            echo "🛑 Frontend stopped."
        else
            echo "⚠️ No running Frontend process found."
        fi
        rm -f $PID_FILE
    else
        echo "⚠️ No PID file found. Frontend might not be running."
    fi
}

case "$1" in
    start)
        start_frontend
        ;;
    stop)
        stop_frontend
        ;;
    restart)
        stop_frontend
        start_frontend
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac