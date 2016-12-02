#!/usr/bin/python

import sys
import os

my_maven_token = os.environ["MY_MAVEN_REPO_TOKEN"]
package_type = "deb"

op_command = sys.argv[1]
op_args = sys.argv[2:]

def artifact_to_url(artifact):
    splitted = artifact.split(":")
    if (len(splitted) != 3):
        raise Exception("Incorrect artifact structure. Should be group_id:artifact_id:version")
    url_prefix = "https://mymavenrepo.com/repo/" + my_maven_token + "/"
    artifact_group_id = splitted[0]
    artifact_group_id_fix = artifact_group_id.replace(".", "/")
    artifact_artifact_id = splitted[1]
    artifact_artifact_id_fix = artifact_artifact_id.replace(".", "/")
    artifact_version = splitted[2]

    artifact_url = [artifact_group_id_fix, artifact_artifact_id_fix, artifact_version]
    file_name = artifact_artifact_id + "-" + artifact_version + "." + package_type

    url = url_prefix + "/".join(artifact_url) + "/" + file_name

    return [url, file_name]


def install(args):
    [url, file_name] = artifact_to_url(args[0])

    print "Installing " + package_type  + " by url " + url

    if package_type == "rpm":
        subprocess.call(["rpm", "-i", url])
    elif package_type == "deb":
        os.system("cd /tmp && curl -O " + url + " && sudo dpkg -i " + file_name)

def info(args):
    [url, file_name] = artifact_to_url(args[0])

    print "Getting info for " + package_type  + " from " + url

    if package_type == "rpm":
        a = 1 #todo
    elif package_type == "deb":
        os.system("cd /tmp && curl -O " + url + " && sudo dpkg -I " + file_name)

if op_command == "install":
    install(op_args)
elif op_command == "info":
    info(op_args)
else:
    raise Exception("Unknown operation " + op_command)
