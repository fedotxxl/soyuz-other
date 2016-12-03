#!/bin/sh
#https://alvinabad.wordpress.com/2013/03/23/how-to-specify-an-ssh-key-file-with-the-git-command/
#http://stackoverflow.com/questions/4565700/specify-private-ssh-key-to-use-when-executing-shell-command-with-or-without-ruby
#export GIT_SSH=~/ssh-git.sh
#PKEY=~/.ssh/thatuserkey.pem git clone thatuser@myserver.com:/git/repo.git

#http://debuggable.com/posts/disable-strict-host-checking-for-git-clone:49896ff3-0ac0-4263-9703-1eae4834cda3
mkdir -p $HOME/.ssh/

SSH_CONFIG_PATH=$HOME/.ssh/config
if [ ! -f $SSH_CONFIG_PATH ] || [ ! -n "$(grep "StrictHostKeyChecking" $SSH_CONFIG_PATH)" ]; then echo "StrictHostKeyChecking no\n" >> $SSH_CONFIG_PATH; fi

if [ -z "$PKEY" ]; then
  # if PKEY is not specified, run ssh using default keyfile
  ssh "$@"
else
  ssh -i "$PKEY" $1 $2
fi