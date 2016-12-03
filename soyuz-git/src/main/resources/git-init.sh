#!/bin/bash

#http://stackoverflow.com/questions/600079/is-there-any-way-to-clone-a-git-repositorys-sub-directory-only
#./git-init-part.sh -d /tmp/git-checkout/test/ -u git@github.com:dadmin/other.git -s spider/src/main/groovy -k /home/fbelov/tmp/ssh/id_rsa

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -u|--url)
    URL="$2"
    shift # past argument
    ;;
    -d|--dir)
    DIR="$2"
    shift # past argument
    ;;
    -k|--key)
    SSH_KEY="$2"
    shift # past argument
    ;;
    -b|--branch)
    BRANCH="$2"
    shift # past argument
    ;;
    -s|--sparse)
    SPARSE_DIR="$2"
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

echo URL  = "${URL}"
echo DIR     = "${DIR}"
echo SSH_KEY     = "${SSH_KEY}"
echo BRANCH     = "${BRANCH}"
echo SPARSE_DIR     = "${SPARSE_DIR}"
echo CURRENT DIR = "${CURRENT_DIR}"

if [[ -z "$DIR" ]]; then
  exit 1
fi

if [[ -z "$BRANCH" ]]; then
  BRANCH="master"
fi

#custom key support
export GIT_SSH="${CURRENT_DIR}/git-ssh.sh"

#empty target directory
rm -fr "${DIR}"

#init repo
git init "${DIR}"
cd "${DIR}"
git remote add origin "${URL}"

if [ -n "$SPARSE_DIR" ]; then
  #checkout part of repository
  git config core.sparsecheckout true
  echo "${SPARSE_DIR}/*" >> .git/info/sparse-checkout
fi

if [ -n "$SSH_KEY" ]; then
  #pull changes
  PKEY="${SSH_KEY}" git pull --depth=1 origin "${BRANCH}"
fi