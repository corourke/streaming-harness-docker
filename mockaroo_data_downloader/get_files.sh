# Data Generator -- Localfiles version


# Prechecks
: "${DATA_DIR:=/tmp/scans}"
STATUS_FILE=$DATA_DIR/loader_status

if [ ! -d $DATA_DIR ]; then
	echo "Please ensure that $DATA_DIR is writable."
  exit 1
fi
if [ ! -f $STATUS_FILE ]; then
  echo "creating status file"
  echo "EPOCH" `date` > $STATUS_FILE
fi
if [ ! -d $DATA_DIR/data ]; then
  echo "creating $DATA_DIR/data directory"
  mkdir $DATA_DIR/data
fi
set -e


cleanup ()
{
  echo "STOP" `date` >> $STATUS_FILE
  rm -f $scan_file
  exit 0
}

trap cleanup SIGINT SIGTERM

echo "START" `date` >> $STATUS_FILE


# Create files until told to stop
while [ `tail -1 $STATUS_FILE | cut -d " " -f 1` != "STOP" ]
do
  # Get new sample data rows
  timestamp=`date "+%s"`
  scan_file="$DATA_DIR/data/scans_${timestamp}.csv"
  echo $scan_file
  curl --silent "https://api.mockaroo.com/api/92dbabf0?count=1000&key=76b93870" > $scan_file
  scan_file=""
  sleep 2
done
