#!/bin/bash

PID_FILE="restapi.pid"
PORT=8080

start_restapi() {
    # Check if port is already in use
    if lsof -i :$PORT -t > /dev/null; then
        echo "⚠️ REST API is already running on port $PORT. Stop it first!"
        exit 1
    fi

    echo "🚀 Starting REST API..."
    java -cp target/Deligo_delivery-1.0-SNAPSHOT.jar com.MainApp restapi &
    echo $! > $PID_FILE
    echo "✅ REST API started with PID $(cat $PID_FILE)"
}

stop_restapi() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat $PID_FILE)
        if kill -9 "$PID" 2>/dev/null; then
            echo "🛑 REST API stopped."
        else
            echo "⚠️ No running REST API process found."
        fi
        rm -f $PID_FILE
    else
        echo "⚠️ No PID file found. REST API might not be running."
    fi
}

case "$1" in
    start)
        start_restapi
        ;;
    stop)
        stop_restapi
        ;;
    restart)
        stop_restapi
        start_restapi
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac