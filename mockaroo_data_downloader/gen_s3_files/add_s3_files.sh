# Data Generator - AWS Version


# Prechecks
: "${REGION:=1}"
: "${DATA_DIR:=/tmp/scan_data}"
STATUS_FILE=$DATA_DIR/loader_status

if [ ! -d $DATA_DIR ]; then
	echo "Please ensure that $DATA_DIR is writable."
  exit 1
fi
if [ ! -f $STATUS_FILE ]; then
  echo "Can't find the status file"
  exit 1
fi
if [ ! -d $DATA_DIR/temp ]; then
  echo "creating $DATA_DIR/temp directory"
  mkdir $DATA_DIR/temp
fi
if [ ! -d $DATA_DIR/processed ]; then
  echo "creating $DATA_DIR/processed directory"
  mkdir $DATA_DIR/processed
fi

# Check that AWS CLI is installed
if [ ! -d ~/.aws ]; then
  echo "AWS Command Line Interface not installed or configured."
  echo " Try:  $ pip install awscli --upgrade --user"
  echo " Then: $ aws configure"
  exit 1
fi
set -e

# Set the REGION if not set in env
: "${REGION:=1}"
echo "REGION set to: " $REGION
DATE=`date "+%m/%d/%Y"`
echo "DATE: " $DATE
S3_LOCATION="s3://streaming-tests/scans/"

# Create files until told to stop
while [ `tail -1 $STATUS_FILE | cut -d " " -f 1` != "STOP" ]
do
  # Get new sample data rows
  timestamp=`date "+%s"`
  scan_file="$DATA_DIR/temp/scans_${REGION}_${timestamp}.csv"
  curl --silent "https://my.api.mockaroo.com/region_scans?key=76b93870&region=${REGION}&date=${DATE}" > $scan_file
  echo $scan_file

  # upload files to S3 and archive
  for file in $DATA_DIR/temp/scans_${REGION}_*
  do
    aws s3 cp $file ${S3_LOCATION}
    mv $file $DATA_DIR/processed
  done
done
