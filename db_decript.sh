#!/bin/bash
# Remove character ' and change '~' to absolute path
#daname=`echo $1 | sed "s/'//g" | sed 's?^~?'"$HOME"'?' | awk -F '/' '{print $NF}'`

# cd to script working directory
script_dir=$(cd `dirname $0` && pwd)
cd $script_dir

if [ -z $1 ]; then
    echo "Please input db file path on the phone"
    exit 1
fi 

if [ -z $2 ]; then
    echo "Please input db decrypt key"
    exit 1
fi 

dbName=$(date +%s).db
adb pull $1 ./$dbName

daname=$(pwd)/$dbName
echo "Decrypting $dbName using key $2"
echo "PRAGMA key = '$2';PRAGMA cipher_use_hmac = OFF;PRAGMA cipher_page_size = 1024;PRAGMA kdf_iter = 4000;select count(*) from sqlite_master;ATTACH DATABASE 'decrypt-$dbName' AS plaintext KEY '';SELECT sqlcipher_export('plaintext');DETACH DATABASE plaintext;" | sqlcipher $daname
echo "Done."
