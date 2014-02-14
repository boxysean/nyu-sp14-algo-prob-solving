#!/bin/python

# +------------------------------------------------+
# | check_homework.py                              |
# |------------------------------------------------|
# | Check the correctly submitted programs against |
# | each other, find if there are similarlities    |
# | worth getting people in trouble for!           |
# +------------------------------------------------+

import glob
import subprocess
import sys
from bs4 import BeautifulSoup
import urllib, urllib2
import simplejson as json
import os
import webbrowser
import argparse
import requests

parser = argparse.ArgumentParser()
parser.add_argument("contest_id", nargs = 1, type = int, help = "Hust Judge Contest ID - Required")
parser.add_argument( "--moss", dest = "moss", action = "store_true")
parser.add_argument( "--no-moss", dest = "moss", action = "store_false")
parser.add_argument( "--download", dest = "download", action = "store_true")
parser.add_argument( "--no-download", dest = "download", action = "store_false")
parser.set_defaults(moss = True, download = True)

contest_id = parser.parse_args().contest_id[0]

results = {}
moss_urls = {}

def parse_language(lang):
	res = "unknown"
	lang = lang.lower()
	if "c++" in lang or "g++" in lang:
		res = "cpp"
	elif "java" in lang or "JAVA" in lang:
		res = "java"
	elif "c" in lang or "C" in lang:
		res = "cpp"
	print lang, "parses to", res
	return res

# Make a folder for this homework set

homework_dir = os.path.join("homework", str(contest_id))

if not os.path.exists(homework_dir):
    os.makedirs(homework_dir)



def download_solutions():

# Download website
#url = "http://acm.hust.edu.cn/vjudge/contest/view.action?cid=38690#status"
    url = "http://acm.hust.edu.cn/vjudge/contest/fetchStatus.action?cid=" + str(contest_id)

    values = { "sEcho": 1, "iColumns": "13", "sColumns": "", "iDisplayStart": "0", "iDisplayLength": "999999", "mDataProp_0": "0", "mDataProp_1": "1", "mDataProp_2": "2", "mDataProp_3": "3", "mDataProp_4": "4", "mDataProp_5": "5", "mDataProp_6": "6", "mDataProp_7": "7", "mDataProp_8": "8", "mDataProp_9": "9", "mDataProp_10": "10", "mDataProp_11": "11", "mDataProp_12": "12", "un": "", "num": "-", "res": "0" }

# This header is required, otherwise no response!
    headers = { "User-Agent": "Mozilla/5.0" }

    data = urllib.urlencode(values)

# Send away the request for all the submissions

    req = urllib2.Request(url, data, headers)
    res = urllib2.urlopen(req)
    content = res.read()

    submissions = json.loads(content)["aaData"]
    print "Number of submissions:", len(submissions)
#print json.dumps(submissions, sort_keys=True, indent=4 * ' ')

#content = urllib2.urlopen(url).read()
#soup = BeautifulSoup(content)
#print soup.prettify()

# Filter only the accepted answers

    accepted = filter(lambda x: x[3] == "Accepted", submissions)
    print "Number of accepted:", len(accepted)
#print json.dumps(accepted, sort_keys=True, indent=4 * ' ')

# Get the last accepted answer from each user
# It's already sorted in decreasing order, so just pick the first one for a particular problem and filter the rest

    answers = {}

#use requests.session to keep cookies

    session = requests.session()
    print session.post("http://acm.hust.edu.cn/vjudge/user/login.action",
            data = {"username" : "CS480Admin" , "password" : "1491625"} ).text

    for a in accepted:
        submission_id = a[0]
        name = a[1]
        problem_id = a[2]
        language = parse_language(a[6])
        if (name, problem_id) not in answers:
            answers[(name, problem_id)] = (submission_id, language)

    print "Number of non-duplicate answers:", len(answers)


# Download all the accepted answers

    for (name, problem_id), (submission_id, language) in answers.iteritems():
        print name, problem_id, submission_id
        if name not in results:
            results[name] = []
        results[name] += str(problem_id)

        solution_url = "http://acm.hust.edu.cn/vjudge/contest/viewSource.action?id=" + str(submission_id)
        #solution_headers = { "User-Agent": "Mozilla/5.0" }

        #req = urllib2.Request(solution_url, headers=solution_headers)
        #res = urllib2.urlopen(req)
        #content = res.read()

        req = session.get(solution_url)
        content = req.text

        soup = BeautifulSoup(content)
        solution_code = soup.pre.contents[0]

        solution_file_path = os.path.join(homework_dir, str(name) + "_" + str(problem_id) + "_" + str(contest_id) + "." + str(language))
        solution_file = open(solution_file_path, "w")
        solution_file.write(solution_code.encode("utf-8"))
        solution_file.close()

    session.close()


# Run moss
def upload_moss(language):

    moss_output = []
    moss_lang = language
    if moss_lang is "cpp":
        moss_lang = "cc"

    try:
        print "Mossing ",language,":"
        output = subprocess.check_output(["./moss", "-l", moss_lang]+glob.glob(os.path.join(homework_dir,"*."+language))).split("\n")
        combined_output = subprocess.check_output(["./moss", "-l", moss_lang]+glob.glob(os.path.join(homework_dir,"*","*."+language))+glob.glob(os.path.join(homework_dir,"*."+language))).split("\n")

        print "mossed"
        output.reverse()
        combined_output.reverse()
        moss_output += [output[1]] + [combined_output[1]]
        print "added"
        moss_urls[language] = output[1]
        moss_urls[language +" with samples"] = combined_output[1]

    except Exception, e:
        print "Moss Failed for "+language+"!"

#print parser.parse_args()

if parser.parse_args().download:
    download_solutions()

if parser.parse_args().moss:
    upload_moss("java")
    upload_moss("cpp")
    upload_moss("c")

student_keylist = results.keys()
student_keylist.sort()
url_keylist = moss_urls.keys()
url_keylist.sort()

output_file = open(os.path.join(homework_dir,"results.txt"),"w")
output_file.write("CS80S14 Grader Results For Contest " + str(contest_id)+"\n\nMoss Results\n")

for key in url_keylist:
    output_file.write(key + ": " + moss_urls[key] + "\n")

output_file.write("\nStudent Results\n")
for student in student_keylist:
    output_file.write(student + " " + str(len(results[student]))+"\n")
