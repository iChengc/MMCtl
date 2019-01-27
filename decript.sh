#!/bin/bash
# Remove character ' and change '~' to absolute path
daname=`echo $2 | sed "s/'//g" | sed 's?^~?'"$HOME"'?' | awk -F '/' '{print $NF}'`

echo $daname

echo "Decrypting $daname using key $1"
echo "PRAGMA key = '$1';PRAGMA cipher_use_hmac = OFF;PRAGMA cipher_page_size = 1024;PRAGMA kdf_iter = 4000;select count(*) from sqlite_master;ATTACH DATABASE 'decrypt-$daname' AS plaintext KEY '';SELECT sqlcipher_export('plaintext');DETACH DATABASE plaintext;" | sqlcipher $2
echo "Done."
