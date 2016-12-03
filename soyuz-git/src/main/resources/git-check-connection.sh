#!/bin/bash

#http://stackoverflow.com/questions/600079/is-there-any-way-to-clone-a-git-repositorys-sub-directory-only
#./git-init-part.sh -d /tmp/git-checkout/test/ -u git@github.com:dadmin/other.git -s spider/src/main/groovy -k /home/fbelov/tmp/ssh/id_rsa

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -d|--dir)
    DIR="$2"
    shift # past argument
    ;;
    -k|--key)
    SSH_KEY="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

#http://stackoverflow.com/questions/630372/determine-the-path-of-the-executing-bash-script
CURRENT_DIR="`dirname \"$0\"`"              # relative
CURRENT_DIR="`( cd \"$CURRENT_DIR\" && pwd )`"

echo DIR     = "${DIR}"
echo SSH_KEY = "${SSH_KEY}"

#custom key support
export GIT_SSH="${CURRENT_DIR}/git-ssh.sh"

cd "${DIR}"
PKEY="${SSH_KEY}" git ls-remote origin