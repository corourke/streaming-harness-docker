
: "${DATA_DIR:=/tmp/scan_data}"
: "${REGION:=1}"
: "${LOADERS:=5}"
STATUS_FILE=$DATA_DIR/loader_status

echo "LOADERS: " $LOADERS

if [ ! -d $DATA_DIR ]; then
	echo "Please ensure that $DATA_DIR is writable."
  exit 1
fi

if [ ! -f $STATUS_FILE ]; then
  echo "creating status file"
  echo "EPOCH" `date` > $STATUS_FILE
fi

cleanup ()
{
  echo "STOP" `date` >> $STATUS_FILE
  exit 0
}

trap cleanup SIGINT SIGTERM

echo "START" "LOADERS:" ${LOADERS} "REGION: " ${REGION} `date` >> $STATUS_FILE
for (( n=0; n<$LOADERS; n++))
do
  R=`expr $REGION + $n`
  REGION=${R} ./add_s3_file.sh &
done
