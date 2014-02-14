import os
import requests
import bs4
import re
import glob
import subprocess
import urllib, urllib2
import json
import argparse


parser = argparse.ArgumentParser()
parser.add_argument("contest_id", nargs = 1, type = int, help = "Hust Judge Contest ID - Required")
parser.add_argument("judge_id", nargs = 1, type = str, help = "Hust Judge ID - Required")
parser.add_argument("prob_id", nargs = 1, type = int, help = "Hust Judge Prob ID - Required")
parser.add_argument("prob_letter", nargs = 1, type = str, help = "Problem Letter in Contest - Required")

args = parser.parse_args()

homework_dir = os.path.join("homework",str(args.contest_id[0]))

def parse_language(lang):
    res = "unknown"
    lang = lang.lower()
    if "c++" in lang or "g++" in lang:
        res = "cpp"
    elif "java" in lang or "JAVA" in lang:
        res = "java"
    elif "c" in lang or "C" in lang:
        res = "cpp"
    print lang, " parses to ", res
    return res

def get_samples(judge_id, prob_id, contest_id, students_files = [], max_downloads = -1):


    session = requests.session()
    print session.post("http://acm.hust.edu.cn/vjudge/user/login.action",
            data = {"username" : "CS480Admin" , "password" : "1491625"} ).text

#get list of sample solutions
    sample_data = {"sEcho":"2", "iColumns":"15", "sColumns":"", "iDisplayStart":"0", "iDisplayLength":"9999999", "mDataProp_0":"0", "mDataProp_1":"1", "mDataProp_2":"2", "mDataProp_3":"3", "mDataProp_4":"4", "mDataProp_5":"5", "mDataProp_6":"6", "mDataProp_7":"7", "mDataProp_8":"8", "mDataProp_9":"9", "mDataProp_10":"10", "mDataProp_11":"11", "mDataProp_12":"12", "mDataProp_13":"13", "mDataProp_14":"14", "un":"", "OJId":judge_id, "probNum":str(prob_id), "res":"1"} #res 1 = accepted
    sample_headers = { "User-Agent": "Mozilla/5.0"}
    sample_res = session.post("http://acm.hust.edu.cn/vjudge/problem/fetchStatus.action", data=sample_data, headers=sample_headers)
    sample_solutions =  sample_res.json()["aaData"]


#save sample solutions that we can access
    sample_solution_dir = os.path.join(homework_dir, judge_id+str(prob_id))
    if not os.path.exists(sample_solution_dir):
        os.makedirs(sample_solution_dir)

    for sol in sample_solutions:
        sample_solution_res = session.get("http://acm.hust.edu.cn/vjudge/problem/viewSource.action?id="+str(sol[0]))
        sample_solution_soup = bs4.BeautifulSoup(sample_solution_res.text)
        sample_solution_filename = sol[1]+"."+parse_language(sol[6])
        sample_solution_filename_student = sol[1]+"_"+args.prob_letter[0]+"_"+str(args.contest_id[0])+"."+parse_language(sol[6])
        sample_solution_path = os.path.join(sample_solution_dir, sample_solution_filename)
        path_if_student = os.path.join(homework_dir, sample_solution_filename_student)

        if sample_solution_soup.pre and not str(path_if_student) in students_files and not max_downloads == 0:
            max_downloads -= 1
            print "Downloading", sample_solution_filename, "...",
            sample_solution_file = open(sample_solution_path, "w")
            sample_solution_file.write(sample_solution_soup.pre.contents[0].encode("utf-8"))
            sample_solution_file.close()
            print "Done."

    session.close()
    moss_filter(50, sample_solution_dir, "java")
    moss_filter(50, sample_solution_dir, "cpp")
    #moss_filter(50, sample_solution_dir, "c")


def moss_filter(percentage_threshhold, directory, language):
#upload all files in directory with given language to moss, delete any with match higher than threshhold
    try:
        files = glob.glob(os.path.join(directory,"*."+language))
        moss_output = subprocess.check_output(["./moss", "-l", "c","-m",str(len(files))]+files).split("\n")
        moss_output.reverse()
    except Exception, e:
        print "Moss Filter Failed for "+language+"!"
        return

    moss_res = requests.get(moss_output[1])
    moss_soup = bs4.BeautifulSoup(moss_res.text)
    moss_matches = moss_soup.findAll("td")
    for match in moss_matches:
        if "<td align=" in str(match):
            moss_matches.remove(match)
    for index in range(0,len(moss_matches)/2):
        try:
            file1_path = re.search(str(directory)+".*\."+language,str(moss_matches[2*index-2])).group(0)
            file2_path = re.search(str(directory)+".*\."+language,str(moss_matches[2*index-1])).group(0)
            file1_percentage = re.search("[0-9]+",re.search("\(.*\)",str(moss_matches[2*index-2])).group(0)).group(0)
            file2_percentage = re.search("[0-9]+",re.search("\(.*\)",str(moss_matches[2*index-1])).group(0)).group(0)
            if int(file1_percentage) > percentage_threshhold or int(file2_percentage) > percentage_threshhold:
                if int(file1_percentage) > int(file2_percentage):
                    if os.path.exists(file1_path):
                        os.remove(str(file1_path))
                        print "deleting", file1_path, file1_percentage
                else:
                    if os.path.exists(file2_path):
                       os.remove(file2_path)
                       print "deleting", file2_path, file2_percentage

        except:
            print "Something didn't match in the " + language + " moss output!"
            print index*2-2, moss_matches[2*index-2]
            print index*2-1, moss_matches[2*index-1]

get_samples(args.judge_id[0], args.prob_id[0], args.contest_id[0], students_files = glob.glob(os.path.join(str(homework_dir),"*")), max_downloads = 100)

