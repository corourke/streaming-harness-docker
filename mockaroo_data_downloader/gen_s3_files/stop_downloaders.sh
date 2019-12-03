
: "${DATA_DIR:=/tmp/scan_data}"
STATUS_FILE=$DATA_DIR/loader_status

if [ ! -f $STATUS_FILE ]; then
  echo "Can't find the status file"
  exit 1
fi

echo "STOP" `date` >> $STATUS_FILE
